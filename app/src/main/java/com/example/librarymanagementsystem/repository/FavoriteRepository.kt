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

    // Lấy danh sách Id các quyển sách (bản logic) yêu thích của một độc giả
    suspend fun getFavoriteBooksId(readerId: String): Favorite? = withContext(Dispatchers.IO) {
        db.collection("favorites").document(readerId)
            .get()
            .await()
            .toObject(Favorite::class.java)
    }

    suspend fun updateFavorite(readerId: String, bookIdSet: Set<String>) = withContext(Dispatchers.IO) {
        val docRef = db.collection("favorites").document(readerId)

        docRef.update("bookIdList", bookIdSet.toList()).await()
    }
}