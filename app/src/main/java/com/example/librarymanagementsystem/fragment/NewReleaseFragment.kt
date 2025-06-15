package com.example.librarymanagementsystem.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.activity.ActivityDetailBook
import com.example.librarymanagementsystem.adapter.BookHomeAdapter
import com.example.librarymanagementsystem.model.Book


class NewReleaseFragment : Fragment() {

    private lateinit var rvNewReleaseAll: RecyclerView
    private lateinit var titleTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_release, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvNewReleaseAll = view.findViewById(R.id.rvNewReleaseAll)
        titleTextView = view.findViewById(R.id.newReleaseTitle)

        titleTextView.text = "New Releases"
        rvNewReleaseAll.layoutManager = GridLayoutManager(requireContext(), 3)

        val newReleaseBooks = arguments?.getParcelableArrayList<Book>("newReleaseBooks")
        if (!newReleaseBooks.isNullOrEmpty()) {
            rvNewReleaseAll.adapter = BookHomeAdapter(newReleaseBooks) { book ->
                openBookDetail(book)
            }
        } else {
            Toast.makeText(requireContext(), "Không nhận được danh sách sách mới", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openBookDetail(book: Book) {
        val intent = Intent(requireContext(), ActivityDetailBook::class.java).apply {
            putExtra("book", book)
        }
        startActivity(intent)
    }
}