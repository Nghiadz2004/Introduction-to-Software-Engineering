package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId

data class Fine(
    @DocumentId
    val requestId: String = "",
    val readerId: String = "",
    val fineAmount: Int = 0,
    val copyId: String = "",
    val bookId: String = "",
    val reason: String = "",
)