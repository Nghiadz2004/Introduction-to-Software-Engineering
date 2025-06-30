package com.example.librarymanagementsystem.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.model.QueueDisplay
import com.example.librarymanagementsystem.repository.BookCopyRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.repository.RequestBorrowRepository
import com.example.librarymanagementsystem.service.BorrowBookManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QueueAdapter(
    private var queues: List<QueueDisplay>,
    private val librarianId: String,
    private val onQueueChanged: suspend () -> Unit
) : RecyclerView.Adapter<QueueAdapter.QueueViewHolder>() {

    inner class QueueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCover: ImageView = itemView.findViewById(R.id.imgCover)
        val qBook: TextView = itemView.findViewById(R.id.qBook)
        val qAuthor: TextView = itemView.findViewById(R.id.qAuthor)
        val qCopyLeft: TextView = itemView.findViewById(R.id.qCopyLeft)
        val qReader: TextView = itemView.findViewById(R.id.qReader)
        val qDays: TextView = itemView.findViewById(R.id.qDays)
        val btnApprove: AppCompatButton = itemView.findViewById(R.id.btnApprove)
        val btnReject: AppCompatButton = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_queue, parent, false)
        return QueueViewHolder(view)
    }

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        val queue = queues[position]
        holder.qBook.text = queue.title
        holder.qAuthor.text = "Author: ${queue.author ?: "Unknown"}"
        holder.qCopyLeft.text = "Copy left: ${queue.copyLeft}"
        holder.qReader.text = "Borrowed by: ${queue.readerName}"
        holder.qDays.text = "Borrow period: ${queue.daysBorrow}"

        Glide.with(holder.itemView.context)
            .load(queue.coverUrl)
            .placeholder(R.drawable.harry_potter_cover)
            .into(holder.imgCover)

        // Set màu và trạng thái nút Approve
        val context = holder.itemView.context

        holder.btnApprove.isEnabled = queue.canApprove
        if (holder.btnApprove.isEnabled) {
            holder.btnApprove.backgroundTintList = ContextCompat.getColorStateList(context, R.color.green)
        }
        else {
            holder.btnApprove.backgroundTintList = ContextCompat.getColorStateList(context, R.color.light_gray)
        }

        // Bắt sự kiện Approve
        holder.btnApprove.setOnClickListener {
            if (!queue.canApprove) {
                Toast.makeText(context, "All copies of the book have been borrowed or the reader is not eligible to borrow", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loadingDialog = LoadingDialog(context)
            loadingDialog.show()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val copyRepo = BookCopyRepository()
                    val availableCopy = withContext(Dispatchers.IO) {
                        copyRepo.getFirstBookCopiesByStatus(queue.bookId, "AVAILABLE")
                    }

                    if (availableCopy != null) {
                        val manager = BorrowBookManager(
                            RequestBorrowRepository(),
                            BorrowingRepository()
                        )
                        // Gọi hàm approveBorrowRequestBatch
                        withContext(Dispatchers.IO) {
                            manager.approveBorrowRequestBatch(queue.request, librarianId, availableCopy.copyId!!)
                        }
                        // Sau khi approve xong, gọi onQueueChanged để refresh dữ liệu
                        onQueueChanged()
                        // Chỉ đóng dialog sau khi MỌI THAO TÁC BẤT ĐỒNG BỘ ĐÃ HOÀN TẤT
                        loadingDialog.dismiss()
                        Toast.makeText(context, "Book borrowing request approved", Toast.LENGTH_SHORT).show()
                    } else {
                        // Nếu không có bản sao nào, đóng dialog và thông báo
                        loadingDialog.dismiss()
                        Toast.makeText(context, "No available copies found for this book.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("QueueAdapter", "Error approving request: ${e.message}", e)
                    // Đóng dialog khi có lỗi
                    loadingDialog.dismiss()
                    Toast.makeText(context, "Failed to approve request: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Bắt sự kiện Reject
        holder.btnReject.setOnClickListener {
            val loadingDialog = LoadingDialog(context)
            loadingDialog.show()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val manager = BorrowBookManager(
                        RequestBorrowRepository(),
                        BorrowingRepository()
                    )
                    // Gọi hàm rejectBorrowRequest
                    withContext(Dispatchers.IO) {
                        manager.rejectBorrowRequest(queue.request, librarianId)
                    }
                    // Sau khi reject xong, gọi onQueueChanged để refresh dữ liệu
                    onQueueChanged()
                    // Chỉ đóng dialog sau khi MỌI THAO TÁC BẤT ĐỒNG BỘ ĐÃ HOÀN TẤT
                    loadingDialog.dismiss()
                    Toast.makeText(context, "Book borrowing request rejected", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e("QueueAdapter", "Error rejecting request: ${e.message}", e)
                    // Đóng dialog khi có lỗi
                    loadingDialog.dismiss()
                    Toast.makeText(context, "Failed to reject request: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = queues.size
}