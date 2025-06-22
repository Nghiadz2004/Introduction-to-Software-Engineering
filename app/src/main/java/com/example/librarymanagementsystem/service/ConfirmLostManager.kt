package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.*
import com.example.librarymanagementsystem.repository.BookCopyRepository
import com.example.librarymanagementsystem.repository.LostBookRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class ConfirmLostManager(
    private val lostRepo: LostBookRepository = LostBookRepository(),
    private val bookCopyRepo: BookCopyRepository = BookCopyRepository(),
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

        // 1. Tạo fine document
        val fine = Fine(
            requestId = requestId,
            readerId = readerId.toIntOrNull() ?: 0,
            fineAmount = fineAmount,
            copyId = copyId,
            bookId = bookId,
            reason = "Book lost"
        )
        db.collection("fine").document(requestId).set(fine).await()

        // 2. Cập nhật người duyệt và thời gian duyệt
        lostRepo.updateLostRequestStatus(requestId, librarianId)

        // 3. Cập nhật trạng thái copy thành LOST
        db.collection("book_copy")
            .document(copyId)
            .update("status", CopyStatus.LOST.value)
            .await()
    }
}