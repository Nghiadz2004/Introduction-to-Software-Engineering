package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId
import java.util.Date


data class BorrowRequest(
    @DocumentId
    val id: String? = null, // Firestore document ID nếu bạn cần
    val libraryCardId: String = "",
    val readerId: String,
    val bookId: String = "",
    val borrowDate: Date = Date(),
    val daysBorrow: Int = 1,
    val expectedReturnDate: Date,
    val status: String = RequestStatus.PENDING.value // Lưu dưới dạng string
) {
    constructor() : this(
        id = null,
        libraryCardId = "",
        readerId = "",
        bookId = "",
        borrowDate = Date(),
        daysBorrow = 1,
        expectedReturnDate = Date(),
        status = RequestStatus.PENDING.value
    )

    fun getStatusEnum(): RequestStatus? = RequestStatus.fromString(status)
}