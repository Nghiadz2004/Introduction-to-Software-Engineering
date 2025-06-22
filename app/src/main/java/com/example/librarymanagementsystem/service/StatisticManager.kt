package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.BorrowBook
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.repository.FineRepository
import com.example.librarymanagementsystem.repository.LostBookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatisticManager {
    suspend fun getTotalBooks(): Long {
        return BookRepository().getAllBookCount()
    }

    suspend fun getTotalBooksLent(days: Int? = null): Long {
        return BorrowingRepository().getBorrowedBooksCountInRange(days)
    }

    suspend fun getTotalReturnBooks(days: Int? = null): Long {
        return BorrowingRepository().getReturnBookCountInRange(days)
    }

    suspend fun getTotalLostBooks(days: Int? = null): Long {
        return LostBookRepository().getConfirmedLostBookCountInRange(days)
    }

    suspend fun getTotalFine(days: Int? = null): Long {
        return FineRepository().getTotalFineInRange(days)
    }
}