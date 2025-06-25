package com.example.librarymanagementsystem.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

suspend fun fetchServerTime(): Date? = withContext(Dispatchers.IO) {
    val firestore = FirebaseFirestore.getInstance()
    val docRef = firestore.collection("utils").document("server_time")

    try {
        docRef.set(mapOf("timestamp" to FieldValue.serverTimestamp())).await()
        val snapshot = docRef.get().await()
        return@withContext snapshot.getTimestamp("timestamp")?.toDate()
    } catch (e: Exception) {
        return@withContext null
    }
}
