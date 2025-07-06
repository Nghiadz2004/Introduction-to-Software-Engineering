package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.CardRequest
import com.example.librarymanagementsystem.model.RequestStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CardRequestRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    // Gửi yêu cầu tạo thẻ
    suspend fun submitCardRequest(request: CardRequest): String = withContext(Dispatchers.IO) {
        db.collection("card_requests").add(request).await().id
    }

    // Lấy tất cả yêu cầu tạo thẻ đang chờ được duyệt
    suspend fun getPendingRequests(): List<CardRequest> = withContext(Dispatchers.IO) {
        db.collection("card_requests")
            .whereEqualTo("status", RequestStatus.PENDING.value)
            .get()
            .await()
            .toObjects(CardRequest::class.java)
    }

    // Cập nhật trạng thái yêu cầu tạo thẻ (đang chờ -> đã được duyệt/bị từ chối)
    suspend fun updateRequestStatus(
        requestId: String,
        status: RequestStatus
    ) = withContext(Dispatchers.IO) {
        val updateData = mapOf(
            "status" to status.value
        )
        db.collection("card_requests").document(requestId).update(updateData).await()
    }
}
