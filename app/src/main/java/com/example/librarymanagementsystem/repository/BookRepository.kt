package com.example.librarymanagementsystem.repository

import com.example.librarymanagementsystem.model.Book
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

fun tokenize(title: String, author: String): List<String> {
    val text = "$title $author".lowercase()
    val cleanText = text.replace(Regex("[^\\w\\sÀ-ỹ]"), "")
    val words = cleanText.split("\\s+".toRegex())

    return words
        .distinct() // loại trùng nhưng giữ thứ tự xuất hiện đầu tiên
        .sortedByDescending { it.length } // sắp xếp theo độ dài giảm dần
}


class BookRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    // Thêm một quyển sách mới vào cơ sở dữ liệu
    suspend fun addBook(book: Book): String = withContext(Dispatchers.IO) {
        // 1. Thêm document sách
        val bookRef = db.collection("books").add(book).await()
        val bookId = bookRef.id

        // 2. Tạo batch để thêm các bản sao
        val batch = db.batch()
        val bookCopiesRef = bookRef.collection("book_copy")

        for (i in 1..book.quantity!!) {
            val copyRef = bookCopiesRef.document() // Auto-id
            val copyData = mapOf(
                "status" to "AVAILABLE"
            )
            batch.set(copyRef, copyData)
        }

        // 3. Thêm các từ khóa vào collection "keyword"
        val keyword = tokenize(book.title, book.author!!)
        val keywordRef = bookRef.collection("keyword")

        for (word in keyword) {
            val wordRef = keywordRef.document(word)
            val wordData = mapOf(
                "word" to word
            )
            batch.set(wordRef, wordData)
        }

        // 4. Commit batch
        batch.commit().await()

        // 5. Trả về id của book
        bookId
    }


    // Lấy danh sách tất cả các sách (bản logic) có trong cơ sở dữ liệu
    suspend fun getBooks(): List<Book> = withContext(Dispatchers.IO) {
        db.collection("books").get().await().toObjects(Book::class.java)
    }

    suspend fun getAllBookCount(): Long = withContext(Dispatchers.IO) {
        val countResult = db.collection("books")
            .count()
            .get(AggregateSource.SERVER)
            .await()

        countResult.count
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

        val words = keyword
            .lowercase()
            .replace(Regex("[^a-zA-Z0-9\\sÀ-ỹ]"), "") // giữ lại tiếng Việt, số, chữ cái
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .distinct() // loại trùng trước
            .sortedByDescending { it.length }

        if (words[0] == "")
        {
            return emptyList()
        }
        val snapshot = db.collectionGroup("keyword")
            .whereEqualTo("word", words[0])
            .get()
            .await()

        val parentDocs = snapshot.documents.mapNotNull { it.reference.parent.parent }

        // Lấy dữ liệu các parent document (books)
        val bookSnapshots: List<DocumentSnapshot> = parentDocs.map { it.get().await() }

        if(words.size < 2) {
            // Chuyển DocumentSnapshot sang Book
            return bookSnapshots.mapNotNull { doc ->
                if (doc.exists()) {
                    doc.toObject(Book::class.java)
                } else null
            }
        }

        val books = mutableListOf<Book>()
        books.addAll(bookSnapshots.mapNotNull { doc ->
            if (doc.exists()) {
                doc.toObject(Book::class.java)
            } else null
        })
        var filteredBooks = books.toMutableList()

        for (i in 1..<words.size) {
            filteredBooks = filteredBooks.filter { book ->
                book.title.contains(words[i], ignoreCase = true) ||
                        book.author!!.contains(words[i], ignoreCase = true)
            }.toMutableList()

            if (filteredBooks.isEmpty()) {
                break
            }
        }
        return filteredBooks
    }


}
