package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.BookCopy
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BookCopyRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    suspend fun getAllBookCopies(): List<BookCopy> = withContext(Dispatchers.IO) {
        db.collection("book_copy").get().await().toObjects(BookCopy::class.java)
    }
}
