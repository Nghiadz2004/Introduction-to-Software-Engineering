package com.example.libmanagementsystem.model

data class AddReaderRequest (
    val requestId: String,
    val fullName: String,
    val email: String,
    val birthday: String,
    val address: String,
    val type: String
)