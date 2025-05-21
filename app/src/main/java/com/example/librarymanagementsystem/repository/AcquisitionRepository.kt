package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.BookAcquisition
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AcquisitionRepository(private val db: FirebaseFirestore) {

    suspend fun recordAcquisition(acquisition: BookAcquisition): String = withContext(Dispatchers.IO) {
        db.collection("book_acquisitions").add(acquisition).await().id
    }

    suspend fun getAcquisitionByBook(bookId: String): List<BookAcquisition> = withContext(
        Dispatchers.IO) {
        db.collection("book_acquisition")
            .whereEqualTo("bookId", bookId)
            .get()
            .await()
            .toObjects(BookAcquisition::class.java)
    }
}
