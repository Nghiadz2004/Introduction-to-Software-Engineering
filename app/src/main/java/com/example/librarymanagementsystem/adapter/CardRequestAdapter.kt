package com.example.librarymanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.model.CardRequest

class CardRequestAdapter(
    private val items: List<CardRequest>,
    private val listener: OnRequestActionListener? = null
) : RecyclerView.Adapter<CardRequestAdapter.CardRequestViewHolder>() {

    interface OnRequestActionListener {
        fun onApprove(request: CardRequest)
        fun onReject(request: CardRequest)
    }

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
        holder.btnApprove.setOnClickListener { listener?.onApprove(req) }
        holder.btnReject.setOnClickListener { listener?.onReject(req) }
    }

    override fun getItemCount(): Int = items.size
}