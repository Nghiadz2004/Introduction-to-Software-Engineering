package com.example.libmanagementsystem.model

import com.google.firebase.firestore.DocumentId // Giả sử bạn đang dùng Firestore
import java.util.Calendar
import java.util.Date

data class LibraryCard(
    @DocumentId // Nếu bạn đang dùng Firestore và muốn ID tài liệu trùng với thuộc tính này
    val id: String? =null, // id integer [primary key, increment] -> Có thể null khi tạo mới
    val requestId: String = "", // request_id integer [not null] -> Đổi thành camelCase
    val readerId: String = "", // reader_id integer [not null] -> Đổi thành camelCase
    val librarianId: String = "", // librarian_id integer [not null] -> Đổi thành camelCase
    val fullName: String = "",
    val email: String = "",
    val type: String = "",
    val birthday: Date = Date(), // birthday datetime [not null]
    val createdAt: Date = Date(), // created_at datetime [not null, default: `CURRENT_TIMESTAMP`] -> Đổi thành camelCase
    val address: String = "",
    var status: String = UserStatus.ACTIVE.value, // status varchar(20)
)
{
    // Hàm để tính dueDate dựa trên createdAt cộng thêm 1 năm
    val getDueDate: Date
        get() {
            val calendar = Calendar.getInstance()
            calendar.time = createdAt
            calendar.add(Calendar.YEAR, 1) // cộng thêm 1 năm
            return calendar.time
        }
}