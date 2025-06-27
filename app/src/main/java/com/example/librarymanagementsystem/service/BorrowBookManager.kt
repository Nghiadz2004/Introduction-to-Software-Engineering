package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.BorrowBook
import com.example.librarymanagementsystem.model.BorrowRequest
import com.example.librarymanagementsystem.model.BorrowStatus
import com.example.librarymanagementsystem.model.RequestStatus
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.repository.RequestBorrowRepository
import com.example.librarymanagementsystem.repository.fetchServerTime
import java.util.Calendar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BorrowBookManager (
    private val borrowRequestRepository: RequestBorrowRepository,
    private val borrowRepository: BorrowingRepository
) {
    val db = FirebaseFirestore.getInstance()

    // Xác nhận yêu cầu mượn sách của người dùng
    suspend fun approveBorrowRequestBatch(request: BorrowRequest, librarianId: String, copyId: String) = withContext(
        Dispatchers.IO) {
        val batch = db.batch()

        val newBorrowRef = db.collection("borrow_book").document(request.id!!)
        val copyRef = db.collection("books").document(request.bookId).collection("book_copy").document(copyId)
        val requestRef = db.collection("request_borrow").document(request.id)

        val confirmDate = fetchServerTime()
        val expectedReturnDate = if (confirmDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = confirmDate
            calendar.add(Calendar.DAY_OF_YEAR, request.daysBorrow + 1) // Cộng số ngày mượn vào borrowDate
            calendar.time // Trả về ngày sau khi cộng
        } else {
            null // Trường hợp borrowDate null thì trả về null
        }

        val borrowBook = BorrowBook(
            requestId = request.id,
            libraryCardId = request.libraryCardId,
            copyId = copyId,
            readerId = request.readerId,
            bookId = request.bookId,
            borrowDate = request.borrowDate,
            librarianId = librarianId,
            expectedReturnDate = expectedReturnDate, // Sử dụng ngày đã cộng
            status = BorrowStatus.BORROWED.name,
            category = request.category
        )


        batch.set(newBorrowRef, borrowBook)
        batch.update(requestRef, "status", RequestStatus.APPROVED.name)
        batch.update(copyRef, "status", "BORROWED")

        batch.commit().await() // Dùng coroutine extension
    }

    // Từ chối yêu cầu mượn sách của người dùng
    suspend fun rejectBorrowRequest(request: BorrowRequest, librarianId: String) = withContext(
        Dispatchers.IO) {
        borrowRequestRepository.updateRequestBorrowStatus(request.id!!, RequestStatus.REJECTED)
    }
}