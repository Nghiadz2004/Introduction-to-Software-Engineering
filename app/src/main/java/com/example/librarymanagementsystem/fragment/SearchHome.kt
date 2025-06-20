package com.example.librarymanagementsystem.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.activity.ActivityDetailBook
import com.example.librarymanagementsystem.adapter.BookHomeAdapter
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.service.BookStatistics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.GridLayoutManager

class SearchHome : Fragment() {

    private var searchResultBooks: List<Book> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchResultBooks = it.getParcelableArrayList(ARG_BOOK_LIST) ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerSearchResults)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = BookHomeAdapter(searchResultBooks) { book ->
            val intent = Intent(requireContext(), ActivityDetailBook::class.java)
            intent.putExtra("book", book)
            startActivity(intent)
        }

        return view
    }

    companion object {
        private const val ARG_BOOK_LIST = "BOOK_LIST"

        fun newInstance(books: List<Book>): SearchHome {
            val fragment = SearchHome()
            val args = Bundle()
            args.putParcelableArrayList(ARG_BOOK_LIST, ArrayList(books))
            fragment.arguments = args
            return fragment
        }
    }

}
