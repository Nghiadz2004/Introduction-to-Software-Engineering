package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.BorrowBook
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.model.User
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

data class LoanDisplay(
    val borrow: BorrowBook,
    val book: Book,
    val readerName: String,
    val librarianName: String,
    val formattedLoanDate: String,
    val formattedDueDate: String,
    val statusText: String
)

class LoanService(
    private val borrowingRepository: BorrowingRepository = BorrowingRepository(),
    private val bookRepository: BookRepository = BookRepository(),
    private val userRepository: UserRepository = UserRepository()
) {
    suspend fun getAllLoanDisplays(): List<LoanDisplay> = withContext(Dispatchers.IO) {
        val borrows = borrowingRepository.getAllBorrows()
        val books = bookRepository.getBooksByIds(borrows.mapNotNull { it.bookId }.distinct())
        val users = userRepository.getUsers(borrows.mapNotNull { it.readerId }.distinct())

        val bookMap = books.associateBy { it.id }
        val userMap = users.associateBy { it.id }

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val today = Date()

        return@withContext borrows.mapNotNull { borrow ->
            val book = bookMap[borrow.bookId] ?: return@mapNotNull null
            val reader = userMap[borrow.readerId] ?: return@mapNotNull null
            val librarian = userMap[borrow.librarianId] ?: return@mapNotNull null

            val dueDate = borrow.expectedReturnDate ?: return@mapNotNull null
            val daysDiff = ((dueDate.time - today.time) / (1000 * 60 * 60 * 24)).toInt()
            val status = if (daysDiff >= 0) "$daysDiff days left" else "overdue ${-daysDiff} days"

            LoanDisplay(
                borrow = borrow,
                book = book,
                readerName = reader.fullname ?: "Reader",
                librarianName = librarian.fullname ?: "Librarian",
                formattedLoanDate = borrow.borrowDate?.let { dateFormat.format(it) } ?: "N/A",
                formattedDueDate = dateFormat.format(dueDate),
                statusText = status
            )
        }
    }
}