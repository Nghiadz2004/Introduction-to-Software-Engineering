package com.example.librarymanagementsystem.adapter

import com.example.librarymanagementsystem.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.FavoriteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFavoriteAdapter(
    private val readerId: String,
    private val bookList: MutableList<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<MyFavoriteAdapter.MyFavoriteViewHolder>() {
    private val favoriteRepository = FavoriteRepository()

    inner class MyFavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImg: ImageView = itemView.findViewById(R.id.bookImg)
        val bookTitleTV: TextView = itemView.findViewById(R.id.bookTitleTV)
        val bookAuthorTV: TextView = itemView.findViewById(R.id.bookAuthorTV)
        val bookCategoryTV: TextView = itemView.findViewById(R.id.bookCategoryTV)
        val removeBtn: ImageButton = itemView.findViewById(R.id.removeBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_book, parent, false)
        return MyFavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyFavoriteViewHolder, position: Int) {
        val book = bookList[position]

        Glide.with(holder.itemView.context)
            .load(book.cover)
            .into(holder.bookImg)
        holder.bookTitleTV.text = book.title
        holder.bookAuthorTV.text = book.author
        holder.bookCategoryTV.text = book.category

        holder.removeBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                favoriteRepository.removeBookFromFavorite(readerId, book.id.toString())

                // Cập nhật RecyclerView
                bookList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, bookList.size)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
    }

    override fun getItemCount(): Int = bookList.size
}