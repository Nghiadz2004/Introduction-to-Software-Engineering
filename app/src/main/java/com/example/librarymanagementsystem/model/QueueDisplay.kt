package com.example.librarymanagementsystem.model

data class QueueDisplay(
    val coverUrl: String?,
    val title: String,
    val author: String?,
    val copyLeft: Int,
    val readerName: String,
    val status: String
)