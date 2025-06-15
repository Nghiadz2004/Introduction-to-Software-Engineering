package com.example.librarymanagementsystem.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.Log
import com.example.librarymanagementsystem.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.model.BookDisplayItem
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.text.SimpleDateFormat
import java.util.Locale

// My book section id
private const val BORROWED_ID = "BORROWED"
private const val PENDING_ID = "PENDING"
private const val LOST_ID = "LOST"

class MyBookAdapter(
    private val items: List<BookDisplayItem>,
    private val myBookID: String,
    private val onItemClick: (BookDisplayItem) -> Unit
) : RecyclerView.Adapter<MyBookAdapter.MyBookViewHolder>() {

    inner class MyBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImg: ImageView = itemView.findViewById(R.id.bookImg)
        val bookTitleTV: TextView = itemView.findViewById(R.id.bookTitleTV)
        val bookAuthorTV: TextView = itemView.findViewById(R.id.bookAuthorTV)
        val bookCategoryTV: TextView = itemView.findViewById(R.id.bookCategoryTV)
        val bookDueDateLeftTV: TextView = itemView.findViewById(R.id.bookDueDateLeftTV)
        val statusTV: TextView = itemView.findViewById(R.id.statusTV)
        val favBtn: ImageButton = itemView.findViewById(R.id.favBtn)
        val cancelBtn: Button = itemView.findViewById(R.id.cancelBtn)
        val lostBtn: Button = itemView.findViewById(R.id.lostBtn)

        val borrowBookRepository = BorrowingRepository()
        val userID = Firebase.auth.currentUser!!.uid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return MyBookViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyBookViewHolder, position: Int) {
        val item = items[position]
        Log.e("MYBOOKADAPTER", item.toString())
        val book = item.book

        Glide.with(holder.itemView.context)
            .load(book.cover)
            .into(holder.bookImg)

        holder.bookTitleTV.text = book.title
        holder.bookAuthorTV.text = book.author ?: "Unknown"
        holder.bookCategoryTV.text = book.category

        loadItem(holder, item)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    private fun loadItem(holder: MyBookViewHolder, item: BookDisplayItem) {
        if (myBookID == BORROWED_ID) {
            holder.bookDueDateLeftTV.visibility = View.VISIBLE
            holder.lostBtn.text = "Report Lost"
            item.expectedReturnDate.let {
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it!!)
                holder.bookDueDateLeftTV.text = "Due: $formattedDate"
            } ?: run {
                holder.bookDueDateLeftTV.text = ""
            }
            holder.lostBtn.setOnClickListener {
                // TODO: Inform lost book
            }
        }
        else if (myBookID == PENDING_ID) {
            holder.bookDueDateLeftTV.visibility = View.GONE
            holder.lostBtn.text = "Unborrowed"
            holder.lostBtn.setOnClickListener {
                // TODO: Remove from pending queue
            }
        }
        else if (myBookID == LOST_ID) {
            val text_color = ContextCompat.getColor(holder.itemView.context, R.color.orange)
            holder.bookTitleTV.setTextColor(text_color)
            holder.lostBtn.text = "Cancel"
            val button_color = ContextCompat.getColor(holder.itemView.context, R.color.orange)
            holder.lostBtn.backgroundTintList = ColorStateList.valueOf(button_color)
            holder.lostBtn.setOnClickListener {
                // TODO: Remove from lost queue
            }
        }
    }

    override fun getItemCount(): Int = items.size
}