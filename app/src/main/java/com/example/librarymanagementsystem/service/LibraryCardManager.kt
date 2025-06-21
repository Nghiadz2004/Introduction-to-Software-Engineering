package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.LibraryCard
import com.example.librarymanagementsystem.repository.LibraryCardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Calendar

class LibraryCardManager (
    private val libraryCardRepository: LibraryCardRepository = LibraryCardRepository()
    ) {
        // Lấy thẻ mới nhất (hiện tại)
        suspend fun getCurrentLibraryCard(readerId: String): LibraryCard? = withContext(Dispatchers.IO) {
            val libraryCardList = libraryCardRepository.getLibraryCardByReader(readerId)

            // Sắp xếp theo ngày tạo giảm dần và lấy phần tử đầu tiên
            return@withContext libraryCardList
                .sortedByDescending { it.createdAt }
                .firstOrNull()
        }

        fun getDueDate(date: Date, months: Int = 6): Date {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(Calendar.MONTH, months)
            return calendar.time
        }

}