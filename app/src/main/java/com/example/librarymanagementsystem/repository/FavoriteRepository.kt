package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Favorite
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FavoriteRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    // Lấy danh sách Id các quyển sách (bản logic) yêu thích của một độc giả
    suspend fun getFavoriteBooksId(readerId: String): Favorite? = withContext(Dispatchers.IO) {
         db.collection("favorites").document(readerId)
             .get()
             .await()
             .toObject(Favorite::class.java)
    }

    suspend fun removeBookFromFavorite(readerId: String, bookId: String) = withContext(Dispatchers.IO) {
        val docRef = db.collection("favorites").document(readerId)
        val snapshot = docRef.get().await()
        val favorite = snapshot.toObject(Favorite::class.java)

        if (favorite != null && bookId in favorite.bookIdList) {
            val updatedList = favorite.bookIdList.filter { it != bookId }
            docRef.update("bookIdList", updatedList).await()
        }
    }
}