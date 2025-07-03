package com.example.librarymanagementsystem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.model.CardRequest
import com.example.librarymanagementsystem.model.LibraryCard
import com.example.librarymanagementsystem.model.RequestStatus
import com.example.librarymanagementsystem.repository.CardRequestRepository
import com.example.librarymanagementsystem.repository.LibraryCardRepository
import com.example.librarymanagementsystem.service.LibraryCardManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class CardRequestAdapter(
    items: List<CardRequest>,
    private val onApprove: (CardRequest) -> Unit,
    private val onReject: (CardRequest) -> Unit
) : RecyclerView.Adapter<CardRequestAdapter.CardRequestViewHolder>() {

    // Chuyển thành MultableList để thực hiện các thao tác thêm/xoá/sửa
    private val items: MutableList<CardRequest> = items.toMutableList()
    val user = Firebase.auth.currentUser


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
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val req = items[position]
        holder.requestIdTV.text = (position + 1).toString()
        holder.fullNameTV.text = req.fullName
        holder.emailTV.text = req.email
        holder.birthdayTV.text =  "${outputFormat.format(req.birthday)}"
        holder.addressTV.text = if (req.address.isNullOrBlank()) "—" else req.address
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
        val uid = user?.uid
        val libraryCardManager = LibraryCardManager()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                libraryCardManager.approveCardRequestBatch(item, uid!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                removeItem(item)
            }
        }
    }

    fun rejectItem(item: CardRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val cardRequestRepository = CardRequestRepository()
                cardRequestRepository.updateRequestStatus(item.id!!, RequestStatus.REJECTED)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                removeItem(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}