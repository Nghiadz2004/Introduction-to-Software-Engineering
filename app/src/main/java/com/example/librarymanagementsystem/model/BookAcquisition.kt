package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

// File: BookAcquisition.kt
data class BookAcquisition(
    val bookId: String,
    val acquisitionDate: Date = Date(),
    val acquiredBy: String // Officer ID
)
