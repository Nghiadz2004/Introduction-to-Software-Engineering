package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class LostRequest (
    @DocumentId val id: String? = null,
    val readerId: String,
    val fullName: String,
    val submittedAt: Date = Date(),
    var status: LostRequestStatus = LostRequestStatus.PENDING,
    var confirmedBy: String? = null, // officerId nếu được duyệt
    var confirmedAt: Date? = null
)

enum class LostRequestStatus(val value: String) {
    PENDING("Chờ xác nhận"),
    CONFIRMED("Đã xác nhận")
}