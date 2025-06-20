package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.model.BookAcquisition
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class AcquisitionRepository(private val db: FirebaseFirestore) {

    // Ghi nhận một quyển sách mới được nhập vào kho
    suspend fun recordAcquisition(bookId: String, copyId: String? = null, recorderId: String): String = withContext(Dispatchers.IO) {
        val data = mapOf(
            "bookId" to bookId,
            "copyId" to copyId,
            "acquiredBy" to recorderId,
            "acquisitionDate" to FieldValue.serverTimestamp()
        )

        db.collection("book_acquisitions").add(data).await().id
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
