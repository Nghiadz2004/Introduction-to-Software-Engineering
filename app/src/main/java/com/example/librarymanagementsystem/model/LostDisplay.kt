package com.example.librarymanagementsystem.model

data class LostDisplay(
    val book: Book,
    val readerName: String,
    val copyId: String,
    val formattedLoanDate: String,
    val formattedDueDate: String,
    val fine: String
)