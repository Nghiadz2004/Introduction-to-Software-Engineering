package com.example.librarymanagementsystem.model

data class LostDisplay(
    val book: Book,
    val readerName: String,
    val copyId: String,
    val formattedLoanDate: String,
    val formattedDueDate: String,
    val fine: String,
    val requestId: String,
    val readerId: String,
    val bookId: String,
    val fineAmount: Int
)