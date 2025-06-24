package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Fine
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class FineRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    // Ghi nhận khoản phạt mới
    suspend fun addFine(fine: Fine): String = withContext(Dispatchers.IO) {
        val docRef = db.collection("fines").add(fine).await()
        docRef.id
    }

    // Lấy danh sách các khoản phạt
    suspend fun getFines(): List<Fine> = withContext(Dispatchers.IO) {
        db.collection("fines").get().await().toObjects(Fine::class.java)
    }

    // Lấy danh sách các khoản phạt của một độc giả
    suspend fun getFineByReader(readerId: String): List<Fine> = withContext(Dispatchers.IO) {
        db.collection("fines")
            .whereEqualTo("readerId", readerId)
            .get()
            .await()
            .toObjects(Fine::class.java)
    }

    suspend fun getTotalFineInRange(days: Int? = null): Long = withContext(Dispatchers.IO) {
        val collection = db.collection("fine")

        val query = if (days != null) {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -days) // Lùi về 'days' ngày
            val fromDate = calendar.time

            collection.whereGreaterThanOrEqualTo("recordDate", fromDate)
                .get()
                .await()
        }
        else {
            collection.get().await()
        }

        query.documents.sumOf {
            it.getLong("fineAmount") ?: 0L
        }
    }

    // Lấy khoản phạt theo yêu cầu mượn sách tương ứng
    suspend fun getFineByRequest(requestId: String): Fine? = withContext(Dispatchers.IO) {
        val snapshot = db.collection("fines")
            .whereEqualTo("requestId",requestId)
            .get()
            .await()

        return@withContext snapshot.documents.firstOrNull()?.toObject(Fine::class.java)

    }

    // Lấy khoản phạt theo bản copy của một quyển sách
    suspend fun getFineByCopy(copyId: String): List<Fine> = withContext(Dispatchers.IO) {
        db.collection("fines")
            .whereEqualTo("copyId", copyId)
            .get().await()
            .toObjects(Fine::class.java)
    }

    // Lấy khoản phạt theo một quyển sách (bản vât lý)
    suspend fun getFineByBook(bookId: String): List<Fine> = withContext(Dispatchers.IO) {
        db.collection("fines")
            .whereEqualTo("bookId", bookId)
            .get().await()
            .toObjects(Fine::class.java)
    }
}

