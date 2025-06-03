package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.BorrowBook
import com.example.librarymanagementsystem.model.LostBook
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class BorrowingRepository(private val db: FirebaseFirestore) {

    suspend fun addBorrowBook(borrow: BorrowBook): String = withContext(Dispatchers.IO) {
        db.collection("borrow_book").add(borrow).await().id
    }

    suspend fun returnBook(borrowId: String, returnDate: Date) = withContext(Dispatchers.IO) {
        db.collection("borrow_book").document(borrowId)
            .update("actualReturnDate", returnDate).await()
    }

    suspend fun getBorrowBooksByCard(libraryCardId: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("libraryCardId", libraryCardId)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    suspend fun removeFromBorrow(libraryCardId: String, readerId: String, borrowDate: Date) = withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        val querySnapshot = db.collection("borrow_book")
            .whereEqualTo("bookId", libraryCardId)
            .whereEqualTo("readerId", readerId)
            .whereEqualTo("borrowDate", borrowDate)
            .get()
            .await()

        for (document in querySnapshot.documents) {
            db.collection("borrow_book").document(document.id).delete().await()
        }
    }

    suspend fun getBorrowBooksByBook(libraryCardId: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("bookId", libraryCardId)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    suspend fun getBorrowBooksByReader(readerId: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("readerId", readerId)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    suspend fun getBorrowBooksByCopy(copyId: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("copyId", copyId)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    suspend fun getBorrowBooksByBorrowDate(borrowDate: Date): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("borrowDate",borrowDate)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    suspend fun getBorrowBooksByConfirmDate(confirmDate: Date): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("confirmDate",confirmDate)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    suspend fun getBorrowBooksByActualReturnDate(actualReturnDate: Date): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("actualReturnDate",actualReturnDate)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    suspend fun getBorrowBooksByRecorder(recorderId: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("recorderId",recorderId)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }
}
