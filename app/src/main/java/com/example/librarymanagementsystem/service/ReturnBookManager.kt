package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ReturnBookManager(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun markAsReturnedBatch(borrow: BorrowBook, fineAmount: Int?, reason: String) = withContext(Dispatchers.IO) {
        val batch = db.batch()

        val bookBorrowRef = db.collection("borrow_book").document(borrow.requestId!!)
        val copyRef = db.collection("books").document(borrow.bookId!!).collection("book_copy").document(borrow.copyId!!)

        if (fineAmount != null && fineAmount > 0) {
            val newFineRef = db.collection("fine").document(borrow.requestId)
            val fine = Fine(
                requestId = borrow.requestId,
                readerId = borrow.readerId!!,
                fineAmount = fineAmount,
                copyId = borrow.copyId,
                bookId = borrow.bookId,
                reason = reason
            )
            batch.set(newFineRef, fine)
        }
        batch.update(bookBorrowRef, "actualReturnDate", FieldValue.serverTimestamp())
        batch.update(bookBorrowRef, "status", BorrowStatus.RETURNED.value)
        batch.update(copyRef, "status", CopyStatus.AVAILABLE.value)

        batch.commit().await()
    }
}