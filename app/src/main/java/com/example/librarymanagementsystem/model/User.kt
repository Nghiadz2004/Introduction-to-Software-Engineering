package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId // Giả sử bạn đang dùng Firestore
import java.util.Date // Để sử dụng kiểu Date cho created_at

enum class UserStatus(val value: String) {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    BANNED("BANNED");

    companion object {
        fun fromString(value: String): UserStatus? {
            return UserStatus.entries.find { it.value == value }
        }
    }
}

data class User(
    @DocumentId // Nếu bạn đang làm việc với Firestore và muốn ID tài liệu trùng với thuộc tính này
    val id: String? = null, // id integer [primary key, increment] -> Có thể null khi thêm mới
    var username: String = "", // username varchar(50) [not null, unique]
    var birthday: Date, // password varchar(50) [not null]
    val fullname: String, // fullname varchar(50)
    var email: String? = null, // email varchar(50)
    val roleId: String, // role varchar(20) [not null]
    val createdAt: Date = Date(), // created_at datetime
    var status: String = UserStatus.ACTIVE.value, // status varchar(20)
    var avatar: String? = null, // avatar varchar(255)
    var enumStatus: String? = null
){
    // Constructor không đối số để Firestore deserialize
    constructor() : this(
        id = null,
        username = "",
        birthday = Date(),
        fullname = "",
        email = null,
        roleId = "",
        createdAt = Date(),
        status = UserStatus.ACTIVE.value,
        avatar = null,
        enumStatus = null
    )

    fun enumStatusValue(): UserStatus? = UserStatus.fromString(status)
}