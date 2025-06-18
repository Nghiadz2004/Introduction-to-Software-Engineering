package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId

enum class CopyStatus(val value: String) {
    AVAILABLE("Có sẵn"),
    BORROWED("Đang mượn"),
    LOST("Mất");

    companion object {
        fun fromString(value: String): CopyStatus? {
            return entries.find { it.value == value }
        }
    }
}

data class BookCopy (
    @DocumentId
    val copyId: String? = null,
    val bookId: String? = null,
    val status: String = CopyStatus.AVAILABLE.value
) {
    fun getStatusEnum(): CopyStatus? = CopyStatus.fromString(status)
}