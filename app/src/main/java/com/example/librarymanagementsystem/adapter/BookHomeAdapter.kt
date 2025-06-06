package com.example.librarymanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.model.Book

class BookHomeAdapter(
    private val books: List<Book>
) : RecyclerView.Adapter<BookHomeAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgCover: ImageView = itemView.findViewById(R.id.imgCoverIV)
        val tvTitle: TextView = itemView.findViewById(R.id.titleTV)
        val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_bl1, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        // Load ảnh bìa bằng Glide nếu có cover, nếu không thì dùng ảnh mặc định
        val imageUrl = book.cover
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.harry_potter_cover)
                .into(holder.imgCover)
        } else {
            holder.imgCover.setImageResource(R.drawable.harry_potter_cover)
        }

        holder.tvTitle.text = book.title
        holder.tvAuthor.text = "by ${book.author ?: "Unknown"}"
    }

    override fun getItemCount(): Int = books.size
}