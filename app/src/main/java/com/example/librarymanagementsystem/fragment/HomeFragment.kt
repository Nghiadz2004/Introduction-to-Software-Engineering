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
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository
import com.example.librarymanagementsystem.service.BookStatistics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerFeatured: RecyclerView
    private lateinit var recyclerNewRelease: RecyclerView
    private lateinit var seeAllNewReleaseTV: View
    private lateinit var seeAllFeatureTV: View
    private lateinit var loadingDialog: LoadingDialog
    private val bookRepository = BookRepository()
    private val borrowingRepository = BorrowingRepository(FirebaseFirestore.getInstance())

    private var featuredBooks: List<Book> = emptyList()
    private var newReleaseBooks: List<Book> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerFeatured = view.findViewById(R.id.recyclerFeatured)
        recyclerNewRelease = view.findViewById(R.id.recyclerNewRelease)
        seeAllNewReleaseTV = view.findViewById(R.id.seeAllNewReleaseTV)
        seeAllFeatureTV = view.findViewById(R.id.seeAllFeatureTV)
        loadingDialog = LoadingDialog(requireContext())

        seeAllFeatureTV.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelableArrayList("featuredBooks", ArrayList(featuredBooks))
            }
            val fragment = FeaturedBooksFragment().apply {
                arguments = bundle
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        seeAllNewReleaseTV.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelableArrayList("newReleaseBooks", ArrayList(newReleaseBooks))
            }
            val fragment = NewReleaseFragment().apply {
                arguments = bundle
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        loadBooksFromFirestore()
        return view
    }

    private fun loadBooksFromFirestore() {
        loadingDialog.show()
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val allBooks = bookRepository.getBooks()
                val bookStatistics = BookStatistics(borrowingRepository, bookRepository)

                val booksWithBorrowCount = bookStatistics.getNumBorrowByBook()

                featuredBooks = booksWithBorrowCount
                    .sortedByDescending { it.second }
                    .map { it.first }
                    .take(60)

                newReleaseBooks = allBooks
                    .filter { it.publishYear != null }
                    .sortedByDescending { it.publishYear }
                    .take(60)

                recyclerFeatured.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                recyclerNewRelease.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                recyclerFeatured.adapter = BookHomeAdapter(featuredBooks.take(10)) { book ->
                    openBookDetail(book)
                }
                recyclerNewRelease.adapter = BookHomeAdapter(newReleaseBooks.take(10)){ book ->
                    openBookDetail(book)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("HomeFragment", "Exception khi gọi getBooks()", e)
                Toast.makeText(requireContext(), "Lỗi khi tải sách: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                loadingDialog.dismiss()
            }
        }
    }

    private fun openBookDetail(book: Book) {
        val intent = Intent(requireContext(), ActivityDetailBook::class.java).apply {
            putExtra("book", book)
        }
        startActivity(intent)
    }
}