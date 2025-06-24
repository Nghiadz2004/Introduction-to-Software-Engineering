package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId // Tùy chọn: Nếu bạn dùng Firestore

data class Fine(
    @DocumentId
    val requestId: String = "", // request_id integer [not null]
    val readerId: Int = 0, // reader_id integer [not null, unique]
    val fineAmount: Int = 0, // fine_amount integer [not null]
    val copyId: String,
    val bookId: String,
    val reason: String,
)