package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.model.BookAcquisition
import com.example.librarymanagementsystem.model.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    // Tạo một người dùng mới
    suspend fun createUser(user: User): String = withContext(Dispatchers.IO) {
        // Giả sử user.id là uid đã được cung cấp
        val uid = user.id ?: throw IllegalArgumentException("User ID (UID) must not be null")
        db.collection("users").document(uid).set(user).await()
        uid // Trả về uid làm ID của document
    }

    // Lấy ra danh sách tất cả các người dùng có trong cơ sở dữ liệu
    suspend fun getUsers(userIds :List<String>): List<User> = withContext(Dispatchers.IO) {
        if (userIds.isEmpty()) return@withContext emptyList()

        val users = mutableListOf<User>()
        val chunkedIds = userIds.chunked(10)

        for (chunk in chunkedIds) {
            val snapshot = db.collection("books")
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()
            users.addAll(snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)?.copy(id = doc.id)
            })
        }

        return@withContext users
    }

    // Tìm kiếm người dùng theo Id
    suspend fun getUserById(id: String): User? = withContext(Dispatchers.IO) {
        db.collection("users").document(id).get().await().toObject(User::class.java)
    }

    // Tìm kiếm người dùng theo username
    suspend fun getUserByUsername(username: String): User? = withContext(Dispatchers.IO) {
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .await()
            .toObjects(User::class.java)
            .firstOrNull()
    }

    // Tìm kiếm người dùng theo role
    suspend fun getUserByRoleId(roleId: String): List<User> = withContext(Dispatchers.IO) {
        db.collection("users")
            .whereEqualTo("roleId", roleId)
            .get().await().toObjects(User::class.java)
    }

    // Tìm kiếm người dùng theo trạng thái
    suspend fun getUserByStatus(status: String): List<User> = withContext(Dispatchers.IO) {
        db.collection("users")
            .whereEqualTo("status",status)
            .get()
            .await()
            .toObjects(User::class.java)
    }

    // Tìm kiếm người dùng theo địa chỉ email đã đăng ký
    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        val snapshot = db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()

        return@withContext snapshot.documents.firstOrNull()?.toObject(User::class.java)
    }

    // Cập nhật thông tin người dùng
    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        db.collection("users").document(user.id!!).set(user).await()
    }
}
