package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.LostBook
import com.example.librarymanagementsystem.model.LostRequestStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class LostBookRepository(private val db: FirebaseFirestore) {
    suspend fun submitLostRequest(request: LostBook): String = withContext(Dispatchers.IO) {
        db.collection("lost_requests").add(request).await().id
    }

    suspend fun getPendingRequests(): List<LostBook> = withContext(Dispatchers.IO) {
        db.collection("lost_requests")
            .whereEqualTo("status", LostRequestStatus.PENDING.value)
            .get()
            .await()
            .toObjects(LostBook::class.java)
    }

    suspend fun getReaderPendingRequests(readerId: String): List<LostBook> = withContext(Dispatchers.IO) {
        db.collection("lost_requests")
            .whereEqualTo("status", LostRequestStatus.PENDING.value)
            .whereEqualTo("readerId", readerId)
            .get()
            .await()
            .toObjects(LostBook::class.java)
    }

    suspend fun updateLostRequestStatus(
        requestId: String,
        librarianId: String
    ) = withContext(Dispatchers.IO) {
        val updateData = mapOf(
            "status" to LostRequestStatus.CONFIRMED.value,
            "confirmedBy" to librarianId,
            "confirmedAt" to Date()
        )
        db.collection("lost_requests").document(requestId).update(updateData).await()
    }
}