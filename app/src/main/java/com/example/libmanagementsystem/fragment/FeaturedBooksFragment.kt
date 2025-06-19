package com.example.libmanagementsystem.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libmanagementsystem.R
import com.example.libmanagementsystem.activity.ActivityDetailBook
import com.example.libmanagementsystem.adapter.BookHomeAdapter
import com.example.libmanagementsystem.model.Book

class FeaturedBooksFragment : Fragment() {

    private lateinit var rvFeaturedBooksAll: RecyclerView
    private lateinit var titleTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_release, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvFeaturedBooksAll = view.findViewById(R.id.rvNewReleaseAll)
        titleTextView = view.findViewById(R.id.newReleaseTitle)

        titleTextView.text = "Featured"
        rvFeaturedBooksAll.layoutManager = GridLayoutManager(requireContext(), 3)

        val featuredBooks = arguments?.getParcelableArrayList<Book>("featuredBooks")
        if (!featuredBooks.isNullOrEmpty()) {
            rvFeaturedBooksAll.adapter = BookHomeAdapter(featuredBooks) { book ->
                openBookDetail(book)
            }
        } else {
            Toast.makeText(requireContext(), "Không nhận được danh sách featured books", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openBookDetail(book: Book) {
        val intent = Intent(requireContext(), ActivityDetailBook::class.java).apply {
            putExtra("book", book)
        }
        startActivity(intent)
    }
}