package com.example.libmanagementsystem.service

import com.example.libmanagementsystem.model.Book
import com.example.libmanagementsystem.repository.BookRepository
import com.example.libmanagementsystem.repository.FavoriteRepository
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
