package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.FavoriteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteManager(
    private val favoriteRepository: FavoriteRepository,
    private val bookRepository: BookRepository
) {
    // Lấy danh sách các quyển sách (bản logic) yêu thích của một độc giả
    suspend fun getFavoriteBooks(readerId: String): List<Book> = withContext(Dispatchers.IO) {
        val favorite = favoriteRepository.getFavoriteBooksId(readerId)
        val bookIds = favorite?.bookIdList ?: emptyList()
        return@withContext bookRepository.getBooksByIds(bookIds)
    }
}
