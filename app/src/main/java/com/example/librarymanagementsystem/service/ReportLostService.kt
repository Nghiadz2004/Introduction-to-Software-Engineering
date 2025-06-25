package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.*
import com.example.librarymanagementsystem.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ReportLostService(
    private val lostRepo: LostBookRepository = LostBookRepository(),
    private val borrowRepo: BorrowingRepository = BorrowingRepository(),
    private val bookRepo: BookRepository = BookRepository(),
    private val userRepo: UserRepository = UserRepository()
) {
    suspend fun getAllLostDisplays(): List<LostDisplay> = withContext(Dispatchers.IO) {
        val lostRequests = lostRepo.getPendingRequests()
        val borrows = borrowRepo.getAllBorrowsByStatus(BorrowStatus.BORROWED.name)
        val books = bookRepo.getBooksByIds(lostRequests.mapNotNull { it.bookId })
        val users = userRepo.getUsers(lostRequests.mapNotNull { it.readerId })

        val borrowMap = borrows.associateBy { it.requestId }
        val bookMap = books.associateBy { it.id }
        val userMap = users.associateBy { it.id }

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        return@withContext lostRequests.mapNotNull { lost ->
            val book = bookMap[lost.bookId] ?: return@mapNotNull null
            val reader = userMap[lost.readerId] ?: return@mapNotNull null
            val borrow = borrowMap[lost.requestId] ?: return@mapNotNull null
            val confirmDate = borrow.confirmDate
            val dueDate = borrow.expectedReturnDate
            val fineAmount = book.price?.plus(20000) ?: 20000

            LostDisplay(
                book = book,
                readerName = reader.fullname,
                copyId = lost.copyId ?: "",
                formattedLoanDate = confirmDate?.let { dateFormat.format(it) } ?: "N/A",
                formattedDueDate = dueDate?.let { dateFormat.format(it) } ?: "N/A",
                fine = "${fineAmount} VNĐ",

                requestId = lost.requestId ?: "",
                readerId = lost.readerId ?: "",
                bookId = lost.bookId ?: "",
                fineAmount = fineAmount
            )
        }
    }
}