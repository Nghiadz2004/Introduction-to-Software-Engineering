package com.example.librarymanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.model.QueueDisplay

class QueueAdapter(private val queues: List<QueueDisplay>) :
    RecyclerView.Adapter<QueueAdapter.QueueViewHolder>() {

    class QueueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

        // Sự kiện xử lý duyệt và từ chối sẽ thêm sau
    }

    override fun getItemCount(): Int = queues.size
}
