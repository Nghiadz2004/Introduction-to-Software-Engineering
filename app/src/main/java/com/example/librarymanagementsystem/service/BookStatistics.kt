package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository

class BookStatistics(
    private val borrowRepository: BorrowingRepository,
    private val bookRepository: BookRepository
) {
    suspend fun getNumBorrowByBook () : List<Pair<Book, Int>> {
        val allBorrows = borrowRepository.getAllBorrows()
        val allBooks = bookRepository.getBooks()

        val borrowCountMap = allBorrows.groupingBy { it.bookId }.eachCount()

        val booksWithBorrowCount = allBooks.map { book ->
            val count = borrowCountMap[book.id] ?: 0
            Pair(book, count)
        }

        return booksWithBorrowCount
    }
}