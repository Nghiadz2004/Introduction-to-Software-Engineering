package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class BorrowBook(
    @DocumentId
    val requestId: String? = null,
    val libraryCardId: String,
    val copyId: String,
    val readerId: String,
    val bookId: String,
    val borrowDate: Date,
    val confirmDate: Date = Date(),
    val expectedReturnDate: Date,
    val actualReturnDate: Date = Date(),
    val librarianId: String
)
