package com.example.libmanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.libmanagementsystem.R
import com.example.libmanagementsystem.model.Book

class BookAdapter(
    items: List<Book>,
    private val onItemClick: (Book) -> Unit,
    private val onRemove: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    // Chuyển thành MultableList để thực hiện các thao tác thêm/xoá/sửa
    private val items: MutableList<Book> = items.toMutableList()

    inner class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCover: ImageView = view.findViewById(R.id.bookImage)
        val tvTitle: TextView = view.findViewById(R.id.bookTitle)
        val tvAuthor: TextView = view.findViewById(R.id.bookAuthor)
        val tvCategory: TextView = view.findViewById(R.id.bookCategory)
        val tvCopyLeft: TextView = view.findViewById(R.id.copyLeftTV)
        val btnRemove: Button = view.findViewById(R.id.removeBT)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = items[position]
        Glide.with(holder.itemView.context)
            .load(book.cover)
            .into(holder.imgCover)
        holder.tvTitle.text = book.title
        holder.tvAuthor.text = book.author
        holder.tvCategory.text = book.category
        holder.tvCopyLeft.text = book.quantity.toString()
        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
        holder.btnRemove.setOnClickListener {
            onRemove(book)
        }
    }

    fun removeItem(item: Book) {
        val index = items.indexOf(item)
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun getItemCount(): Int = items.size
}