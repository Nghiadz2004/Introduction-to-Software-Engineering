package com.example.librarymanagementsystem.model

import com.google.firebase.firestore.DocumentId // Tùy chọn: Nếu bạn dùng Firestore

data class Fine(
    @DocumentId
    // Trong trường hợp này, reader_id vừa là unique vừa có thể dùng làm ID tài liệu/khóa chính.
    // Nếu bạn dùng Firestore, có thể dùng @DocumentId trực tiếp trên readerId.
    // Nếu không, bạn có thể cân nhắc thêm một 'id' riêng (integer [primary key, increment])
    // nếu hệ thống DB của bạn yêu cầu một khóa chính độc lập.
    // Giả định readerId sẽ là ID của tài liệu trong Firestore hoặc khóa chính.
    val requestId: String = "", // request_id integer [not null]
    val readerId: Int = 0, // reader_id integer [not null, unique]
    val fineAmount: Int = 0, // fine_amount integer [not null]
    val copyId: String,
    val bookId: String,
    val reason: String,
)