package com.example.librarymanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.service.LoanDisplay

class LoanAdapter(private val loans: List<LoanDisplay>) : RecyclerView.Adapter<LoanAdapter.LoanViewHolder>() {

    inner class LoanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val loanIndexTV: TextView = itemView.findViewById(R.id.loanIndexTV)
        val imgCover: ImageView = itemView.findViewById(R.id.imgCover)
        val lBook: TextView = itemView.findViewById(R.id.lBook)
        val lAuthor: TextView = itemView.findViewById(R.id.lAuthor)
        val lCopyId: TextView = itemView.findViewById(R.id.lCopyId)
        val lReader: TextView = itemView.findViewById(R.id.lReader)
        val lRecorder: TextView = itemView.findViewById(R.id.lRecorder)
        val lLoanDate: TextView = itemView.findViewById(R.id.lLoanDate)
        val lDueDate: TextView = itemView.findViewById(R.id.lDueDate)
        val lStatus: TextView = itemView.findViewById(R.id.lStatus)
        val dividerView: View = itemView.findViewById(R.id.dividerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan, parent, false)
        return LoanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoanViewHolder, position: Int) {
        val loan = loans[position]

        holder.loanIndexTV.text = "Loan %02d".format(position + 1)
        holder.lBook.text = loan.book.title
        holder.lAuthor.text = "Author: ${loan.book.author ?: "Unknown"}"
        holder.lCopyId.text = "Copy: ${loan.borrow.copyId}"
        holder.lReader.text = "Borrowed by: ${loan.readerName}"
        holder.lRecorder.text = "Processed by: ${loan.librarianName}"
        holder.lLoanDate.text = "Loan date: ${loan.formattedLoanDate}"
        holder.lDueDate.text = "Due date: ${loan.formattedDueDate}"
        holder.lStatus.text = "Status: ${loan.statusText}"

        Glide.with(holder.itemView.context)
            .load(loan.book.cover)
            .placeholder(R.drawable.harry_potter_cover)
            .into(holder.imgCover)

        if (position < loans.size - 1) {
            holder.dividerView.visibility = View.VISIBLE
        } else {
            holder.dividerView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = loans.size
}