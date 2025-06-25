package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class BorrowBook(
    @DocumentId
    val requestId: String? = null,
    val libraryCardId: String? = null,
    val copyId: String? = null,
    val readerId: String? = null,
    val bookId: String? = null,
    val borrowDate: Date? = null,
    @ServerTimestamp
    val confirmDate: Date? = null,
    val expectedReturnDate: Date? = null,
    val actualReturnDate: Date? = null,
    val librarianId: String? = null,
    val status : String = BorrowStatus.BORROWED.value
) {
    constructor(): this(
        requestId = null,
        libraryCardId = null,
        copyId = null,
        readerId = null,
        bookId = null,
        borrowDate = null,
        confirmDate = null,
        expectedReturnDate = null,
        actualReturnDate = null,
        librarianId = null,
        status = BorrowStatus.BORROWED.value
    )
}

enum class BorrowStatus(val value: String) {
    RETURNED("RETURNED"),
    BORROWED("BORROWED"),
    LOST("LOST");

    companion object {
        fun fromString(value: String): BorrowStatus? {
            return BorrowStatus.entries.find { it.value == value }
        }
    }
}