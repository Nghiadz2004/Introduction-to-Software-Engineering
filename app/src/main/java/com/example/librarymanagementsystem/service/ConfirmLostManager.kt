package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
        val batch = db.batch()

        // 1. Tạo fine document
        val fineRef = db.collection("fine").document(requestId)
        val fine = Fine(
            requestId = requestId,
            readerId = readerId,
            fineAmount = fineAmount,
            copyId = copyId,
            bookId = bookId,
            reason = "Book lost",
        )
        batch.set(fineRef, fine)

        // 2. Cập nhật status và librarianId trong request_lost
        val lostRequestRef = db.collection("lost_requests").document(requestId)
        batch.update(lostRequestRef, mapOf(
            "status" to LostRequestStatus.CONFIRMED.value,
            "confirmedBy" to librarianId,
            "confirmedAt" to FieldValue.serverTimestamp()
        ))

        // 3. Cập nhật trạng thái copy thành LOST
        val copyRef = db.collection("books").document(bookId).collection("book_copy").document(copyId)
        batch.update(copyRef, "status", CopyStatus.LOST.value)

        val borrowBookRef = db.collection("borrow_book").document(requestId)
        batch.update(borrowBookRef, "status", BorrowStatus.LOST.value)

        // Commit toàn bộ batch
        batch.commit().await()
    }

}