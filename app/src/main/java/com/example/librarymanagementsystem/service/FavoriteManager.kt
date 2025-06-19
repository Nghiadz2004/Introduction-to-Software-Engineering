package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.cache.FavoriteCache
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavoriteManager(
    private val bookRepository: BookRepository
) {
    // Lấy danh sách các quyển sách (bản logic) yêu thích của một độc giả
    suspend fun getFavoriteBooks(): List<Book> = withContext(Dispatchers.IO) {
        return@withContext bookRepository.getBooksByIds(FavoriteCache.favoriteBookIds.toList())
    }
}
