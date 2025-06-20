package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.model.BorrowBook
import com.example.librarymanagementsystem.model.LibraryCard
import com.example.librarymanagementsystem.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar

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