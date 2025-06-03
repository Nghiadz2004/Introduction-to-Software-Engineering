package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Role
import com.example.librarymanagementsystem.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RoleRepository(private val db: FirebaseFirestore) {
    suspend fun createRole(role: Role): String = withContext(Dispatchers.IO) {
        db.collection("roles")
            .document(role.id) // Tự set ID ở đây
            .set(role)
            .await()
        role.id
    }

    suspend fun getRoles(): List<User> = withContext(Dispatchers.IO) {
        db.collection("roles").get().await().toObjects(User::class.java)
    }

    suspend fun getRoleById(id: String): User? = withContext(Dispatchers.IO) {
        db.collection("roles").document(id).get().await().toObject(User::class.java)
    }
}