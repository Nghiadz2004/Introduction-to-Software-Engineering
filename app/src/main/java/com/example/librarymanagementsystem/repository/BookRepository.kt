package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Book
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class BookRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    // Thêm một quyển sách mới vào cơ sở dữ liệu
    suspend fun addBook(book: Book): String = withContext(Dispatchers.IO) {
        val docRef = db.collection("books").add(book).await()
        docRef.id
    }

    // Lấy danh sách tất cả các sách (bản logic) có trong cơ sở dữ liệu
    suspend fun getBooks(): List<Book> = withContext(Dispatchers.IO) {
        db.collection("books").get().await().toObjects(Book::class.java)
    }

    // Tìm kiếm sách theo Id
    suspend fun getBookById(bookId: String): Book? = withContext(Dispatchers.IO) {
        db.collection("books")
          .document(bookId)
          .get().await().toObject(Book::class.java)
    }

    // Cập nhật thông tin số lượng sách
    suspend fun updateBookQuantity(bookId: String, newQuantity: Int) = withContext(Dispatchers.IO) {
        db.collection("books").document(bookId).update("quantity", newQuantity).await()
    }

    // Tìm kiếm sách theo tác giả
    suspend fun getBookByAuthor(author: String): List<Book> = withContext(Dispatchers.IO) {
        db.collection("books")
          .whereEqualTo("author", author)
          .get().await().toObjects(Book::class.java)
    }

    // Tìm kiếm sách theo tựa đề
    suspend fun getBookByTitle(title: String): List<Book> = withContext(Dispatchers.IO) {
        db.collection("books")
            .whereEqualTo("title", title)
            .get().await().toObjects(Book::class.java)
    }

    // Tìm kiếm sách theo thể loại
    suspend fun getBookByCategory(category: String): List<Book> = withContext(Dispatchers.IO) {
        db.collection("books")
            .whereEqualTo("category", category)
            .get().await().toObjects(Book::class.java)
    }

    // Tìm kiếm sách theo năm xuất bản
    suspend fun getBookByPublishYear(publishYear: Int): List<Book> = withContext(Dispatchers.IO) {
        db.collection("books")
            .whereEqualTo("publishYear", publishYear)
            .get().await().toObjects(Book::class.java)
    }

    // Tìm kiếm sách theo nhà xuất bản
    suspend fun getBookByPublisher(publisher: String): List<Book> = withContext(Dispatchers.IO) {
        db.collection("books")
            .whereEqualTo("publisher", publisher)
            .get().await().toObjects(Book::class.java)
    }

    // Lấy danh sách các sách theo danh sách Id
    suspend fun getBooksByIds(bookIds: List<String>): List<Book> = withContext(Dispatchers.IO) {
        if (bookIds.isEmpty()) return@withContext emptyList()

        val books = mutableListOf<Book>()
        val chunkedIds = bookIds.chunked(10)

        for (chunk in chunkedIds) {
            val snapshot = db.collection("books")
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()
            books.addAll(snapshot.documents.mapNotNull { doc ->
                doc.toObject(Book::class.java)?.copy(id = doc.id)
            })
        }

        return@withContext books
    }

    suspend fun searchBooksByKeyword(keyword: String): List<Book> {
        val db = FirebaseFirestore.getInstance()
        val keywordLower = keyword.lowercase()

        val snapshot = db.collection("books").get().await()
        return snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
            .filter { book ->
                book.title.contains(keywordLower, ignoreCase = true) ||
                        (book.author?.contains(keywordLower, ignoreCase = true) == true)
            }
    }
}
