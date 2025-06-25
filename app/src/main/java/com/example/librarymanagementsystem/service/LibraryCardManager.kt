package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.CardRequest
import com.example.librarymanagementsystem.model.LibraryCard
import com.example.librarymanagementsystem.model.RequestStatus
import com.example.librarymanagementsystem.model.UserStatus
import com.example.librarymanagementsystem.repository.LibraryCardRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Calendar

class LibraryCardManager (
    private val libraryCardRepository: LibraryCardRepository = LibraryCardRepository()
    ) {

    val db = FirebaseFirestore.getInstance()

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

    suspend fun approveCardRequestBatch(request: CardRequest, librarianId: String) = withContext(
    Dispatchers.IO) {
        val batch = db.batch()

        val newCardRef = db.collection("library_cards").document(request.id!!)
        val requestRef = db.collection("card_requests").document(request.id)

        val card = LibraryCard(
            requestId = request.id,
            birthday = request.birthday!!,
            readerId = request.readerId,
            librarianId = librarianId,
            fullName = request.fullName,
            email = request.email,
            type = request.type,
            address = request.address,
            status = UserStatus.ACTIVE.name,
        )

        batch.set(newCardRef, card)
        batch.update(requestRef, "status", RequestStatus.APPROVED.name)

        batch.commit().await() // Dùng coroutine extension
    }

}