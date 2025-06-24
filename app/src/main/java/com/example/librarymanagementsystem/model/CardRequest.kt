package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class CardRequest(
    @DocumentId val id: String? = null,
    val readerId: String = "",
    val fullName: String = "",
    val birthday: Date? = null,
    val address: String = "",
    val email: String = "",
    val type: String = ReaderType.GOLD.value, // Mặc định là GOLD
    @ServerTimestamp
    val requestAt: Date? = null,
    var status: String = RequestStatus.PENDING.value // Mặc định là PENDING
) {
    // Constructor không tham số để Firestore có thể deserialize được
    constructor() : this(
        id = null,
        readerId = "",
        fullName = "",
        birthday = null,
        address = "",
        email = "",
        type = ReaderType.GOLD.value,
        requestAt = null,
        status = RequestStatus.PENDING.value
    )

    // Các phương thức chuyển đổi thành Enum
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
