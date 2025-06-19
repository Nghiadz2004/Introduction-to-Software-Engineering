package com.example.librarymanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.model.LibraryCard

class LibraryCardAdapter(
    items: List<LibraryCard>,
    private val onRemove: (LibraryCard) -> Unit,
) : RecyclerView.Adapter<LibraryCardAdapter.LibraryCardViewHolder>() {

    // Chuyển thành MultableList để thực hiện các thao tác thêm/xoá/sửa
    private val items: MutableList<LibraryCard> = items.toMutableList()

    inner class LibraryCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reader_card, parent, false)
        return LibraryCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryCardViewHolder, position: Int) {
        val card = items[position]
        holder.cardIdTV.text     = "Card ID: ${card.id}"
        holder.fullNameTV.text   = "Full name: ${card.fullName}"
        holder.emailTV.text      = "Email: ${card.email}"
        holder.birthdayTV.text   = "Birthday: ${card.birthday}"
        holder.addressTV.text    = "Address: ${card.address}"
        holder.typeTV.text       = "Type: ${card.type}"
        holder.issuerTV.text     = "Issuer: ${card.librarianId}"
        holder.issueDateTV.text  = "Issue Date: ${card.createdAt}"
        holder.dueDateTV.text    = "Due Date: ${card.getDueDate}"
        holder.statusTV.text     = "Status: ${card.status}"
        holder.btnRemove.setOnClickListener {
            onRemove(card)
        }
    }

    fun removeItem(item: LibraryCard) {
        val index = items.indexOf(item)
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun getItemCount(): Int = items.size
}
