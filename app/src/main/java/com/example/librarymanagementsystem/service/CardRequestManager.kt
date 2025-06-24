package com.example.librarymanagementsystem.service

import com.example.librarymanagementsystem.model.CardRequest
import com.example.librarymanagementsystem.model.LibraryCard
import com.example.librarymanagementsystem.model.RequestStatus
import com.example.librarymanagementsystem.repository.CardRequestRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Calendar

fun calculateAge(birthday: Date): Int {
    val today = Calendar.getInstance()
    val birthDate = Calendar.getInstance()
    birthDate.time = birthday

    var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

    // Nếu chưa đến ngày sinh trong năm nay thì trừ đi 1
    if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
        age--
    }

    return age
}


class CardRequestManager (
    private val cardRequestRepository: CardRequestRepository
) {
    val db = FirebaseFirestore.getInstance()

    // Từ chối yêu cầu lập thẻ của người dùng
    suspend fun rejectRequest(request: CardRequest, librarianId: String) {
        // Lưu request vào Firestore
        cardRequestRepository.updateRequestStatus(request.id!!, RequestStatus.REJECTED)
    }

    // Xác nhận yêu cầu lập thẻ của người dùng
    suspend fun approveRequestBatch(request: CardRequest, librarianId: String) = withContext(
        Dispatchers.IO) {
        val batch = db.batch()

        val newCardRef = db.collection("library_cards").document()
        val card = LibraryCard(
            readerId = request.readerId,
            librarianId = librarianId,
            birthday = request.birthday!!,
            createdAt = Date()
        )
        val requestRef = db.collection("card_requests").document(request.id!!)

        batch.set(newCardRef, card)
        batch.update(requestRef, "status", RequestStatus.APPROVED.name)

        batch.commit().await() // Dùng coroutine extension
    }


    // Kiểm tra logic tạo thẻ và tạo thẻ nếu thỏa điều kiện
    suspend fun createRequest(request: CardRequest): Boolean = withContext(Dispatchers.IO) {
        val age = calculateAge(request.birthday!!)

        if (age !in 15..55) {
            return@withContext false // Tuổi không hợp lệ
        }

        // Nếu có lỗi trong submitCardRequest, exception sẽ được ném ra ngoài
        cardRequestRepository.submitCardRequest(request)

        return@withContext true // Tạo thành công
    }
}
