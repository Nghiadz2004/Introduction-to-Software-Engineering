package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Fine
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FineRepository(private val db: FirebaseFirestore) {

    suspend fun getFineByReader(readerId: String): Fine? = withContext(Dispatchers.IO) {
        db.collection("fines")
            .whereEqualTo("readerId", readerId)
            .get()
            .await()
            .toObjects(Fine::class.java)
            .firstOrNull()
    }

    suspend fun setFine(readerId: String, fineAmount: Int) = withContext(Dispatchers.IO) {
        val fineData = mapOf(
            "readerId" to readerId,
            "fineAmount" to fineAmount
        )
        db.collection("fines").document(readerId).set(fineData).await()
    }
}

