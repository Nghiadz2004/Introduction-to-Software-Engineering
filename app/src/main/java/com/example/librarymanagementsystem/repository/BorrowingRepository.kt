package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.BorrowBook
import com.example.librarymanagementsystem.model.BorrowStatus
import com.example.librarymanagementsystem.model.LostBook
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class BorrowingRepository(private val db: FirebaseFirestore) {

    suspend fun borrowBook(borrow: BorrowBook): String = withContext(Dispatchers.IO) {
        db.collection("borrow_book").add(borrow).await().id
    }

    suspend fun returnBook(borrowId: String, returnDate: Date) = withContext(Dispatchers.IO) {
        db.collection("borrow_book").document(borrowId)
            .update(
                "returnDate", returnDate
            ).await()
    }

    suspend fun getBorrowedBooksByCard(libraryCardId: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("libraryCardId", libraryCardId)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    suspend fun reportLostBook(lostBook: LostBook): String = withContext(Dispatchers.IO) {
        db.collection("lost_books").add(lostBook).await().id
    }
}
