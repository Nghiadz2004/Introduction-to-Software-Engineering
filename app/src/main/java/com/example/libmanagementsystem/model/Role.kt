package com.example.libmanagementsystem.model

import com.google.firebase.firestore.DocumentId

data class Role (
    @DocumentId // Nếu bạn đang làm việc với Firestore và muốn ID tài liệu trùng với thuộc tính này
    val id: String,
    val name: String = "",
    val permissions: List<String> = emptyList()
)