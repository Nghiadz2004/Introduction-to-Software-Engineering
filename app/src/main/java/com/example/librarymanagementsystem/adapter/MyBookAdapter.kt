package com.example.librarymanagementsystem.adapter

import android.annotation.SuppressLint
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
import com.example.librarymanagementsystem.model.Book

class MyBookAdapter(
    private val bookList: MutableList<Book>,
    private val onItemClick: (Book) -> Unit
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return MyBookViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyBookViewHolder, position: Int) {
        val book = bookList[position]

        Glide.with(holder.itemView.context)
            .load(book.cover)
            .into(holder.bookImg)

        holder.bookTitleTV.text = book.title
        holder.bookAuthorTV.text = book.author
        holder.bookCategoryTV.text = book.category

//        // Hiển thị trạng thái
//        val isPending = borrow.confirmDate == borrow.borrowDate // chưa xác nhận
//        holder.statusTV.text = if (isPending) "Request Pending" else "Borrowed"
//
//        // Hiển thị ngày trả
//        holder.bookDueDateLeftTV.text =
//            "Return by: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(borrow.expectedReturnDate)}"
//
//        // Nút huỷ & mất sách
//        holder.cancelBtn.visibility = if (isPending) View.VISIBLE else View.GONE
//        holder.lostBtn.visibility = if (!isPending) View.VISIBLE else View.GONE
//
//        // Trạng thái yêu thích
//        val color = if (book.isFavorite) R.color.red else R.color.white
//        holder.favBtn.setColorFilter(ContextCompat.getColor(holder.itemView.context, color))
//
//        holder.favBtn.setOnClickListener {
//            book.isFavorite = !book.isFavorite
//            val newColor = if (book.isFavorite) R.color.red else R.color.white
//            holder.favBtn.setColorFilter(ContextCompat.getColor(holder.itemView.context, newColor))
//
//            // TODO: Cập nhật favorite trong Firestore nếu cần
//        }
//
//        holder.cancelBtn.setOnClickListener {
//            // TODO: Xoá bản ghi BorrowBook trong Firestore theo borrow.requestId
//
//            bookList.removeAt(position)
//            borrowBookList.removeAt(position)
//            notifyItemRemoved(position)
//            notifyItemRangeChanged(position, bookList.size)
//        }

        holder.lostBtn.setOnClickListener {
            // Giả lập gửi báo mất
            if (holder.lostBtn.text == "Report Lost") {
                holder.bookTitleTV.text = "${book.title} [LOST]"
                holder.bookTitleTV.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
                holder.lostBtn.text = "Cancel"
                // TODO: Gửi báo mất tới thủ thư
            } else {
                holder.bookTitleTV.text = book.title
                holder.bookTitleTV.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                holder.lostBtn.text = "Report Lost"
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
    }

    override fun getItemCount(): Int = bookList.size
}