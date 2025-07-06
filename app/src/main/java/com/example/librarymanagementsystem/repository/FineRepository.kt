package com.example.librarymanagementsystem.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class FineRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

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
}

