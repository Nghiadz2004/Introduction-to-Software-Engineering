package com.example.librarymanagementsystem.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
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
import com.example.librarymanagementsystem.cache.FavoriteCache
import com.example.librarymanagementsystem.model.BookDisplayItem
import com.example.librarymanagementsystem.repository.FavoriteRepository
import com.example.librarymanagementsystem.service.UIService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

// My book section id
private const val BORROWED_ID = "BORROWED"
private const val PENDING_ID = "PENDING"
private const val LOST_ID = "LOST"

class MyBookAdapter(
    items: List<BookDisplayItem>, // giữ nguyên đây là List
    private val myBookID: String,
    private val onItemClick: (BookDisplayItem) -> Unit,
    private val onReportLost: (BookDisplayItem) -> Unit,
    private val onCancelPending: (BookDisplayItem) -> Unit,
    private val onCancelLost: (BookDisplayItem) -> Unit
) : RecyclerView.Adapter<MyBookAdapter.MyBookViewHolder>() {

    private val items: MutableList<BookDisplayItem> = items.toMutableList()

    inner class MyBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookImg: ImageView = itemView.findViewById(R.id.bookImg)
        val bookTitleTV: TextView = itemView.findViewById(R.id.bookTitleTV)
        val bookAuthorTV: TextView = itemView.findViewById(R.id.bookAuthorTV)
        val bookCategoryTV: TextView = itemView.findViewById(R.id.bookCategoryTV)
        val dueDateLeftTV: TextView = itemView.findViewById(R.id.duedateleftTV)
        val bookDueDateLeftTV: TextView = itemView.findViewById(R.id.bookDueDateLeftTV)

        val favBtn: ImageButton = itemView.findViewById(R.id.favBtn)
        val lostBtn: Button = itemView.findViewById(R.id.lostBtn)

        val userID = Firebase.auth.currentUser!!.uid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return MyBookViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyBookViewHolder, position: Int) {
        val item = items[position]
        val book = item.book

        Glide.with(holder.itemView.context)
            .load(book.cover)
            .into(holder.bookImg)

        holder.bookTitleTV.text = book.title
        holder.bookAuthorTV.text = book.author ?: "Unknown"
        holder.bookCategoryTV.text = book.category

        if (FavoriteCache.favoriteBookIds.contains(item.book.id)) {
            UIService.setButtonIcon(
                holder.itemView.context,
                holder.favBtn,
                R.drawable.baseline_favorite_24
            )
            UIService.setButtonColor(
                holder.itemView.context,
                holder.favBtn,
                selectedColorResId = R.color.red
            )
        }

        holder.favBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (FavoriteCache.favoriteBookIds.contains(item.book.id)) {
                    UIService.setButtonIcon(
                        holder.itemView.context,
                        holder.favBtn,
                        R.drawable.favorite_icon
                    )
                    UIService.setButtonColor(
                        holder.itemView.context,
                        holder.favBtn,
                        selectedColorResId = R.color.white
                    )

                    // Remove from cache
                    FavoriteCache.favoriteBookIds.remove(item.book.id)
                }
                else {
                    UIService.setButtonIcon(
                        holder.itemView.context,
                        holder.favBtn,
                        R.drawable.baseline_favorite_24
                    )
                    UIService.setButtonColor(
                        holder.itemView.context,
                        holder.favBtn,
                        selectedColorResId = R.color.red
                    )

                    // Add to cache
                    FavoriteCache.favoriteBookIds.add(item.book.id!!)
                }

                // Update data base
                FavoriteRepository().updateFavorite(holder.userID, FavoriteCache.favoriteBookIds)
            }
        }

        loadItem(holder, item)

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadItem(holder: MyBookViewHolder, item: BookDisplayItem) {
        when (myBookID) {
            BORROWED_ID -> {
                holder.bookDueDateLeftTV.visibility = View.VISIBLE
                holder.lostBtn.text = "Report Lost"

                // Kiểm tra và chuyển expectedReturnDate thành Date nếu cần
                item.borrowBook?.expectedReturnDate?.let { timestamp ->
                    // Nếu expectedReturnDate là Timestamp, chuyển nó thành Date
                    val dueDate = timestamp.toDate() // chuyển đổi Timestamp thành Date
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dueDate)

                    holder.dueDateLeftTV.visibility = View.GONE
                    holder.bookDueDateLeftTV.text = "Due: $formattedDate"
                } ?: run {
                    holder.bookDueDateLeftTV.text = ""
                }

                holder.lostBtn.setOnClickListener {
                    onReportLost(item)
                }
            }


            PENDING_ID -> {
                holder.bookDueDateLeftTV.visibility = View.GONE
                holder.dueDateLeftTV.visibility = View.GONE
                holder.lostBtn.text = "Cancel"
                holder.lostBtn.setOnClickListener {
                    onCancelPending(item)
                }
            }

            LOST_ID -> {
                val context = holder.itemView.context
                val textColor = ContextCompat.getColor(context, R.color.orange)
                val buttonColor = ColorStateList.valueOf(textColor)

                holder.bookTitleTV.setTextColor(textColor)
                holder.lostBtn.text = "Cancel"
                holder.lostBtn.backgroundTintList = buttonColor
                holder.bookDueDateLeftTV.visibility = View.GONE
                holder.dueDateLeftTV.visibility = View.GONE

                holder.lostBtn.setOnClickListener {
                    onCancelLost(item)
                }
            }
        }
    }

    fun removeItem(item: BookDisplayItem) {
        val index = items.indexOf(item)
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun getItemCount(): Int = items.size
}