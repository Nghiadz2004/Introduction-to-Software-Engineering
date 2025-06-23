package com.example.librarymanagementsystem.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class BorrowBook(
    @DocumentId
    val requestId: String? = null,
    val libraryCardId: String? = null,
    val copyId: String? = null,
    val readerId: String? = null,
    val bookId: String? = null,
    val borrowDate: Date? = null,
    val confirmDate: Timestamp? = null,
    val expectedReturnDate: Timestamp? = null,
    val actualReturnDate: Date? = null,
    val librarianId: String? = null
)