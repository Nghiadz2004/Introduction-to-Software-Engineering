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

class MyBookAdapter(
    private val bookList: List<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<MyBookAdapter.MyBookViewHolder>() {

    inner class MyBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImg: ImageView = itemView.findViewById(R.id.bookImg)
        val bookTitleTV: TextView = itemView.findViewById(R.id.bookTitleTV)
        val bookAuthorTV: TextView = itemView.findViewById(R.id.bookAuthorTV)
        val bookCategoryTV: TextView = itemView.findViewById(R.id.bookCategoryTV)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val ratingBarScoreTV: TextView = itemView.findViewById(R.id.ratingBarScoreTV)
        val bookDueDateLeftTV: TextView = itemView.findViewById(R.id.bookDueDateLeftTV)
        val favBtn: ImageButton = itemView.findViewById(R.id.favBtn)
        val returnBtn: Button = itemView.findViewById(R.id.returnBtn)
        val lostBtn: Button = itemView.findViewById(R.id.lostBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)
        return MyBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyBookViewHolder, position: Int) {
        val book = bookList[position]
//        holder.bookImg.src = book.url
        holder.bookTitleTV.text = book.title
        holder.bookAuthorTV.text = book.author
        holder.bookCategoryTV.text = book.category.value
//        holder.ratingBar.rating = book.rating
//        holder.ratingBarScoreTV.text = book.rating
//        holder.bookDueDateLeftTV.text = book.duedate
        var color = if (book.isFavorite) R.color.red else R.color.white
        holder.favBtn.setColorFilter(ContextCompat.getColor(holder.itemView.context, color))

        holder.favBtn.setOnClickListener {
            book.isFavorite = !book.isFavorite
            color = if (book.isFavorite) R.color.red else R.color.white
            holder.favBtn.setColorFilter(ContextCompat.getColor(holder.itemView.context, color))
        }

        holder.returnBtn.setOnClickListener {

        }

        holder.lostBtn.setOnClickListener {

        }

        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
    }

    override fun getItemCount(): Int = bookList.size
}