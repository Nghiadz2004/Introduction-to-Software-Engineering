package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.*
import com.example.librarymanagementsystem.repository.BookCopyRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class ReturnBookManager(
    private val borrowingRepo: BorrowingRepository = BorrowingRepository(),
    private val bookCopyRepo: BookCopyRepository = BookCopyRepository(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun markAsReturned(borrow: BorrowBook, fineAmount: Int?, reason: String) = withContext(Dispatchers.IO) {
        val now = Date()

        // 1. Cập nhật actualReturnDate trong borrow_book
        val borrowDocId = borrow.requestId ?: return@withContext
        db.collection("borrow_book")
            .document(borrowDocId)
            .update("actualReturnDate", now)
            .await()

        // 2. Nếu có fine -> tạo Fine document mới
        if (fineAmount != null && fineAmount > 0) {
            val fine = Fine(
                requestId = borrow.requestId ?: borrowDocId,
                readerId = borrow.readerId?.toIntOrNull() ?: 0,
                fineAmount = fineAmount,
                copyId = borrow.copyId ?: "",
                bookId = borrow.bookId ?: "",
                reason = reason
            )
            db.collection("fine").document(fine.requestId).set(fine).await()
        }

        // 3. Cập nhật status copy thành AVAILABLE
        val copyId = borrow.copyId ?: return@withContext
        db.collection("book_copy")
            .document(copyId)
            .update("status", CopyStatus.AVAILABLE.value)
            .await()
    }
}