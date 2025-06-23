package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.BorrowBook
import com.example.librarymanagementsystem.model.BorrowRequest
import com.example.librarymanagementsystem.model.RequestStatus
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.repository.RequestBorrowRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
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

        val newBorrowRef = db.collection("borrow_book").document()
        val copyRef = db.collection("books").document(request.bookId).collection("book_copy").document(copyId)
        val requestRef = db.collection("request_borrow").document(request.id!!)

        val borrowBook = BorrowBook(
            requestId = request.id,
            libraryCardId = request.libraryCardId,
            copyId = copyId,
            readerId = request.readerId,
            bookId = request.bookId,
            borrowDate = request.borrowDate,
            librarianId = librarianId,
            confirmDate = Timestamp.now(),
            expectedReturnDate = Timestamp.now()
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