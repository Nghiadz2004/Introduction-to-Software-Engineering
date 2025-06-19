package com.example.libmanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.libmanagementsystem.R
import com.example.libmanagementsystem.model.ReaderCard

class ReaderCardAdapter(
    private val items: List<ReaderCard>,
    private val listener: OnReaderCardActionListener? = null
) : RecyclerView.Adapter<ReaderCardAdapter.ReaderCardViewHolder>() {

    interface OnReaderCardActionListener {
        fun onEdit(card: ReaderCard)
        fun onRemove(card: ReaderCard)
    }

    inner class ReaderCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardIdTV: TextView    = view.findViewById(R.id.cardIdTV)
        val fullNameTV: TextView  = view.findViewById(R.id.FullNameTV)
        val emailTV: TextView     = view.findViewById(R.id.tvEmail)
        val birthdayTV: TextView  = view.findViewById(R.id.tvBirthday)
        val addressTV: TextView   = view.findViewById(R.id.tvAddress)
        val typeTV: TextView      = view.findViewById(R.id.tvType)
        val issuerTV: TextView    = view.findViewById(R.id.tvIssuer)
        val issueDateTV: TextView = view.findViewById(R.id.tvIssueDate)
        val dueDateTV: TextView   = view.findViewById(R.id.tvDueDate)
        val statusTV: TextView    = view.findViewById(R.id.tvStatus)
        val btnEdit: Button       = view.findViewById(R.id.btnEdit)
        val btnRemove: Button     = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReaderCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reader_card, parent, false)
        return ReaderCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReaderCardViewHolder, position: Int) {
        val card = items[position]
        holder.cardIdTV.text     = "Card ID: ${card.cardId}"
        holder.fullNameTV.text   = "Full name: ${card.fullName}"
        holder.emailTV.text      = "Email: ${card.email}"
        holder.birthdayTV.text   = "Birthday: ${card.birthday}"
        holder.addressTV.text    = "Address: ${card.address}"
        holder.typeTV.text       = "Type: ${card.type}"
        holder.issuerTV.text     = "Issuer: ${card.issuer}"
        holder.issueDateTV.text  = "Issue Date: ${card.issueDate}"
        holder.dueDateTV.text    = "Due Date: ${card.dueDate}"
        holder.statusTV.text     = "Status: ${card.status}"
        holder.btnEdit.setOnClickListener { listener?.onEdit(card) }
        holder.btnRemove.setOnClickListener { listener?.onRemove(card) }
    }

    override fun getItemCount(): Int = items.size
}
