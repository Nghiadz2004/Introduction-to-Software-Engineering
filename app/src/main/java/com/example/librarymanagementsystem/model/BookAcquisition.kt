package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class BookAcquisition(
    val bookId: String,
    val copyId: String,
    @ServerTimestamp
    val acquisitionDate: Date? = null,
    val acquiredBy: String
)
