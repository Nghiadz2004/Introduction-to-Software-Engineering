package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId

data class Role (
    @DocumentId
    val id: String,
    val name: String = "",
    val permissions: List<String> = emptyList()
)