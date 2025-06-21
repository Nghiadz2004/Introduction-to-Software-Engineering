package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.model.BorrowBook
import com.example.librarymanagementsystem.model.LostBook
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
    suspend fun getReaderBorrowingBooks(readerId: String): Map<Book, BorrowBook> = withContext(Dispatchers.IO) {
        val borrowList = borrowingRepository.getBorrowBooksByReader(readerId)
        val lostList = lostBookRepository.getReaderPendingRequests(readerId)

        val lostBookIds = lostList.map { it.bookId }.toSet()
        val validBorrowList = borrowList.filterNot { it.bookId in lostBookIds }
        val bookIds = validBorrowList.map { it.bookId }

        val books = bookRepository.getBooksByIds(bookIds.filterNotNull())

        return@withContext validBorrowList.mapNotNull { borrow ->
            val book = books.find { it.id == borrow.bookId }
            book?.let { it to borrow }
        }.toMap()
    }


    // Lấy danh sách các quyển sách đang được người dùng báo mất để hiển thị trong section "My Book"
    suspend fun getReaderPendingLosts(readerId: String): List<Book> = withContext(Dispatchers.IO) {
        val lost = lostBookRepository.getReaderPendingRequests(readerId)
        val bookIds = lost.map {it.bookId}
        return@withContext bookRepository.getBooksByIds(bookIds)
    }

    // Người dùng báo cáo với thủ thư sách bị mất để chờ được xác nhận
    suspend fun submitLostRequest(requestId: String, readerId: String, bookId: String, copyId: String): String = withContext(Dispatchers.IO){
        val lostRequest = LostBook(
            requestId = requestId,
            bookId = bookId,
            copyId = copyId,
            readerId = readerId)

        return@withContext lostBookRepository.submitLostRequest(lostRequest)
    }

    suspend fun cancelLostRequest(bookId: String, readerId: String) = withContext(Dispatchers.IO) {

        return@withContext lostBookRepository.cancelLostRequestByCopyID(bookId, readerId)
    }

    suspend fun cancelPendingRequest(bookId: String, readerId: String) = withContext(Dispatchers.IO) {

        return@withContext requestBorrowRepository.cancelPendingRequest(bookId, readerId)
    }
}