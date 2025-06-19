package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.BookCopy
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BookCopyRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    suspend fun getBookCopies(bookId: String): List<BookCopy> = withContext(Dispatchers.IO) {
        val copiesRef = db.collection("books").document(bookId).collection("copies")
        val snapshot = copiesRef.get().await()
        snapshot.toObjects(BookCopy::class.java) // ✅ return implicit
    }

    suspend fun getBookCopiesByStatus(bookId: String, status: String): BookCopy? = withContext(Dispatchers.IO) {
        val querySnapshot = db.collection("books")
            .document(bookId)
            .collection("copies")
            .whereEqualTo("status", status)   // dùng biến status truyền vào
            .limit(1)
            .get()
            .await()  // đợi truy vấn xong

        querySnapshot.toObjects(BookCopy::class.java).firstOrNull()  // lấy bản copy đầu tiên hoặc null nếu không có
    }
}
