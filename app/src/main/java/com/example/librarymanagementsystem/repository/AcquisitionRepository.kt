package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.model.BookAcquisition
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class AcquisitionRepository(private val db: FirebaseFirestore) {

    suspend fun recordAcquisition(acquisition: BookAcquisition): String = withContext(Dispatchers.IO) {
        db.collection("book_acquisitions").add(acquisition).await().id
    }

    suspend fun getAcquisitions(): List<BookAcquisition> = withContext(Dispatchers.IO) {
        db.collection("book_acquisitions").get().await().toObjects(BookAcquisition::class.java)
    }

    suspend fun getAcquisitionByCopy(copyId: String): List<BookAcquisition> = withContext(
        Dispatchers.IO) {
        db.collection("book_acquisition")
            .whereEqualTo("copyId", copyId)
            .get()
            .await()
            .toObjects(BookAcquisition::class.java)
    }

    suspend fun getAcquisitionByBook(bookId: String): List<BookAcquisition> = withContext(
        Dispatchers.IO) {
        db.collection("book_acquisition")
            .whereEqualTo("bookId", bookId)
            .get()
            .await()
            .toObjects(BookAcquisition::class.java)
    }

    suspend fun getAcquisitionByDate(acquisitionDate: Date): List<BookAcquisition> = withContext(
        Dispatchers.IO) {
        db.collection("book_acquisition")
            .whereEqualTo("acquisitionDate", acquisitionDate)
            .get()
            .await()
            .toObjects(BookAcquisition::class.java)
    }

    suspend fun getAcquisitionByRecorder(recorderId: String): List<BookAcquisition> = withContext(
        Dispatchers.IO) {
        db.collection("book_acquisition")
            .whereEqualTo("acquiredBy", recorderId)
            .get()
            .await()
            .toObjects(BookAcquisition::class.java)
    }
}
