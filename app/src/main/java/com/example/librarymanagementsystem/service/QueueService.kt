package com.example.librarymanagementsystem.service

import android.util.Log
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.model.QueueDisplay
import com.example.librarymanagementsystem.model.User
import com.example.librarymanagementsystem.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QueueService(
    private val requestRepo: RequestBorrowRepository = RequestBorrowRepository(),
    private val bookRepo: BookRepository = BookRepository(),
    private val userRepo: UserRepository = UserRepository(),
    private val bookCopyRepo: BookCopyRepository = BookCopyRepository(),
    private val borrowRequestRepository: RequestBorrowRepository = RequestBorrowRepository(),
    private val borrowRepository: BorrowingRepository = BorrowingRepository(),
    private val borrowService: BorrowBookManager = BorrowBookManager(borrowRequestRepository, borrowRepository)
) {
    suspend fun getAllQueueDisplays(): List<QueueDisplay> = withContext(Dispatchers.IO) {
        val requests = requestRepo.getPendingRequests()

        val bookIds = requests.mapNotNull { it.bookId }.distinct()
        val userIds = requests.mapNotNull { it.readerId }.distinct()


        val books = bookRepo.getBooksByIds(bookIds)
        val users = userRepo.getUsers(userIds)
        val availableCopies = bookCopyRepo.getNumAvailableCopies(bookIds)

        val bookMap = books.associateBy { it.id }
        val userMap = users.associateBy { it.id }
        val daysBorrowMap = requests.associateBy({ it.id }, { it.daysBorrow })

        return@withContext requests.mapNotNull { request ->
            val book = bookMap[request.bookId] ?: return@mapNotNull null
            val user = userMap[request.readerId] ?: return@mapNotNull null
            val copyLeft = availableCopies[book.id] ?: 0
            val now = fetchServerTime()

            val canApprove = copyLeft > 0 &&
                    borrowService.canBorrowMore(request.readerId, now).ok

            QueueDisplay(
                coverUrl = book.cover,
                title = book.title,
                author = book.author ?: "Unknown",
                copyLeft = availableCopies[book.id]!!,
                readerName = user.fullname ?: "Reader",
                daysBorrow = if (daysBorrowMap[request.id] == 1) "1 day" else "${daysBorrowMap[request.id]} days",
                bookId = book.id!!,
                request = request,
                canApprove = canApprove
            )
        }
    }
}