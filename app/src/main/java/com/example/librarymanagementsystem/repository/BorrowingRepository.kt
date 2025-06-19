package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.BorrowBook
import com.example.librarymanagementsystem.model.LostBook
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class BorrowingRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {
    // Lấy danh sách tất cả các bản ghi dữ liệu mượn sách trong cơ sở dữ liệu
    suspend fun getAllBorrows(): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book").get().await().toObjects(BorrowBook::class.java)
    }

    suspend fun getBorrowCountByBookIds(bookIds: List<String>): Map<String, Int> = withContext(Dispatchers.IO) {
        val result = mutableMapOf<String, Int>()

        for (id in bookIds) {
            val countSnapshot = db.collection("borrow_book")
                .whereEqualTo("bookId", id)
                .count()
                .get(AggregateSource.SERVER)  // dùng server để không tính local cache
                .await()

            result[id] = countSnapshot.count.toInt()
        }

        return@withContext result
    }

    // Thêm một quyển sách được mượn vào cơ sở dữ liệu
    suspend fun addBorrowBook(borrow: BorrowBook): String = withContext(Dispatchers.IO) {
        db.collection("borrow_book").add(borrow).await().id
    }

    // Cập nhật lại ngày trả của sách
    suspend fun returnBook(borrowId: String, returnDate: Date) = withContext(Dispatchers.IO) {
        db.collection("borrow_book").document(borrowId)
            .update("actualReturnDate", returnDate).await()
    }

    // Lấy danh sách các sách được mượn (all-time) bởi một thẻ thư viện
    suspend fun getBorrowBooksByCard(libraryCardId: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("libraryCardId", libraryCardId)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    // Xóa một bản ghi dữ liệu mượn sách ra khỏi cơ sở dữ liệu
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

    // Lấy ra danh sách lượt mượn (all-time) đối với 1 cuốn sách
    suspend fun getBorrowByBook(bookId: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("bookId", bookId)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    // Lấy ra danh sách các sách (bản logic) mà một người dùng mượn
    suspend fun getBorrowBooksByReader(readerId: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("readerId", readerId)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    // Lấy ra danh sách các người dùng đã mượn một quyển sách (bản vật lý)
    suspend fun getBorrowBooksByCopy(copyId: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("copyId", copyId)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    // Lấy ra danh sách các quyển sách (bản vật lý) đã được mượn trong một ngày
    suspend fun getBorrowBooksByBorrowDate(borrowDate: Date): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("borrowDate",borrowDate)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    // Lấy ra danh sách các quyển sách (bản vật lý) đã được xác nhận cho mượn trong một ngày
    suspend fun getBorrowBooksByConfirmDate(confirmDate: Date): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("confirmDate",confirmDate)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    // Lấy ra danh sách các quyển sách (bản vật lý) đã được xác nhận trả trong một ngày
    suspend fun getBorrowBooksByActualReturnDate(actualReturnDate: Date): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("actualReturnDate",actualReturnDate)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    // Lấy ra danh sách các quyển sách (bản vật lý) đã được xác nhận cho mượn bởi một thủ thư
    suspend fun getBorrowBooksByRecorder(recorderId: String): List<BorrowBook> = withContext(Dispatchers.IO) {
        db.collection("borrow_book")
            .whereEqualTo("recorderId",recorderId)
            .get()
            .await()
            .toObjects(BorrowBook::class.java)
    }

    suspend fun getNumBorrowById (bookId: String) : Int = withContext(Dispatchers.IO){
        val countSnapshot = db.collection("borrow_book")
            .whereEqualTo("bookId", bookId)
            .count()
            .get(AggregateSource.SERVER)  // dùng server để không tính local cache
            .await()

        val result = countSnapshot.count.toInt()

        return@withContext result
    }
}
