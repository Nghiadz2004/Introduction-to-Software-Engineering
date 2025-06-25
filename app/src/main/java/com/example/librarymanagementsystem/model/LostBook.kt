package com.example.librarymanagementsystem.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class LostBook(
    @DocumentId
    val requestId: String? =null,
    val bookId: String = "",
    val copyId: String = "",
    val readerId: String = "",
    @ServerTimestamp
    val recordDate: Date? = null,
    var status: String = LostRequestStatus.PENDING.value,
    var confirmedBy: String? = null, // officerId nếu được duyệt
    var confirmedAt: Date? = null
){
    constructor(): this(
        requestId = null,
        bookId = "",
        copyId = "",
        readerId = "",
    )
}

enum class LostRequestStatus(val value: String) {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED");

    companion object {
        fun fromString(value: String): LostRequestStatus? {
            return LostRequestStatus.entries.find { it.value == value }
        }
    }
}