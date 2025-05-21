package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId // Giả sử bạn đang dùng Firestore
import java.util.Date // Để sử dụng kiểu Date cho created_at

enum class OfficerRole(val value: String) {
    THU_THU("Thủ thư"),
    THU_KHO("Thủ kho");

    companion object {
        fun fromString(value: String): OfficerRole? {
            return entries.find { it.value == value }
        }
    }
}

data class Officer(
    @DocumentId // Nếu bạn đang làm việc với Firestore và muốn ID tài liệu trùng với thuộc tính này
    val id: String? = null, // id integer [primary key, increment] -> Có thể null khi thêm mới

    var username: String = "", // username varchar(50) [not null, unique]
    var password: String = "", // password varchar(50) [not null]
    val role: OfficerRole = OfficerRole.THU_THU, // role enum('Thủ thư', 'Thủ kho') [not null]
    var fullName: String? = null, // full_name varchar(50) -> Lưu ý: đổi thành camelCase
    var email: String? = null, // email varchar(50)
    val createdAt: Date = Date() // created_at datetime -> Lưu ý: đổi thành camelCase
)