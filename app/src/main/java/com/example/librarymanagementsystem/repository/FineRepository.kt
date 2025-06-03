package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Fine
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FineRepository(private val db: FirebaseFirestore) {
    suspend fun addFine(fine: Fine): String = withContext(Dispatchers.IO) {
        val docRef = db.collection("fines").add(fine).await()
        docRef.id
    }

    suspend fun getFines(): List<Fine> = withContext(Dispatchers.IO) {
        db.collection("fines").get().await().toObjects(Fine::class.java)
    }

    suspend fun getFineByReader(readerId: String): List<Fine> = withContext(Dispatchers.IO) {
        db.collection("fines")
            .whereEqualTo("readerId", readerId)
            .get()
            .await()
            .toObjects(Fine::class.java)
    }

    suspend fun getFineByRequest(requestId: String): List<Fine> = withContext(Dispatchers.IO) {
        db.collection("fines")
            .whereEqualTo("requestId", requestId)
            .get().await()
            .toObjects(Fine::class.java)
    }

    suspend fun getFineByCopy(copyId: String): List<Fine> = withContext(Dispatchers.IO) {
        db.collection("fines")
            .whereEqualTo("copyId", copyId)
            .get().await()
            .toObjects(Fine::class.java)
    }

    suspend fun getFineByBook(bookId: String): List<Fine> = withContext(Dispatchers.IO) {
        db.collection("fines")
            .whereEqualTo("bookId", bookId)
            .get().await()
            .toObjects(Fine::class.java)
    }
}

