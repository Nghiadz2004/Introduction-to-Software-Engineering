package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BookRepository(private val db: FirebaseFirestore) {

    suspend fun addBook(book: Book): String = withContext(Dispatchers.IO) {
        val docRef = db.collection("books").add(book).await()
        docRef.id
    }

    suspend fun getBooks(): List<Book> = withContext(Dispatchers.IO) {
        db.collection("books").get().await().toObjects(Book::class.java)
    }

    suspend fun getBookById(bookId: String): Book? = withContext(Dispatchers.IO) {
        val doc = db.collection("books").document(bookId).get().await()
        doc.toObject(Book::class.java)
    }

    suspend fun updateBookQuantity(bookId: String, newQuantity: Int) = withContext(Dispatchers.IO) {
        db.collection("books").document(bookId).update("quantity", newQuantity).await()
    }
}
