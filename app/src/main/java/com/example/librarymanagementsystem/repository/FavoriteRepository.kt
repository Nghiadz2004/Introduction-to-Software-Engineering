package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Favorite
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FavoriteRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    suspend fun createFavorite(readerId: String, bookIdList: List<String>) = withContext(Dispatchers.IO) {
        val favorite = Favorite(readerId, bookIdList)
        db.collection("favorites").document(readerId).set(favorite).await()
    }

    suspend fun getFavorite(readerId: String): Favorite? = withContext(Dispatchers.IO) {
        db.collection("favorites").document(readerId)
            .get()
            .await()
            .toObject(Favorite::class.java)
    }

    // Lấy danh sách Id các quyển sách (bản logic) yêu thích của một độc giả
    suspend fun getFavoriteBooksId(readerId: String): Favorite? = withContext(Dispatchers.IO) {
        db.collection("favorites").document(readerId)
            .get()
            .await()
            .toObject(Favorite::class.java)
    }

    suspend fun addBookToFavorite(readerId: String, bookId: String) = withContext(Dispatchers.IO) {
        val docRef = db.collection("favorites").document(readerId)
        val snapshot = docRef.get().await()

        if (snapshot.exists()) {
            val favorite = snapshot.toObject(Favorite::class.java)
            val currentList = favorite?.bookIdList ?: emptyList()

            val updatedList = currentList + bookId
            docRef.update("bookIdList", updatedList).await()
        } else {
            // Tạo mới document nếu chưa tồn tại
            val newFavorite = Favorite(readerId = readerId, bookIdList = listOf(bookId))
            docRef.set(newFavorite).await()
        }
    }

    // Loại bỏ sách khỏi danh sách yêu thích của độc giả
    suspend fun removeBookFromFavorite(readerId: String, bookId: String) = withContext(Dispatchers.IO) {
        val docRef = db.collection("favorites").document(readerId)
        val snapshot = docRef.get().await()
        val favorite = snapshot.toObject(Favorite::class.java)

        if (favorite != null && bookId in favorite.bookIdList) {
            val updatedList = favorite.bookIdList.filter { it != bookId }
            docRef.update("bookIdList", updatedList).await()
        }
    }

    suspend fun updateFavorite(readerId: String, bookIdList: List<String>) = withContext(Dispatchers.IO) {
        val docRef = db.collection("favorites").document(readerId)

        docRef.update("bookIdList", bookIdList).await()
    }
}