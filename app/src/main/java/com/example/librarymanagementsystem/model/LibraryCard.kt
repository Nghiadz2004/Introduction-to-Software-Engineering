package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId // Giả sử bạn đang dùng Firestore
import com.google.firebase.firestore.ServerTimestamp
import java.util.Calendar
import java.util.Date

data class LibraryCard(
    @DocumentId // Nếu bạn đang dùng Firestore và muốn ID tài liệu trùng với thuộc tính này
    val requestId: String = "", // request_id integer [not null] -> Đổi thành camelCase
    val readerId: String = "", // reader_id integer [not null] -> Đổi thành camelCase
    val librarianId: String = "", // librarian_id integer [not null] -> Đổi thành camelCase
    val fullName: String = "",
    val email: String = "",
    val type: String = "",
    val birthday: Date = Date(), // birthday datetime [not null]
    @ServerTimestamp
    val createdAt: Date? = null,
    val address: String = "",
    var status: String = UserStatus.ACTIVE.value, // status varchar(20)
)
{
    // Hàm để tính dueDate dựa trên createdAt cộng thêm 1 năm
    fun getDueDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = createdAt ?: Date()
        calendar.add(Calendar.MONTH, 6)
        return calendar.time
    }

}