package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class BorrowRequest(
    @DocumentId
    val id: String? = null, // Firestore document ID nếu bạn cần
    val libraryCardId: String = "",
    val readerId: String = "",
    val bookId: String = "",
    val borrowDate: Date = Date(),
    val daysBorrow: Int = 1,
    val expectedReturnDate: Date = Date(),
    val status: String = RequestStatus.PENDING.value // Lưu dưới dạng string
) {
    fun getStatusEnum(): RequestStatus? = RequestStatus.fromString(status)
}