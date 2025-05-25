package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class CardRequest(
    @DocumentId val id: String? = null,
    val readerId: String,
    val fullName: String,
    val birthday: Date,
    val submittedAt: Date = Date(),
    var status: RequestStatus = RequestStatus.PENDING,
    var approvedBy: String? = null, // officerId nếu được duyệt
    var approvedAt: Date? = null
)

enum class RequestStatus(val value: String) {
    PENDING("Chờ duyệt"),
    APPROVED("Đã duyệt"),
    REJECTED("Từ chối")
}
