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
    val librarianId: String? = null
)