package com.example.libmanagementsystem.model

data class ReturnDisplay(
    val book: Book,
    val borrow: BorrowBook,
    val readerName: String,
    val formattedBorrowDate: String,
    val formattedDueDate: String,
    val statusText: String,
    val fineText: String
)