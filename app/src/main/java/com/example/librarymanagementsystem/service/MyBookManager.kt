package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.cache.BookOperateCache
import com.example.librarymanagementsystem.cache.LibraryCardCache
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.model.BorrowBook
import com.example.librarymanagementsystem.model.BorrowStatus
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
    suspend fun getReaderPendingBooks(): List<Book> = withContext(Dispatchers.IO) {
        val card = LibraryCardCache.libraryCard ?: return@withContext emptyList()

        val pendingList = requestBorrowRepository.getReaderPendingRequests(card.requestId)

        val bookIds = pendingList.map { it.bookId }

        // 1. Thêm (hoặc ghi đè) tất cả phần tử trong bookIds với value = "PENDING"
        for (id in bookIds) {
            BookOperateCache.statusMap[id] = "PENDING"
        }

        // 2. Xoá tất cả phần tử có value == "PENDING" mà không nằm trong bookIds
            BookOperateCache.statusMap.entries.removeIf { (key, value) ->
            value == "PENDING" && key !in bookIds
        }

        return@withContext bookRepository.getBooksByIds(bookIds.toList())
    }

    // Lấy danh sách các quyển sách đang được người dùng mượn để hiển thị trong section "My Book"
    suspend fun getReaderBorrowingBooks(): Map<Book, BorrowBook> = withContext(Dispatchers.IO) {
        val card = LibraryCardCache.libraryCard ?: return@withContext emptyMap()

        val borrowList = borrowingRepository.getBorrowBooksByCardAndStatus(card.requestId,BorrowStatus.BORROWED.name)

        val bookIds = borrowList.map { it.bookId!! }
        val books = bookRepository.getBooksByIds(bookIds)

        // 1. Thêm (hoặc ghi đè) tất cả phần tử trong bookIds với value = "BORROWED"
        for (id in bookIds) {
            BookOperateCache.statusMap[id] = "BORROWED"
        }

        // 2. Xoá tất cả phần tử có value == "BORROWED" mà không nằm trong bookIds
        BookOperateCache.statusMap.entries.removeIf { (key, value) ->
            value == "BORROWED" && key !in bookIds
        }

        return@withContext borrowList.mapNotNull { borrow ->
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