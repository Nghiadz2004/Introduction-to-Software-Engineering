package com.example.librarymanagementsystem.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.activity.ActivityDetailBook
import com.example.librarymanagementsystem.adapter.BookAdapter
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import kotlinx.coroutines.launch

private const val ALLBOOK_ID = "ALLBOOK"
private const val ADDBOOK_ID = "ADDBOOK"

class StorekeeperHomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private var fragmentId: String = ALLBOOK_ID
    private var currentAdapter: RecyclerView.Adapter<*>? = null
    private lateinit var bookAdapter: BookAdapter
    private var bookRepository = BookRepository()
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentId = arguments?.getSerializable("FRAGMENT_ID") as? String ?: ALLBOOK_ID
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_officer_home, container, false)

        recyclerView = view.findViewById(R.id.librarianHomeRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadingDialog = LoadingDialog(requireContext())

        when (getFragmentId()) {
            ALLBOOK_ID -> loadAllBook()
            ADDBOOK_ID -> loadAddBook()
        }

        return view
    }

    fun getFragmentId(): String {
        return fragmentId
    }

    private fun loadAllBook() {
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            try {
                val allBooks: List<Book> = bookRepository.getBooks()

                bookAdapter = BookAdapter(
                    allBooks,
                    onItemClick = { item ->
                        val intent = Intent(requireContext(), ActivityDetailBook::class.java).apply {
                            putExtra("book", item)
                        }
                        startActivity(intent)
                    },
                    onRemove = { item ->
                        lifecycleScope.launch {
                            try {
                                bookAdapter.removeItem(item)
                            } catch (e: Exception) {
                                Log.e("REMOVE_REPORT_LOST", "Error: ${e.message}")
                            }
                        }
                    }
                )

                recyclerView.adapter = bookAdapter
                currentAdapter = bookAdapter
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("AllBookAdapter", e.toString())
            } finally {
                loadingDialog.dismiss()
            }
        }
    }

    private fun loadAddBook() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_add_book, AddBookFragment())
            .addToBackStack(null)
            .commit()
    }
}
