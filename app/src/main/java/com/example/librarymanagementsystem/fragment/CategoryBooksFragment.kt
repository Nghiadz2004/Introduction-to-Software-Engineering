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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.activity.ActivityDetailBook
import com.example.librarymanagementsystem.adapter.BookHomeAdapter
import com.example.librarymanagementsystem.repository.BookRepository
import kotlinx.coroutines.launch

class CategoryBooksFragment : Fragment() {

    private lateinit var rvBooks: RecyclerView
    private lateinit var titleTextView: TextView
    private val bookRepository = BookRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_new_release, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvBooks = view.findViewById(R.id.rvNewReleaseAll)
        titleTextView = view.findViewById(R.id.newReleaseTitle)

        val category = arguments?.getString("category") ?: return
        titleTextView.text = category

        rvBooks.layoutManager = GridLayoutManager(requireContext(), 3)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val books = bookRepository.getBookByCategory(category)
                rvBooks.adapter = BookHomeAdapter(books) { book ->
                    val intent = Intent(requireContext(), ActivityDetailBook::class.java)
                    intent.putExtra("book", book)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Lỗi khi tải sách: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("CategoryBooksFragment", "Error loading books", e)
            }
        }
    }
}