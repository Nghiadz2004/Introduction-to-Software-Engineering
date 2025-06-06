package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.repository.LostBookRepository
import com.example.librarymanagementsystem.repository.RequestBorrowRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyBookManager(
    private val requestBorrowRepository: RequestBorrowRepository = RequestBorrowRepository(),
    private val bookRepository: BookRepository = BookRepository(),
    private val borrowingRepository: BorrowingRepository = BorrowingRepository(),
    private val lostBookRepository: LostBookRepository = LostBookRepository()
) {
    // Lấy danh sách các quyển sách đang được người dùng yêu cầu mượn để hiển thị trong section "My Book"
    suspend fun getReaderPendingBooks(readerId: String): List<Book> = withContext(Dispatchers.IO) {
        val request = requestBorrowRepository.getReaderPendingRequests(readerId)
        val bookIds = request.map {it.bookId}
        return@withContext bookRepository.getBooksByIds(bookIds)
    }

    // Lấy danh sách các quyển sách đang được người dùng mượn để hiển thị trong section "My Book"
    suspend fun getReaderBorrowingBooks(readerId: String): List<Book> = withContext(Dispatchers.IO) {
        val borrow = borrowingRepository.getBorrowBooksByReader(readerId)
        val bookIds = borrow.map {it.bookId}
        return@withContext bookRepository.getBooksByIds(bookIds)
    }

    // Lấy danh sách các quyển sách đang được người dùng báo mất để hiển thị trong section "My Book"
    suspend fun getReaderPendingLosts(readerId: String): List<Book> = withContext(Dispatchers.IO) {
        val lost = lostBookRepository.getReaderPendingRequests(readerId)
        val bookIds = lost.map {it.bookId}
        return@withContext bookRepository.getBooksByIds(bookIds)
    }
}