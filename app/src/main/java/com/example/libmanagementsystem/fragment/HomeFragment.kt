package com.example.libmanagementsystem.fragment

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
import com.example.libmanagementsystem.R
import com.example.libmanagementsystem.activity.ActivityDetailBook
import com.example.libmanagementsystem.adapter.BookHomeAdapter
import com.example.libmanagementsystem.model.Book
import com.example.libmanagementsystem.repository.BookRepository
import com.example.libmanagementsystem.repository.BorrowingRepository
import com.example.libmanagementsystem.service.BookStatistics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerFeatured: RecyclerView
    private lateinit var recyclerNewRelease: RecyclerView
    private lateinit var seeAllNewReleaseTV: View
    private lateinit var seeAllFeatureTV: View
    private val bookRepository = BookRepository()
    private val borrowingRepository = BorrowingRepository(FirebaseFirestore.getInstance())

    private var featuredBooks: List<Book> = emptyList()
    private var newReleaseBooks: List<Book> = emptyList()
    private var searchResultBooks: List<Book> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Nhận danh sách từ bundle (nếu được truyền vào từ HomeActivity)
        arguments?.let {
            searchResultBooks = it.getParcelableArrayList("BOOK_LIST") ?: emptyList()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        // Bước 1: Gán các view
        recyclerFeatured = view.findViewById(R.id.recyclerFeatured)
        recyclerNewRelease = view.findViewById(R.id.recyclerNewRelease)
        seeAllNewReleaseTV = view.findViewById(R.id.seeAllNewReleaseTV)
        seeAllFeatureTV = view.findViewById(R.id.seeAllFeatureTV)

        // Bước 2: Nếu có dữ liệu tìm kiếm thì hiển thị luôn
        arguments?.getParcelableArrayList<Book>(ARG_BOOK_LIST)?.let { bookList ->
            // Hiển thị danh sách tìm kiếm
            recyclerFeatured.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerFeatured.adapter = BookHomeAdapter(bookList) { book ->
                openBookDetail(book)
            }

            // Ẩn phần new release và feature để chỉ hiện kết quả tìm kiếm
            recyclerNewRelease.visibility = View.GONE
            seeAllFeatureTV.visibility = View.GONE
            seeAllNewReleaseTV.visibility = View.GONE

            return view
        }

        // Bước 3: Gán sự kiện click cho "See all"
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

        // Bước 4: Nếu không tìm kiếm thì tải dữ liệu từ Firestore như thường
        loadBooksFromFirestore()
        return view
    }

    private fun loadBooksFromFirestore() {
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
            }
        }
    }

    private fun openBookDetail(book: Book) {
        val intent = Intent(requireContext(), ActivityDetailBook::class.java).apply {
            putExtra("book", book)
        }
        startActivity(intent)
    }
    companion object {
        private const val ARG_BOOK_LIST = "book_list"

        fun newInstance(books: List<Book>): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_BOOK_LIST, ArrayList(books))
            fragment.arguments = args
            return fragment
        }
    }

}