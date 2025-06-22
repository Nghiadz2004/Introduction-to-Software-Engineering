package com.example.librarymanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.model.ReturnDisplay
import com.example.librarymanagementsystem.service.ReturnBookManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class ReturnBookAdapter(
    private val returnList: List<ReturnDisplay>,
    private val onReturnChanged: suspend () -> Unit
) :
    RecyclerView.Adapter<ReturnBookAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCover: ImageView = view.findViewById(R.id.imgCover)
        val rBook: TextView = view.findViewById(R.id.rBook)
        val rAuthor: TextView = view.findViewById(R.id.rAuthor)
        val rCopyId: TextView = view.findViewById(R.id.rCopyId)
        val rReader: TextView = view.findViewById(R.id.rReader)
        val rBorrowDate: TextView = view.findViewById(R.id.rBorrowDate)
        val rDueDate: TextView = view.findViewById(R.id.rDueDate)
        val rStatus: TextView = view.findViewById(R.id.rStatus)
        val rFine: TextView = view.findViewById(R.id.rFine)
        val btnReturn: Button = view.findViewById(R.id.btnMaskAsReturn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_return_book, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = returnList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = returnList[position]
        Glide.with(holder.itemView.context)
            .load(item.book.cover)
            .placeholder(R.drawable.harry_potter_cover) // fallback image
            .into(holder.imgCover)

        holder.rBook.text = item.book.title
        holder.rAuthor.text = "Author: ${item.book.author}"
        holder.rCopyId.text = "Copy ID: ${item.borrow.copyId}"
        holder.rReader.text = "Borrowed by: ${item.readerName}"
        holder.rBorrowDate.text = "Loan date: ${item.formattedBorrowDate}"
        holder.rDueDate.text = "Due date: ${item.formattedDueDate}"
        holder.rStatus.text = "Status: ${item.statusText}"
        holder.rFine.text = item.fineText

        holder.btnReturn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val borrow = item.borrow
                val today = Date()
                val expected = borrow.expectedReturnDate ?: today
                val daysLate = ((today.time - expected.time) / (1000 * 60 * 60 * 24)).toInt()
                val fine = if (daysLate > 0) daysLate * 1000 else null
                val reason = item.statusText

                ReturnBookManager().markAsReturned(borrow, fine, reason)
                onReturnChanged()  // Gọi để reload lại adapter
            }
        }
    }
}