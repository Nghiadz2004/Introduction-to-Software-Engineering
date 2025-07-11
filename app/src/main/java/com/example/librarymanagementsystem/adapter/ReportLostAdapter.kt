package com.example.librarymanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.model.LostDisplay
import com.example.librarymanagementsystem.service.ConfirmLostManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReportLostAdapter(
    private val lostList: List<LostDisplay>,
    private val librarianId: String,
    private val onLostChanged: suspend () -> Unit,
    private val isLibrarian: Boolean = true
) : RecyclerView.Adapter<ReportLostAdapter.LostViewHolder>() {

    inner class LostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCover: ImageView = view.findViewById(R.id.imgCover)
        val rBook: TextView = view.findViewById(R.id.rBook)
        val rAuthor: TextView = view.findViewById(R.id.rAuthor)
        val rCopyId: TextView = view.findViewById(R.id.rCopyId)
        val rReader: TextView = view.findViewById(R.id.rReader)
        val rLoanDate: TextView = view.findViewById(R.id.rLoanDate)
        val rDueDate: TextView = view.findViewById(R.id.rDueDate)
        val rFine: TextView = view.findViewById(R.id.rFine)
        val btnConfirm: Button = view.findViewById(R.id.btnConfirm)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_lost, parent, false)
        return LostViewHolder(view)
    }

    override fun getItemCount(): Int = lostList.size

    override fun onBindViewHolder(holder: LostViewHolder, position: Int) {
        val item = lostList[position]
        holder.rBook.text = item.book.title
        holder.rAuthor.text = "Author: ${item.book.author ?: "..."}"
        holder.rCopyId.text = "Copy ID: ${item.copyId}"
        holder.rReader.text = "Borrowed by: ${item.readerName}"
        holder.rLoanDate.text = "Loan date: ${item.formattedLoanDate}"
        holder.rDueDate.text = "Due date: ${item.formattedDueDate}"
        holder.rFine.text = "Fine: ${item.fine}"

        Glide.with(holder.itemView.context)
            .load(item.book.cover)
            .placeholder(R.drawable.harry_potter_cover)
            .into(holder.imgCover)

        holder.btnConfirm.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val context = holder.itemView.context // Lấy context đúng
                if (!isLibrarian) {
                    Toast.makeText(context, "You are not a librarian", Toast.LENGTH_SHORT).show()
                    return@launch
                }
                val loadingDialog = LoadingDialog(context)
                loadingDialog.show()
                ConfirmLostManager().confirmLostBook(
                    requestId = item.requestId,
                    readerId = item.readerId,
                    fineAmount = item.fineAmount,
                    copyId = item.copyId,
                    bookId = item.bookId,
                    librarianId = librarianId
                )
                onLostChanged()
                loadingDialog.dismiss()
                Toast.makeText(context, "Lost confirmed successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }
}