package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Officer
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class OfficerRepository(private val db: FirebaseFirestore) {

    suspend fun createOfficer(officer: Officer): String = withContext(Dispatchers.IO) {
        db.collection("officers").add(officer).await().id
    }

    suspend fun getOfficerById(id: String): Officer? = withContext(Dispatchers.IO) {
        db.collection("officers").document(id).get().await().toObject(Officer::class.java)
    }

    suspend fun getOfficerByUsername(username: String): Officer? = withContext(Dispatchers.IO) {
        db.collection("officers")
            .whereEqualTo("username", username)
            .get()
            .await()
            .toObjects(Officer::class.java)
            .firstOrNull()
    }

    suspend fun getAllOfficers(): List<Officer> = withContext(Dispatchers.IO) {
        db.collection("officers").get().await().toObjects(Officer::class.java)
    }
}
