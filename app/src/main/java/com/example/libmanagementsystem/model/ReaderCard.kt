package com.example.libmanagementsystem.model

data class ReaderCard (
    val cardId: String,
    val fullName: String,
    val email: String,
    val birthday: String,
    val address: String,
    val type: String,
    val issuer: String,
    val issueDate: String,
    val dueDate: String,
    val status: String
)