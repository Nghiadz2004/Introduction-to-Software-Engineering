package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.CardRequest
import com.example.librarymanagementsystem.model.LibraryCard
import com.example.librarymanagementsystem.model.RequestStatus
import com.example.librarymanagementsystem.repository.CardRequestRepository
import com.example.librarymanagementsystem.repository.LibraryCardRepository
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class CardRequestManager (
    private val libraryCardRepository: LibraryCardRepository,
    private val cardRequestRepository: CardRequestRepository
) {
    val db = FirebaseFirestore.getInstance()

    suspend fun rejectRequest(request: CardRequest, librarianId: String) {
        // Lưu request vào Firestore
        cardRequestRepository.updateRequestStatus(request.id!!, RequestStatus.REJECTED, librarianId)
    }

    suspend fun approveRequest(request: CardRequest, librarianId: String) {
        val card = LibraryCard(
            readerId = request.readerId, // đổi nếu readerId là String
            librarianId = librarianId,
            birthday = request.birthday,
            createdAt = request.submittedAt
        )

        // Lưu card vào Firestore
        libraryCardRepository.createLibraryCard(card)
        // Lưu request vào Firestore
        cardRequestRepository.updateRequestStatus(request.id!!, RequestStatus.APPROVED, librarianId)
    }
}
