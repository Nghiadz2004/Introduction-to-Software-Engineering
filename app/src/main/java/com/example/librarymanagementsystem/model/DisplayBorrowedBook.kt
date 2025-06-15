package com.example.librarymanagementsystem.model

import java.util.Date

data class BookDisplayItem(
    val book: Book,
    val expectedReturnDate: Date?
)