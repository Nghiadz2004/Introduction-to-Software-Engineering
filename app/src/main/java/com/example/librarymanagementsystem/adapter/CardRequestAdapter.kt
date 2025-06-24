package com.example.librarymanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.model.CardRequest
import com.example.librarymanagementsystem.model.LibraryCard
import com.example.librarymanagementsystem.repository.LibraryCardRepository

class CardRequestAdapter(
    items: List<CardRequest>,
    private val onApprove: (CardRequest) -> Unit,
    private val onReject: (CardRequest) -> Unit
) : RecyclerView.Adapter<CardRequestAdapter.CardRequestViewHolder>() {

    // Chuyển thành MultableList để thực hiện các thao tác thêm/xoá/sửa
    private val items: MutableList<CardRequest> = items.toMutableList()

    inner class CardRequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val requestIdTV: TextView = view.findViewById(R.id.requestIdTV)
        val fullNameTV: TextView = view.findViewById(R.id.FullNameTV)
        val emailTV: TextView = view.findViewById(R.id.emailTV)
        val birthdayTV: TextView = view.findViewById(R.id.birthdayTV)
        val addressTV: TextView = view.findViewById(R.id.addressTV)
        val typeTV: TextView = view.findViewById(R.id.typeTV)
        val btnApprove: Button = view.findViewById(R.id.btnApprove)
        val btnReject: Button = view.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_reader_card, parent, false)
        return CardRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardRequestViewHolder, position: Int) {
        val req = items[position]
        holder.requestIdTV.text = req.id
        holder.fullNameTV.text = req.fullName
        holder.emailTV.text = req.email
        holder.birthdayTV.text = req.birthday.toString()
        holder.addressTV.text = req.address
        holder.typeTV.text = req.type
        holder.btnApprove.setOnClickListener {
            onApprove(req)
        }
        holder.btnReject.setOnClickListener {
            onReject(req)
        }
    }

    fun removeItem(item: CardRequest) {
        val index = items.indexOf(item)
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun approveItem(item: CardRequest) {
        // LibraryCardRepository().createLibraryCard()
        removeItem(item)
    }

    fun rejectItem(item: CardRequest) {

        removeItem(item)
    }

    override fun getItemCount(): Int = items.size
}