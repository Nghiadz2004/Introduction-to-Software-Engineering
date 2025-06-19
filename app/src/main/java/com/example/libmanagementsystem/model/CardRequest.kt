package com.example.libmanagementsystem.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class CardRequest(
    @DocumentId val id: String? =null,
    val readerId: String,
    val fullName: String,
    val birthday: Date,
    val address: String,
    val email: String,
    val type: String,
    val requestAt: Date = Date(),
    var status: String = RequestStatus.PENDING.value
) {
    fun getStatusEnum(): RequestStatus? = RequestStatus.fromString(status)
    fun getTypeEnum(): ReaderType? = ReaderType.fromString(type)
}

enum class ReaderType(val value: String) {
    GOLD("GOLD"),
    SILVER("SILVER"),
    BRONZE("BRONZE");

    companion object {
        fun fromString(value: String): ReaderType? {
            return entries.find { it.value == value }
        }
    }
}

enum class RequestStatus(val value: String) {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    companion object {
        fun fromString(value: String): RequestStatus? {
            return RequestStatus.entries.find { it.value == value }
        }
    }
}
