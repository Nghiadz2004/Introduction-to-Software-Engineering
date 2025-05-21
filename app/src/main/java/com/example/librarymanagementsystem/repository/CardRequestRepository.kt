package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.CardRequest
import com.example.librarymanagementsystem.model.RequestStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class CardRequestRepository(private val db: FirebaseFirestore) {

    suspend fun submitCardRequest(request: CardRequest): String = withContext(Dispatchers.IO) {
        db.collection("card_requests").add(request).await().id
    }

    suspend fun getPendingRequests(): List<CardRequest> = withContext(Dispatchers.IO) {
        db.collection("card_requests")
            .whereEqualTo("status", RequestStatus.PENDING.value)
            .get()
            .await()
            .toObjects(CardRequest::class.java)
    }

    suspend fun updateRequestStatus(
        requestId: String,
        status: RequestStatus,
        officerId: String
    ) = withContext(Dispatchers.IO) {
        val updateData = mapOf(
            "status" to status.value,
            "approvedBy" to officerId,
            "approvedAt" to Date()
        )
        db.collection("card_requests").document(requestId).update(updateData).await()
    }
}
