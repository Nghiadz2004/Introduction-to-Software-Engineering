package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.*
import com.example.librarymanagementsystem.repository.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ReturnBookService(
    private val borrowingRepo: BorrowingRepository = BorrowingRepository(),
    private val bookRepo: BookRepository = BookRepository(),
    private val userRepo: UserRepository = UserRepository(),
    private val lostRepo: LostBookRepository = LostBookRepository()
) {
    suspend fun getAllReturnDisplays(): List<ReturnDisplay> = withContext(Dispatchers.IO) {
        val allBorrows = borrowingRepo.getAllBorrows()
        val lostCopies = lostRepo.getPendingRequests().mapNotNull { it.copyId }.toSet()
        val returnBorrows = allBorrows.filter { it.actualReturnDate == null && it.copyId !in lostCopies }

        val books = bookRepo.getBooksByIds(returnBorrows.mapNotNull { it.bookId }.distinct())
        val users = userRepo.getUsers(returnBorrows.mapNotNull { it.readerId }.distinct())

        val bookMap = books.associateBy { it.id }
        val userMap = users.associateBy { it.id }

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val today = Date()

        return@withContext returnBorrows.mapNotNull { borrow ->
            val book = bookMap[borrow.bookId] ?: return@mapNotNull null
            val reader = userMap[borrow.readerId] ?: return@mapNotNull null
            val dueDate = borrow.expectedReturnDate ?: return@mapNotNull null
            val borrowDate = borrow.borrowDate ?: return@mapNotNull null

            val days = ((today.time - dueDate.toDate().time) / (1000 * 60 * 60 * 24)).toInt()
            val statusText = if (days <= 0) "Due in ${-days} days" else "$days days overdue"
            val fineText = if (days <= 0) "Fine: No" else "Fine: ${days * 1000}đ"

            ReturnDisplay(
                book = book,
                borrow = borrow,
                readerName = reader.fullname,
                formattedBorrowDate = dateFormat.format(borrowDate),
                formattedDueDate = dateFormat.format(dueDate),
                statusText = statusText,
                fineText = fineText
            )
        }
    }
}