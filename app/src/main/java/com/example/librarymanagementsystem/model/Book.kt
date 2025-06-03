package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId // Giả sử bạn đang dùng Firestore

data class Book(
    @DocumentId // Nếu bạn đang làm việc với Firestore và muốn ID tài liệu trùng với thuộc tính này
    val id: String? =null,
    var title: String = "", // title varchar(100) [not null]
    var category: String = "",
    var author: String? = null, // author varchar(100) -> Có thể null
    var publishYear: Int = 0, // publish_year integer [not null]
    var publisher: String? = null, // publisher varchar(100) -> Có thể null
    var price: Int = 0, // price integer [not null]
    var quantity: Int = 0 // quantity integer [not null]
)