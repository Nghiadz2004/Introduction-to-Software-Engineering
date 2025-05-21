package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

enum class BorrowStatus(val value: String) {
    THANH_CONG("Thành công"),
    THAT_BAI("Thất bại");

    companion object {
        fun fromString(value: String): BorrowStatus? {
            return entries.find { it.value == value }
        }
    }
}

data class BorrowBook(
    @DocumentId
    val id: String? = null, // Firestore document ID nếu bạn cần

    val libraryCardId: String = "",
    val bookId: String = "",
    val borrowDate: Date = Date(),
    val returnDate: Date? = null,
    val status: String = BorrowStatus.THANH_CONG.value // Lưu dưới dạng string
) {
    fun getStatusEnum(): BorrowStatus? = BorrowStatus.fromString(status)
}
