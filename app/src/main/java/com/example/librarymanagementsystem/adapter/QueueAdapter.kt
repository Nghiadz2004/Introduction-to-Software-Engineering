package com.example.librarymanagementsystem.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.model.QueueDisplay
import com.example.librarymanagementsystem.repository.BookCopyRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.repository.RequestBorrowRepository
import com.example.librarymanagementsystem.service.BorrowBookManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        val qStatus: TextView = itemView.findViewById(R.id.qStatus)
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
        holder.qStatus.text = "Status: ${queue.status}"

        Glide.with(holder.itemView.context)
            .load(queue.coverUrl)
            .placeholder(R.drawable.harry_potter_cover)
            .into(holder.imgCover)

        // Set màu và trạng thái nút Approve
        val context = holder.itemView.context
        if (queue.copyLeft == 0) {
            holder.btnApprove.isEnabled = false
            holder.btnApprove.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray))
        } else {
            holder.btnApprove.isEnabled = true
            holder.btnApprove.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
        }

        // Bắt sự kiện Approve
        holder.btnApprove.setOnClickListener {
            if (queue.copyLeft > 0) {
                // Tìm copyId đầu tiên có thể dùng
                CoroutineScope(Dispatchers.Main).launch {
                    val copyRepo = BookCopyRepository()
                    val availableCopy = copyRepo.getFirstBookCopiesByStatus(queue.bookId, "AVAILABLE")

                    if (availableCopy != null) {
                        val manager = BorrowBookManager(
                            RequestBorrowRepository(),
                            BorrowingRepository()
                        )
                        manager.approveBorrowRequestBatch(queue.request, librarianId, availableCopy.copyId!!)
                        onQueueChanged()
                    }
                }
            }
        }

        // Bắt sự kiện Reject
        holder.btnReject.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val manager = BorrowBookManager(
                    RequestBorrowRepository(),
                    BorrowingRepository()
                )
                manager.rejectBorrowRequest(queue.request, librarianId)
                onQueueChanged()
            }
        }
    }

    override fun getItemCount(): Int = queues.size
}