package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(private val db: FirebaseFirestore) {

    suspend fun createUser(user: User): String = withContext(Dispatchers.IO) {
        db.collection("users").add(user).await().id
    }

    suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        db.collection("users").get().await().toObjects(User::class.java)
    }

    suspend fun getUserById(id: String): User? = withContext(Dispatchers.IO) {
        db.collection("users").document(id).get().await().toObject(User::class.java)
    }

    suspend fun getUserByUsername(username: String): User? = withContext(Dispatchers.IO) {
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .await()
            .toObjects(User::class.java)
            .firstOrNull()
    }

    suspend fun getUserByRoleId(roleId: String): List<User> = withContext(Dispatchers.IO) {
        db.collection("users")
            .whereEqualTo("roleId", roleId)
            .get().await().toObjects(User::class.java)
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        db.collection("users").document(user.id!!).set(user).await()
    }
}
