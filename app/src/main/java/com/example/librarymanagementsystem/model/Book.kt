package com.example.librarymanagementsystem.model

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId // Giả sử bạn đang dùng Firestore
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    @DocumentId // Nếu bạn đang làm việc với Firestore và muốn ID tài liệu trùng với thuộc tính này
    val id: String? = null,
    var cover: String? = null,
    val title: String = "", // title varchar(100) [not null]
    val category: String = "",
    val summary: String? = null,
    var author: String? = null, // author varchar(100) -> Có thể null
    val publishYear: Int? = null, // publish_year integer [not null]
    val publisher: String? = null, // publisher varchar(100) -> Có thể null
    val price: Int? = 0, // price integer [not null]
    var quantity: Int? = 0, // quantity integer [not null]
): Parcelable {
    constructor() : this(
        id = null,
        cover = null,
        title = "",
        category = "",
        summary = null,
        author = null,
        publishYear = null,
        publisher = null,
        price = 0,
        quantity = 0
    )
}