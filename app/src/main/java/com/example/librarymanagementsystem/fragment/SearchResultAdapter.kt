package com.example.librarymanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.model.Book

class SearchResultAdapter(
    private val books: List<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.BookViewHolder>() {

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTV: TextView = itemView.findViewById(R.id.bookTitleTV)
        private val authorTV: TextView = itemView.findViewById(R.id.bookAuthorTV)
        private val categoryTV: TextView = itemView.findViewById(R.id.bookCategoryTV)

        fun bind(book: Book) {
            titleTV.text = book.title ?: "No title"
            authorTV.text = book.author ?: "Unknown"
            categoryTV.text = book.category ?: "Unknown"

            itemView.setOnClickListener {
                onItemClick(book)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_grid, parent, false)

        // Chia đúng chiều rộng để hiển thị 3 item 1 hàng
        val layoutParams = view.layoutParams
        layoutParams.width = parent.measuredWidth / 3
        view.layoutParams = layoutParams

        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }
}
