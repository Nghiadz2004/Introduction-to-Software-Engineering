package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId

data class Favorite (
    @DocumentId
    val readerId: String = "",
    val bookIdList: List<String> = emptyList()
)