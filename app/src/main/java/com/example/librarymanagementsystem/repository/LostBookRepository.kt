package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.LostBook
import com.example.librarymanagementsystem.model.LostRequestStatus
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class LostBookRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    // Gửi yêu cầu mất sách
    suspend fun submitLostRequest(request: LostBook): String = withContext(Dispatchers.IO) {
        db.collection("lost_requests")
            .document(request.requestId!!) // dùng requestId làm document ID
            .set(request) // set nội dung document
            .await()
        request.requestId
    }

    suspend fun cancelLostRequestByCopyID(bookId: String, readId: String) = withContext(Dispatchers.IO) {
        val snapshot = db.collection("lost_requests")
            .whereEqualTo("bookId", bookId)
            .whereEqualTo("readerId", readId)
            .get()
            .await()

        for (document in snapshot.documents) {
            db.collection("lost_requests")
                .document(document.id)
                .delete()
                .await()
        }
    }

    suspend fun getConfirmedLostBookCountInRange(days: Int? = null): Long = withContext(Dispatchers.IO) {
        val collection = db.collection("lost_requests")

        val query = if (days != null) {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -days) // Lùi về 'days' ngày
            val fromDate = calendar.time

            collection
                .whereEqualTo("status", "CONFIRMED")                 // Trạng thái xác nhận mất
                .whereGreaterThanOrEqualTo("confirmedAt", fromDate) // confirmedAt trong khoảng
        } else {
            collection.whereEqualTo("status", "CONFIRMED") // All time
        }

        val snapshot = query.count().get(AggregateSource.SERVER).await()
        snapshot.count
    }

    // Lấy tất cả yêu cầu mất sách
    suspend fun getPendingRequests(): List<LostBook> = withContext(Dispatchers.IO) {
        db.collection("lost_requests")
            .whereEqualTo("status", LostRequestStatus.PENDING.value)
            .get()
            .await()
            .toObjects(LostBook::class.java)
    }

    // Lấy tất cả yêu cầu mất sách đang chờ được duyệt
    suspend fun getReaderPendingRequests(readerId: String): List<LostBook> = withContext(Dispatchers.IO) {
        db.collection("lost_requests")
            .whereEqualTo("status", LostRequestStatus.PENDING.value)
            .whereEqualTo("readerId", readerId)
            .get()
            .await()
            .toObjects(LostBook::class.java)
    }

    suspend fun getAllLostBook(): List<LostBook> = withContext(Dispatchers.IO) {
        db.collection("lost_requests")
            .get()
            .await()
            .toObjects(LostBook::class.java)
    }
}