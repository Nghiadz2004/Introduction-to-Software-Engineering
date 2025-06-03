package com.example.librarymanagementsystem.adapter

import android.media.Rating
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.model.Book

class BookAdapter(
    private val items: List<Book>,
    private val listener: OnBookActionListener? = null
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    interface OnBookActionListener {
        fun onEdit(book: Book)
        fun onRemove(book: Book)
    }

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCover: ImageView = view.findViewById(R.id.bookImage)
        val tvTitle: TextView = view.findViewById(R.id.bookTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvAuthor)
        val tvCategory: TextView = view.findViewById(R.id.bookCategory)
        val rbRating: RatingBar = view.findViewById(R.id.ratingBar)
        val tvRaiting: TextView = view.findViewById(R.id.ratingTV)
        val btnEdit: Button = view.findViewById(R.id.editBT)
        val btnRemove: Button = view.findViewById(R.id.removeBT)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = items[position]
        // holder.imgCover.setImage… (nếu có URL hoặc drawable)
        holder.tvTitle.text = book.title
        holder.tvAuthor.text = "Author: ${book.author}"
        holder.tvCategory.text = "Category: ${book.category}"
        holder.rbRating.rating = book.rating
        holder.tvRaiting.text = String.format("%.1f", book.rating)
        holder.btnEdit.setOnClickListener { listener?.onEdit(book) }
        holder.btnRemove.setOnClickListener { listener?.onRemove(book) }
    }

    override fun getItemCount(): Int = items.size
}