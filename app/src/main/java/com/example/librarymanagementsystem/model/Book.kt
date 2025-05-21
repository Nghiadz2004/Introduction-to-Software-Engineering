package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId // Giả sử bạn đang dùng Firestore

enum class BookCategory(val value: String) {
    VAN_HOC("Văn học"),
    KHOA_HOC_KY_THUAT("Khoa học - Kỹ thuật"),
    LICH_SU_DIA_LY("Lịch sử - Địa lý"),
    VAN_HOA_NGHE_THUAT("Văn hóa - Nghệ thuật");

    companion object {
        fun fromString(value: String): BookCategory? {
            return entries.find { it.value == value }
        }
    }
}

data class Book(
    @DocumentId // Nếu bạn đang làm việc với Firestore và muốn ID tài liệu trùng với thuộc tính này
    val id: String? = null, // id integer [primary key, increment] -> Có thể null khi thêm mới, tự động tăng.

    var title: String = "", // title varchar(100) [not null]
    var category: BookCategory = BookCategory.VAN_HOC, // category enum [...] [not null]
    var author: String? = null, // author varchar(100) -> Có thể null
    var publishYear: Int = 0, // publish_year integer [not null]
    var publisher: String? = null, // publisher varchar(100) -> Có thể null
    var price: Int = 0, // price integer [not null]
    var quantity: Int = 0 // quantity integer [not null]
)