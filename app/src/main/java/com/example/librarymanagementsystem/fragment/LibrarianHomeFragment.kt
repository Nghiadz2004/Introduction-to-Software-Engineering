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

private const val ALLBOOK_ID = "ALLBOOK"
private const val RDCARD_ID = "RDCARD"
private const val ADD_RDCARD_ID = "ADD_RDCARD"

class LibrarianHomeFragment : Fragment() {
    // Khởi tạo Recycler View
    private lateinit var recyclerView: RecyclerView

    // Khởi tạo biến lưu ID của fragment
    private var fragmentId: String = ALLBOOK_ID  // hoặc nullable tùy ý

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentId = arguments?.getSerializable("FRAGMENT_ID") as? String ?: ALLBOOK_ID
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_officer_home, container, false)

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById<RecyclerView>(R.id.librarianHomeRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Lấy userID của người dùng hiện tại
        val user = Firebase.auth.currentUser
        if (user == null) {
            Log.e("MyBookFragment", "User not logged in.")
            return view
        }

        // Khởi tạo loading dialog
        loadingDialog = LoadingDialog(requireContext())

        // Mặc định hiển thị danh sách sách
        if (fragmentId == ALLBOOK_ID) {
            loadAllBook()
        } else if (fragmentId == RDCARD_ID) {
            loadLibraryCards()
        } else if (fragmentId == ADD_RDCARD_ID) {
            loadAddReaderData()
        }

        return view
    }

    fun getFragmentId(): String {
        return fragmentId
    }

    // Hàm load dữ liệu cho danh sách AllBooks
    private fun loadAllBook() {
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            try {
                val allBooks: List<Book> = bookRepository.getBooks()

                // Khởi tạo adapter, xử lý click vào sách
                bookAdapter = BookAdapter(
                    allBooks,
                    // Xử lý click vào sách
                    onItemClick = { item ->
                        val intent = Intent(requireContext(), ActivityDetailBook::class.java).apply {
                            putExtra("book", item)
                        }
                        startActivity(intent)
                    },
                    // Xử lý nút xoá sách
                    onRemove = { item ->
                        lifecycleScope.launch {
                            try {
                                // Xoá item book ra khỏi adapter
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

    // Hàm load dữ liệu cho danh sách LibraryCards
    private fun loadLibraryCards() {
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            try {
                // Lấy dữ liệu LibraryCards của bạn
                val libraryCards: List<LibraryCard> = libraryCardRepository.getLibraryCards()

                // Khởi tạo adapter
                libraryCardAdapter = LibraryCardAdapter(
                    libraryCards,
                    onDeactivate = { item ->
                        lifecycleScope.launch {
                            try {
                                val newStatus = if (item.status == "ACTIVE") "INACTIVE" else "ACTIVE"

                                LibraryCardRepository().updateLibraryCard(item.requestId, newStatus)
                                item.status = newStatus

                                val index = libraryCards.indexOfFirst { it.requestId == item.requestId }
                                if (index != -1) {
                                    libraryCardAdapter.notifyItemChanged(index)
                                }

                            } catch (e: Exception) {
                                Log.e("REMOVE_REPORT_LOST", "Error: ${e.message}")
                            }
                        }
                    }
                )


                // Hiển thị danh sách LibraryCards
                recyclerView.adapter = libraryCardAdapter
                currentAdapter = libraryCardAdapter
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
                Log.d("CARD_REQUESTS","${addReaderData.size}")
                // Khởi tạo adapter và xử lý nút
                requestReaderCardAdapter = CardRequestAdapter(
                    addReaderData,
                    onApprove = { item -> requestReaderCardAdapter.approveItem(item)},
                    onReject = { item -> requestReaderCardAdapter.rejectItem(item)}
                    )

                // Hiển thị danh sách Request Add Reader Card
                recyclerView.adapter = requestReaderCardAdapter
                currentAdapter = requestReaderCardAdapter
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("LoadAddReaderData", e.toString())
            } finally {
                loadingDialog.dismiss()
            }
        }
    }


}