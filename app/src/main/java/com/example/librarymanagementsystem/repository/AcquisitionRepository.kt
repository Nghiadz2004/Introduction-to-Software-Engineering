package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.model.BookAcquisition
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

class AcquisitionRepository(private val db: FirebaseFirestore) {

    // Ghi nhận một quyển sách mới được nhập vào kho
    suspend fun recordMultipleAcquisitions(
        bookId: String,
        quantity: Int,
        recorderId: String
    ): List<String> = withContext(Dispatchers.IO) {
        val batch = db.batch()
        val collectionRef = db.collection("book_acquisitions")
        val generatedDocIds = mutableListOf<String>()

        for (i in 1..quantity) {
            val docRef = collectionRef.document()
            generatedDocIds.add(docRef.id)

            val data = mapOf(
                "bookId" to bookId,
                "copyId" to docRef.id, // Tự động dùng ID document làm copyId
                "acquiredBy" to recorderId,
                "acquisitionDate" to FieldValue.serverTimestamp()
            )

            batch.set(docRef, data)
        }

        batch.commit().await()
        return@withContext generatedDocIds
    }


    // Trả về danh sách các sách đã được nhập vào kho bởi thủ kho
    suspend fun getAcquisitions(): List<BookAcquisition> = withContext(Dispatchers.IO) {
        db.collection("book_acquisitions").get().await().toObjects(BookAcquisition::class.java)
    }

    // Trả về chi tiết một copy (bản vật lý của sách) được nhập kho
    suspend fun getAcquisitionByCopy(copyId: String): BookAcquisition? = withContext(Dispatchers.IO) {
        val snapshot = db.collection("book_acquisition")
            .whereEqualTo("copyId", copyId)
            .get()
            .await()

        return@withContext snapshot.documents.firstOrNull()?.toObject(BookAcquisition::class.java)
    }

    // Trả về danh sách dữ liệu nhập kho của một cuốn sách
    suspend fun getAcquisitionByBook(bookId: String): List<BookAcquisition> = withContext(
        Dispatchers.IO) {
        db.collection("book_acquisition")
            .whereEqualTo("bookId", bookId)
            .get()
            .await()
            .toObjects(BookAcquisition::class.java)
    }

    // Trả về danh sách dữ liệu nhập kho trong một thời điểm nào đó
    suspend fun getAcquisitionByDate(acquisitionDate: Date): List<BookAcquisition> = withContext(
        Dispatchers.IO) {
        db.collection("book_acquisition")
            .whereEqualTo("acquisitionDate", acquisitionDate)
            .get()
            .await()
            .toObjects(BookAcquisition::class.java)
    }

    // Trả về danh sách dữ liệu nhập kho do một thủ kho nào đó ghi nhận
    suspend fun getAcquisitionByRecorder(recorderId: String): List<BookAcquisition> = withContext(
        Dispatchers.IO) {
        db.collection("book_acquisition")
            .whereEqualTo("acquiredBy", recorderId)
            .get()
            .await()
            .toObjects(BookAcquisition::class.java)
    }
}
