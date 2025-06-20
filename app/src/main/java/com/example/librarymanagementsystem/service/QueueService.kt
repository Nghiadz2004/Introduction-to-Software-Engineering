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
    private val bookCopyRepo: BookCopyRepository = BookCopyRepository()
) {
    suspend fun getAllQueueDisplays(): List<QueueDisplay> = withContext(Dispatchers.IO) {
        val requests = requestRepo.getPendingRequests()
        Log.d("QUEUE_SERVICE", "Found ${requests.size} pending requests")
        val bookIds = requests.mapNotNull { it.bookId }.distinct()
        val userIds = requests.mapNotNull { it.readerId }.distinct()

        val books = bookRepo.getBooksByIds(bookIds)
        val users = userRepo.getUsers()
        val bookCopies = bookCopyRepo.getAllBookCopies()

        val bookMap = books.associateBy { it.id }
        val userMap = users.associateBy { it.id }

        return@withContext requests.mapNotNull { request ->
            val book = bookMap[request.bookId] ?: return@mapNotNull null
            val user = userMap[request.readerId] ?: return@mapNotNull null
            val availableCopies = bookCopies.count {
                it.bookId == book.id && it.status == "Có sẵn"
            }
            QueueDisplay(
                coverUrl = book.cover,
                title = book.title ?: "Unknown",
                author = book.author,
                copyLeft = availableCopies,
                readerName = user.fullname ?: "Reader",
                status = if (availableCopies > 0) "available" else "out of Copy",
                bookId = book.id!!,
                request = request
            )
        }
    }
}