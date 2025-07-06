package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.BorrowBook
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class BorrowingRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    suspend fun getAllBorrows(): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    // Lấy danh sách tất cả các bản ghi dữ liệu mượn sách theo trạng thái trong cơ sở dữ liệu
    suspend fun getAllBorrowsByStatus(status: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("status", status)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    suspend fun getBorrowedBooksCountInRange(days: Int? = null): Long = withContext(Dispatchers.IO) {
        val collection = db.collection("borrow_book")

        val query = if (days != null) {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -days)
            val fromDate = calendar.time
            collection.whereGreaterThanOrEqualTo("borrowDate", fromDate)
        } else {
            collection
        }

        val aggregateQuery = query.count()
        val snapshot = aggregateQuery.get(AggregateSource.SERVER).await()
        snapshot.count
    }

    suspend fun getReturnBookCountInRange(days: Int? = null): Long = withContext(Dispatchers.IO) {
        val collection = db.collection("borrow_book")

        val query = if (days != null) {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -days)
            val fromDate = calendar.time

            collection
                .whereNotEqualTo("actualReturnDate", null)
                .whereGreaterThanOrEqualTo("actualReturnDate", fromDate)
        } else {
            collection.whereNotEqualTo("actualReturnDate", null) // All time
        }

        val snapshot = query
            .count()
            .get(AggregateSource.SERVER)
            .await()

        snapshot.count
    }

    // Lấy ra danh sách các sách (bản logic) mà một người dùng mượn
    suspend fun getBorrowBooksByCardAndStatus(libraryCardId: String, status: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("libraryCardId", libraryCardId)
            .whereEqualTo("status", status)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    suspend fun getNumBorrowById (bookId: String) : Int = withContext(Dispatchers.IO){
        val countSnapshot = db.collection("borrow_book")
            .whereEqualTo("bookId", bookId)
            .count()
            .get(AggregateSource.SERVER)  // dùng server để không tính local cache
            .await()

        val result = countSnapshot.count.toInt()

        return@withContext result
    }
}
