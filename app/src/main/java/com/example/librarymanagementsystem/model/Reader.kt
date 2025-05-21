package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId // Giả sử bạn đang dùng Firestore
import java.util.Date // Để sử dụng kiểu Date cho created_at

enum class ReaderType(val value: String) {
    VANG("Vàng"),
    BAC("Bạc"),
    DONG("Đồng");

    companion object {
        fun fromString(value: String): ReaderType? {
            return entries.find { it.value == value }
        }
    }
}


data class Reader(
    @DocumentId // Nếu bạn đang làm việc với Firestore và muốn ID tài liệu trùng với thuộc tính này
    val id: String? = null, // id integer [primary key, increment] -> Có thể null khi thêm mới

    var username: String = "", // username varchar(50) [not null, unique]
    var password: String = "", // password varchar(50) [not null]
    var type: ReaderType = ReaderType.DONG, // type enum('Vàng', 'Bạc', 'Đồng') [not null]
    var fullname: String? = null, // fullname varchar(50)
    var email: String? = null, // email varchar(50)
    val createdAt: Date = Date(), // created_at datetime
    var credits: Int = 0 // credits integer
)