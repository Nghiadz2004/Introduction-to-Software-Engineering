package com.example.libmanagementsystem.service

import com.example.libmanagementsystem.model.Book
import com.example.libmanagementsystem.repository.BookRepository
import com.example.libmanagementsystem.repository.BorrowingRepository

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