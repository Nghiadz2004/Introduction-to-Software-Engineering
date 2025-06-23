package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.BookCopy
import com.google.firebase.firestore.AggregateSource
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

    // Tốn số lượt read = len(bookIds)
    suspend fun getNumAvailableCopies(bookIds: List<String>): Map<String, Int> = withContext(Dispatchers.IO) {
        val result = mutableMapOf<String, Int>()

        for (id in bookIds) {
            val copiesRef = db.collection("books").document(id).collection("book_copy")
            val countSnapshot = copiesRef.count().get(AggregateSource.SERVER).await()
            result[id] = countSnapshot.count.toInt()
        }

        return@withContext result
    }

    // Tốn chỉ 1 lượt read
    suspend fun getFirstBookCopiesByStatus(bookId: String, status: String): BookCopy? = withContext(Dispatchers.IO) {
        val querySnapshot = db.collection("books")
            .document(bookId)
            .collection("book_copy")
            .whereEqualTo("status", status)   // dùng biến status truyền vào
            .limit(1)
            .get()
            .await()  // đợi truy vấn xong

        querySnapshot.toObjects(BookCopy::class.java).firstOrNull()  // lấy bản copy đầu tiên hoặc null nếu không có
    }
}
