package com.example.librarymanagementsystem.service

import android.util.Log
import com.example.librarymanagementsystem.cache.LibraryCardCache
import com.example.librarymanagementsystem.model.BorrowBook
import com.example.librarymanagementsystem.model.BorrowRequest
import com.example.librarymanagementsystem.model.BorrowStatus
import com.example.librarymanagementsystem.model.RequestStatus
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.repository.LibraryCardRepository
import com.example.librarymanagementsystem.repository.RequestBorrowRepository
import com.example.librarymanagementsystem.repository.fetchServerTime
import com.google.firebase.Timestamp
import com.google.firebase.firestore.AggregateSource
import java.util.Calendar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Kết quả kèm thông điệp (null nếu thành công).
 */
data class CheckResult(
    val ok: Boolean,
    val message: String? = null
)

class BorrowBookManager (
    private val borrowRequestRepository: RequestBorrowRepository,
    private val borrowRepository: BorrowingRepository
) {
    val db = FirebaseFirestore.getInstance()

    // Xác nhận yêu cầu mượn sách của người dùng
    suspend fun approveBorrowRequestBatch(request: BorrowRequest, librarianId: String, copyId: String) = withContext(
        Dispatchers.IO) {
        val batch = db.batch()

        val newBorrowRef = db.collection("borrow_book").document(request.id!!)
        val copyRef = db.collection("books").document(request.bookId).collection("book_copy").document(copyId)
        val requestRef = db.collection("request_borrow").document(request.id)

        val confirmDate = fetchServerTime()
        val expectedReturnDate = if (confirmDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = confirmDate
            calendar.add(Calendar.DAY_OF_YEAR, request.daysBorrow + 1) // Cộng số ngày mượn vào borrowDate
            calendar.time // Trả về ngày sau khi cộng
        } else {
            null // Trường hợp borrowDate null thì trả về null
        }

        val borrowBook = BorrowBook(
            requestId = request.id,
            libraryCardId = request.libraryCardId,
            copyId = copyId,
            readerId = request.readerId,
            bookId = request.bookId,
            borrowDate = request.borrowDate,
            librarianId = librarianId,
            expectedReturnDate = expectedReturnDate, // Sử dụng ngày đã cộng
            status = BorrowStatus.BORROWED.name,
            category = request.category
        )


        batch.set(newBorrowRef, borrowBook)
        batch.update(requestRef, "status", RequestStatus.APPROVED.name)
        batch.update(copyRef, "status", "BORROWED")

        batch.commit().await() // Dùng coroutine extension
    }

    // Từ chối yêu cầu mượn sách của người dùng
    suspend fun rejectBorrowRequest(request: BorrowRequest, librarianId: String) = withContext(
        Dispatchers.IO) {
        borrowRequestRepository.updateRequestBorrowStatus(request.id!!, RequestStatus.REJECTED)
    }

    // Kiểm tra điều kiện mượn phía độc giả
    /**
     * Kiểm tra xem độc giả còn được mượn sách không
     * (giới hạn: tối đa 5 cuốn trong 4 ngày gần nhất).
     *
     * @param readerId  ID của độc giả
     * @return          true nếu ĐK còn mượn được, false nếu đã vượt giới hạn
     */
    suspend fun canBorrowMore(readerId: String): CheckResult = withContext(Dispatchers.IO) {
        val fourDaysAgo = Timestamp(Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(4)))

        val countSnapshot = db.collection("borrow_book")
            .whereEqualTo("readerId", readerId)
            .whereEqualTo("status", "BORROWED")
            .whereGreaterThanOrEqualTo("borrowDate", fourDaysAgo)
            .count()
            .get(AggregateSource.SERVER)
            .await()

        val borrowCount = countSnapshot.count.toInt()

        if (borrowCount >= 5) {
            return@withContext CheckResult(
                ok = false,
                message = "Bạn đã mượn tối đa 5 cuốn trong 4 ngày gần nhất. Vui lòng trả bớt hoặc đợi thêm."
            )
        }

        CheckResult(ok = true)
    }

    suspend fun isLibraryCardValid(readerId: String): CheckResult = withContext(Dispatchers.IO) {
        val card = LibraryCardCache.libraryCard

        Log.d("BorrowBookManager", "checkCard: ${card}")

        if (card == null) {
            return@withContext CheckResult(false, "Vui lòng đăng ký thẻ thư viện trước khi mượn sách")
        }

        val now = Timestamp.now().toDate()

        if (card.getDueDate() <= now) {
            return@withContext CheckResult(false, "Thẻ thư viện của bạn đã hết hạn. Vui lòng đăng ký thẻ mới.")
        }

        if (card.status == "INACTIVE") {
            return@withContext CheckResult(false, "Thẻ thư viện của bạn hiện không hoạt động. Vui lòng liên hệ thủ thư.")
        }

        if (card.status == "BANNED") {
            return@withContext CheckResult(false, "Thẻ thư viện của bạn đã bị khoá. Vui lòng liên hệ thủ thư.")
        }

        CheckResult(ok = true)
    }

    suspend fun checkBorrowEligibility(readerId: String): CheckResult = coroutineScope {
        try {
            val card   = async { isLibraryCardValid(readerId) }
            val limit  = async { canBorrowMore(readerId) }

            // 1. Thẻ
            val cardRes = card.await()
            if (!cardRes.ok) return@coroutineScope cardRes

            // 2. Giới hạn
            val limitRes = limit.await()
            if (!limitRes.ok) return@coroutineScope limitRes

            CheckResult(true)          // ✅ tất cả OK
        } catch (e: Exception) {
            CheckResult(false, "Đã xảy ra lỗi, vui lòng thử lại sau!")
        }
    }
}