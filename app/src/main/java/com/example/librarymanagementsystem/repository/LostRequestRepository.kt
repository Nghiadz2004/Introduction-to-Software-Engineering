package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.CardRequest
import com.example.librarymanagementsystem.model.LostRequest
import com.example.librarymanagementsystem.model.LostRequestStatus
import com.example.librarymanagementsystem.model.RequestStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class LostRequestRepository(private val db: FirebaseFirestore) {
    suspend fun submitCardRequest(request: LostRequest): String = withContext(Dispatchers.IO) {
        db.collection("lost_requests").add(request).await().id
    }

    suspend fun getPendingRequests(): List<LostRequest> = withContext(Dispatchers.IO) {
        db.collection("lost_requests")
            .whereEqualTo("status", LostRequestStatus.PENDING.value)
            .get()
            .await()
            .toObjects(LostRequest::class.java)
    }

    suspend fun updateLostRequestStatus(
        requestId: String,
        status: LostRequestStatus,
        officerId: String
    ) = withContext(Dispatchers.IO) {
        val updateData = mapOf(
            "status" to status.value,
            "confirmedBy" to officerId,
            "confirmedAt" to Date()
        )
        db.collection("lost_requests").document(requestId).update(updateData).await()
    }
}