package com.example.libmanagementsystem.model

import com.google.firebase.firestore.DocumentId

data class Favorite (
    @DocumentId
    val readerId: String = "",
    val bookIdList: List<String> = emptyList()
)