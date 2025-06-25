package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class ConfirmLostManager(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun confirmLostBook(
        requestId: String,
        readerId: String,
        copyId: String,
        bookId: String,
        fineAmount: Int,
        librarianId: String
    ) = withContext(Dispatchers.IO) {
        val now = Date()

        val batch = db.batch()

        // Tạo fine document
        val fine = Fine(
            requestId = requestId,
            readerId = readerId.toIntOrNull() ?: 0,
            fineAmount = fineAmount,
            copyId = copyId,
            bookId = bookId,
            reason = "Book lost"
        )
        val fineRef = db.collection("fine").document(requestId)
        batch.set(fineRef, fine)

        // Cập nhật trạng thái + người duyệt + thời gian cho lost_requests
        val lostRequestRef = db.collection("lost_requests").document(requestId)
        batch.update(lostRequestRef, mapOf(
            "status" to "CONFIRMED",
            "confirmedBy" to librarianId,
            "confirmedAt" to now
        ))

        // Cập nhật status trong book_copy
        val bookCopyRef = db.collection("books")
            .document(bookId)
            .collection("book_copy")
            .document(copyId)
        batch.update(bookCopyRef, mapOf("status" to CopyStatus.LOST.value))

        // Commit tất cả
        batch.commit().await()
    }
}