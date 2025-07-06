package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.repository.FineRepository
import com.example.librarymanagementsystem.repository.LostBookRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class StatisticManager (private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    suspend fun getTotalBooks(): Long {
        return BookRepository().getAllBookCount()
    }

    suspend fun getTotalBooksLent(days: Int? = null): Long {
        return BorrowingRepository().getBorrowedBooksCountInRange(days)
    }

    suspend fun getTotalReturnBooks(days: Int? = null): Long {
        return BorrowingRepository().getReturnBookCountInRange(days)
    }

    suspend fun getTotalLostBooks(days: Int? = null): Long {
        return LostBookRepository().getConfirmedLostBookCountInRange(days)
    }

    suspend fun getTotalFine(days: Int? = null): Long {
        return FineRepository().getTotalFineInRange(days)
    }

    // @param offset 0 = tháng hiện tại, -1 = tháng trước, -2 = 2 tháng trước,...
    fun getMonthDateRange(offset: Int = 0): Pair<Date, Date> {
        val calendar = Calendar.getInstance()

        // Đặt về ngày đầu tháng hiện tại
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // Trừ tháng nếu cần
        calendar.add(Calendar.MONTH, offset)
        val startDate = calendar.time

        // Tăng 1 tháng để được endDate
        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.time

        return Pair(startDate, endDate)
    }

    suspend fun countCategory(startDate: Date, endDate: Date): Map<String, Int> = withContext(Dispatchers.IO) {
        val result = mutableMapOf<String, Int>()

        try {
            val snapshot = db.collection("borrow_book")
                .whereGreaterThanOrEqualTo("borrowDate", startDate)
                .whereLessThanOrEqualTo("borrowDate", endDate)
                .get()
                .await()

            for (doc in snapshot.documents) {
                val category = doc.getString("category") ?: continue
                result[category] = result.getOrDefault(category, 0) + 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext result
    }
}