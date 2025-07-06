package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class BorrowRequest(
    @DocumentId
    val id: String? = null,
    val libraryCardId: String = "",
    val readerId: String = "",
    val bookId: String = "",
    val category: String = "",
    @ServerTimestamp
    val borrowDate: Date? = null,
    val daysBorrow: Int = 1,
    val status: String = RequestStatus.PENDING.value
) {
    constructor() : this(
        id = null,
        libraryCardId = "",
        readerId = "",
        bookId = "",
        category = "",
        borrowDate = null,
        daysBorrow = 1,
        status = RequestStatus.PENDING.value
    )
}
