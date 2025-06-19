package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.BorrowRequest
import com.example.librarymanagementsystem.model.RequestStatus
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RequestBorrowRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    // Lấy tất cả các yêu cầu mượn sách của 1 người dùng (bao gồm đang yêu cầu mượn, bị từ chối, đã trả)
    suspend fun getRequestBooksByReader(readerId: String): List<BorrowRequest> = withContext(
        Dispatchers.IO) {
        db.collection("request_borrow")
            .whereEqualTo("readerId", readerId)
            .get()
            .await()
            .toObjects(BorrowRequest::class.java)
    }

    // Lấy yêu cầu mượn sách đang đợi của tất cả người dùng
    suspend fun getPendingRequests(): List<BorrowRequest> = withContext(Dispatchers.IO) {
        db.collection("request_borrow")
            .whereEqualTo("status", RequestStatus.PENDING.value)
            .get()
            .await()
            .toObjects(BorrowRequest::class.java)
    }

    // Lấy yêu cầu mượn sách đang đợi của một người dùng
    suspend fun getReaderPendingRequests(readerId: String): List<BorrowRequest> = withContext(Dispatchers.IO) {
        db.collection("request_borrow")
            .whereEqualTo("status", RequestStatus.PENDING.value)
            .whereEqualTo("readerId", readerId)
            .get()
            .await()
            .toObjects(BorrowRequest::class.java)
    }

    // Cập nhật trạng thái yêu cầu mượn sách (đang chờ -> đã được duyệt/bị từ chối)
    suspend fun updateRequestBorrowStatus(
        requestId: String,
        status: RequestStatus
    ) = withContext(Dispatchers.IO) {
        val updateData = mapOf(
            "status" to status.value
        )
        db.collection("request_borrow").document(requestId).update(updateData).await()
    }

    suspend fun cancelPendingRequest(bookId: String, readerId: String) {
        val snapshot = db.collection("request_borrow")
            .whereEqualTo("bookId", bookId)
            .whereEqualTo("readerId", readerId)
            .get()
            .await()

        for (document in snapshot.documents) {
            db.collection("request_borrow")
                .document(document.id)
                .delete()
                .await()
        }
    }

    // Lấy danh sách số người đang chờ mượn một quyển sách (bản logic)
    suspend fun getBookPendingRequests(bookId: String): List<BorrowRequest> = withContext(Dispatchers.IO) {
        db.collection("request_borrow")
            .whereEqualTo("status", RequestStatus.PENDING.value)
            .whereEqualTo("bookId", bookId)
            .get()
            .await()
            .toObjects(BorrowRequest::class.java)
    }

    suspend fun getNumBookPendingRequests(bookId: String): Int = withContext(Dispatchers.IO) {
        val countSnapshot = db.collection("request_borrow")
            .whereEqualTo("status", RequestStatus.PENDING.value)
            .whereEqualTo("bookId", bookId)
            .count()
            .get(AggregateSource.SERVER)  // dùng server để không tính local cache
            .await()

        val result = countSnapshot.count.toInt()

        return@withContext result
    }
}