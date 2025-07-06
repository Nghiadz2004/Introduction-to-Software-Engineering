package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.LibraryCard
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar

class LibraryCardRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    // Lấy danh sách tất cả các thẻ thư viện
    suspend fun getLibraryCards(): List<LibraryCard> = withContext(Dispatchers.IO) {
        db.collection("library_cards").get().await().toObjects(LibraryCard::class.java)
    }

    suspend fun getLatestLibraryCard(readerId: String): LibraryCard? = withContext(Dispatchers.IO) {
        // 1. Lấy ngày hiện tại và lùi lại 6 tháng
        val now = Calendar.getInstance()
        now.add(Calendar.MONTH, -6)
        val sixMonthsAgo = Timestamp(now.time)

        // 2. Truy vấn Firestore: lấy các thẻ được tạo trong vòng 6 tháng, sắp xếp mới nhất
        val querySnapshot = db.collection("library_cards")
            .whereEqualTo("readerId", readerId)
            .whereGreaterThanOrEqualTo("createdAt", sixMonthsAgo)
            .limit(1)
            .get()
            .await()

        // 3. Lấy document đầu tiên (nếu có) và chuyển thành đối tượng
        return@withContext if (!querySnapshot.isEmpty) {
            querySnapshot.documents[0].toObject(LibraryCard::class.java)
        } else {
            null
        }
    }

    suspend fun updateLibraryCard(cardId: String, status: String) = withContext(Dispatchers.IO) {
        db.collection("library_cards")
            .document(cardId)
            .update("status", status)
            .await()
    }
}