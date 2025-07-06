package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Calendar
import java.util.Date

data class LibraryCard(
    @DocumentId
    val requestId: String = "",
    val readerId: String = "",
    val librarianId: String = "",
    val fullName: String = "",
    val email: String = "",
    val type: String = "",
    val birthday: Date = Date(),
    @ServerTimestamp
    val createdAt: Date? = null,
    val address: String = "",
    var status: String = UserStatus.ACTIVE.value,
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