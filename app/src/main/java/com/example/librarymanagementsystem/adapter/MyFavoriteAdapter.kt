package com.example.librarymanagementsystem.adapter

import com.example.librarymanagementsystem.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.model.Book

class MyFavoriteAdapter(
    private val bookList: List<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<MyFavoriteAdapter.MyFavoriteViewHolder>() {

    inner class MyFavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImg: ImageView = itemView.findViewById(R.id.bookImg)
        val bookTitleTV: TextView = itemView.findViewById(R.id.bookTitleTV)
        val bookAuthorTV: TextView = itemView.findViewById(R.id.bookAuthorTV)
        val bookCategoryTV: TextView = itemView.findViewById(R.id.bookCategoryTV)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val ratingBarScoreTV: TextView = itemView.findViewById(R.id.ratingBarScoreTV)
        val bookDueDateLeftTV: TextView = itemView.findViewById(R.id.bookDueDateLeftTV)
        val removeBtn: ImageButton = itemView.findViewById(R.id.removeBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_book, parent, false)
        return MyFavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyFavoriteViewHolder, position: Int) {
        val book = bookList[position]
//        holder.bookImg.src = book.url
        holder.bookTitleTV.text = book.title
        holder.bookAuthorTV.text = book.author
        holder.bookCategoryTV.text = book.category.value
//        holder.ratingBar.rating = book.rating
//        holder.ratingBarScoreTV.text = book.rating
//        holder.bookDueDateLeftTV.text = book.duedate

        holder.removeBtn.setOnClickListener {
            // TODO: Remove from database and inform to recycle view

            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
    }

    override fun getItemCount(): Int = bookList.size
}