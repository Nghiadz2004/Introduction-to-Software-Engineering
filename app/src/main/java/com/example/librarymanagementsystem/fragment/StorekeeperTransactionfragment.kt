package com.example.librarymanagementsystem.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.dialog.LoadingDialog

private const val LOAN_ID = "LOAN"

class StorekeeperTransactionFragment : Fragment() {
    // Khởi tạo Recycler View
    private lateinit var recyclerView: RecyclerView

    // Khởi tạo biến lưu ID của fragment
    private var fragmentId: String = LOAN_ID

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentId = arguments?.getSerializable("FRAGMENT_ID") as? String ?: LOAN_ID
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_officer_transaction, container, false)

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById<RecyclerView>(R.id.librarianTransactionRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadingDialog = LoadingDialog(requireContext())

        return view
    }
}