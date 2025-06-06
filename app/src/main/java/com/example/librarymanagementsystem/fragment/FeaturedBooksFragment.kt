package com.example.librarymanagementsystem.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.adapter.BookHomeAdapter
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

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
            rvFeaturedBooksAll.adapter = BookHomeAdapter(featuredBooks)
        } else {
            Toast.makeText(requireContext(), "Không nhận được danh sách featured books", Toast.LENGTH_SHORT).show()
        }
    }
}