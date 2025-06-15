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
import com.example.librarymanagementsystem.adapter.AddReaderCardAdapter
import com.example.librarymanagementsystem.adapter.BookAdapter
import com.example.librarymanagementsystem.adapter.CardRequestAdapter
import com.example.librarymanagementsystem.adapter.LibraryCardAdapter
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.model.CardRequest
import com.example.librarymanagementsystem.model.LibraryCard
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.CardRequestRepository
import com.example.librarymanagementsystem.repository.LibraryCardRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class AllBookFragment : Fragment() {
    // Khởi tạo Recycler View
    private lateinit var recyclerView: RecyclerView

    // Khởi tạo biến lưu adapter hiện tại
    private var currentAdapter: RecyclerView.Adapter<*>? = null

    // Khởi tạo các adapter hỗ trợ hiển thị các danh sách
    private lateinit var requestReaderCardAdapter: CardRequestAdapter
    private lateinit var bookAdapter: BookAdapter
    private lateinit var libraryCardAdapter: LibraryCardAdapter

    // Khởi tạo các biến repos để lấy dữ liệu
    private var bookRepository = BookRepository()
    private var libraryCardRepository = LibraryCardRepository()
    private var cardRequestRepository = CardRequestRepository()

    private lateinit var userID: String

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_book, container, false)

        // Lấy userID của người dùng hiện tại
        userID = Firebase.auth.currentUser!!.uid

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.allBookRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Mặc định hiển thị danh sách sách
        setAdapter(bookAdapter)

        // Khởi tạo loading dialog
        loadingDialog = LoadingDialog(requireContext())

        // Load dữ liệu sách
        loadAllBook()

        return view
    }

    fun showAddReader() {
        setAdapter(requestReaderCardAdapter)
    }

    fun showBooks() {
        setAdapter(bookAdapter)
    }

    fun showLibraryCards() {
        setAdapter(libraryCardAdapter)
    }

    private fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
        currentAdapter = adapter
    }

    private fun loadAllBook() {
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            try {
                val allBooks: List<Book> = bookRepository.getBooks()

                // Khởi tạo adapter, xử lý click vào sách
                bookAdapter = BookAdapter(allBooks, object : BookAdapter.OnBookActionListener {
                    override fun onEdit(book: Book) {
                        val intent = Intent(requireContext(), ActivityDetailBook::class.java)
                        intent.putExtra("book", book)
                        startActivity(intent)
                    }

                    override fun onRemove(book: Book) {
                    }
                })

                setAdapter(bookAdapter)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("AllBookAdapter", e.toString())
            } finally {
                loadingDialog.dismiss()
            }
        }
    }

    // Hàm load dữ liệu cho danh sách LibraryCards
    private fun loadLibraryCards() {
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            try {
                // Lấy dữ liệu LibraryCards của bạn
                val libraryCards: List<LibraryCard> = libraryCardRepository.getLibraryCards()
                    libraryCardAdapter = LibraryCardAdapter(libraryCards)

                // Cập nhật RecyclerView
                setAdapter(libraryCardAdapter)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("LoadReaderCards", e.toString())
            } finally {
                loadingDialog.dismiss()
            }
        }
    }

    // Hàm load dữ liệu cho danh sách Request Add Reader Card
    private fun loadAddReaderData() {
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            try {
                // Giả sử bạn lấy dữ liệu từ repository hoặc nguồn khác
                val addReaderData: List<CardRequest> = cardRequestRepository.getPendingRequests()/* lấy dữ liệu của bạn */
                    requestReaderCardAdapter = CardRequestAdapter(addReaderData)

                // Cập nhật RecyclerView
                setAdapter(requestReaderCardAdapter)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("LoadAddReaderData", e.toString())
            } finally {
                loadingDialog.dismiss()
            }
        }
    }


}