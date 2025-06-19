package com.example.libmanagementsystem.repository

import com.example.libmanagementsystem.model.LibraryCard
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LibraryCardRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    // Ghi nhận một thẻ thư viện mới
    suspend fun createLibraryCard(card: LibraryCard): String = withContext(Dispatchers.IO) {
        db.collection("library_cards").add(card).await().id
    }

    // Lấy danh sách tất cả các thẻ thư viện
    suspend fun getLibraryCards(): List<LibraryCard> = withContext(Dispatchers.IO) {
        db.collection("library_cards").get().await().toObjects(LibraryCard::class.java)
    }

    // Lấy danh sách thẻ thư viện (đã tạo) theo Id của độc giả
    suspend fun getLibraryCardByReader(readerId: String): List<LibraryCard> = withContext(Dispatchers.IO) {
         db.collection("library_cards")
            .whereEqualTo("readerId", readerId)
            .get()
            .await()
            .toObjects(LibraryCard::class.java)
    }

    // Lấy thông tin thẻ thư viện ứng với Id yêu cầu tạo thẻ
    suspend fun getLibraryCardByRequest(requestId: String): LibraryCard? = withContext(Dispatchers.IO) {
        val snapshot = db.collection("library_cards")
            .whereEqualTo("requestId", requestId)
            .get()
            .await()

        return@withContext snapshot.documents.firstOrNull()?.toObject(LibraryCard::class.java)

    }

    // Lấy danh sách các thẻ thư viện được ghi nhận bởi một thủ thư
    suspend fun getLibraryCardByRecorder(librarianId: String): List<LibraryCard> = withContext(Dispatchers.IO) {
        db.collection("library_cards")
            .whereEqualTo("librarianId", librarianId)
            .get()
            .await()
            .toObjects(LibraryCard::class.java)
    }
}