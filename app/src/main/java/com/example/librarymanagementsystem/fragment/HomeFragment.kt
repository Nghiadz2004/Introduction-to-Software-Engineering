package com.example.librarymanagementsystem.fragment

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
import com.example.librarymanagementsystem.adapter.BookHomeAdapter
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerFeatured: RecyclerView
    private lateinit var recyclerNewRelease: RecyclerView
    private val bookRepository = BookRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerFeatured = view.findViewById(R.id.recyclerFeatured)
        recyclerNewRelease = view.findViewById(R.id.recyclerNewRelease)

        loadBooksFromFirestore()

        return view
    }

    private fun loadBooksFromFirestore() {
        lifecycleScope.launch {
            try {
                val allBooks = bookRepository.getBooks()
                Log.d("HomeFragment", "Firestore trả về ${allBooks.size} sách")
                val featuredBooks = allBooks.take(10)
                val newReleaseBooks = allBooks
                    .filter { it.publishYear != null }
                    .sortedByDescending { it.publishYear }
                    .take(10)

                recyclerFeatured.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                recyclerNewRelease.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                recyclerFeatured.adapter = BookHomeAdapter(featuredBooks)
                recyclerNewRelease.adapter = BookHomeAdapter(newReleaseBooks)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("HomeFragment", "Exception khi gọi getBooks()", e)
                Toast.makeText(requireContext(), "Lỗi khi tải sách: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}