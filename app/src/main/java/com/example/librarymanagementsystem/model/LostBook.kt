package com.example.librarymanagementsystem.model

import java.util.Date

data class LostBook(
    val bookId: String = "",
    val readerId: String = "",
    val recordDate: Date = Date(),
    val librarianId: String = ""
)
