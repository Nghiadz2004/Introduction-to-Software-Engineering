package com.example.libmanagementsystem.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libmanagementsystem.R
import com.example.libmanagementsystem.adapter.BookAdapter
import com.example.libmanagementsystem.adapter.CardRequestAdapter
import com.example.libmanagementsystem.adapter.LibraryCardAdapter
import com.example.libmanagementsystem.dialog.LoadingDialog
import com.example.libmanagementsystem.repository.BookRepository
import com.example.libmanagementsystem.repository.CardRequestRepository
import com.example.libmanagementsystem.repository.LibraryCardRepository

private const val LOAN_ID = "LOAN"
private const val QUEUE_ID = "QUEUE"
private const val RETURN_ID = "RETURN"
private const val REPORT_ID = "REPORT"

class LibrarianTransactionFragment : Fragment() {
    // Khởi tạo Recycler View
    private lateinit var recyclerView: RecyclerView

    // Khởi tạo biến lưu ID của fragment
    private var fragmentId: String = LOAN_ID  // hoặc nullable tùy ý

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
        fragmentId = arguments?.getSerializable("FRAGMENT_ID") as? String ?: LOAN_ID
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_librarian_transaction, container, false)

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById<RecyclerView>(R.id.librarianTransactionRV)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Lấy userID của người dùng hiện tại
//        userID = Firebase.auth.currentUser!!.uid

        // Khởi tạo loading dialog
        loadingDialog = LoadingDialog(requireContext())

        // Mặc định hiển thị danh sách sách
//        if (fragmentId == LOAN_ID) {
//            loadLoanData()
//        }
//        else if (fragmentId == QUEUE_ID) {
//            loadQueueData()
//        }
//        else if (fragmentId == RETURN_ID) {
//            loadReturnBookData()
//        }
//        else if (fragmentId == REPORT_ID) {
//            loadReportLostData()
//        }

        return view
    }

    fun getFragmentId(): String {
        return fragmentId
    }

    // Hàm load dữ liệu cho danh sách AllBooks
//    private fun loadLoanData() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            loadingDialog.show()
//            try {
//                val allBooks: List<Book> = bookRepository.getBooks()
//                Log.d("AllBookFragment", "Số sách lấy được: ${allBooks.size}")
//                Log.d("AllBookFragment", "Danh sách sách: $allBooks")
//
//                // Khởi tạo adapter, xử lý click vào sách
//                bookAdapter = BookAdapter(allBooks, object : BookAdapter.OnBookActionListener {
//                    override fun onEdit(book: Book) {
//                        val intent = Intent(requireContext(), ActivityDetailBook::class.java)
//                        intent.putExtra("book", book)
//                        startActivity(intent)
//                    }
//
//                    override fun onRemove(book: Book) {
//                    }
//                })
//
//                recyclerView.adapter = bookAdapter
//                currentAdapter = bookAdapter
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("AllBookAdapter", e.toString())
//            } finally {
//                loadingDialog.dismiss()
//            }
//        }
//    }
//
//    // Hàm load dữ liệu cho danh sách LibraryCards
//    private fun loadQueueData() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            loadingDialog.show()
//            try {
//                // Lấy dữ liệu LibraryCards của bạn
//                val libraryCards: List<LibraryCard> = libraryCardRepository.getLibraryCards()
//                libraryCardAdapter = LibraryCardAdapter(libraryCards)
//
//                // Hiển thị danh sách LibraryCards
//                recyclerView.adapter = libraryCardAdapter
//                currentAdapter = libraryCardAdapter
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("LoadReaderCards", e.toString())
//            } finally {
//                loadingDialog.dismiss()
//            }
//        }
//    }
//
//    // Hàm load dữ liệu cho danh sách Request Add Reader Card
//    private fun loadReturnBookData() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            loadingDialog.show()
//            try {
//                // Giả sử bạn lấy dữ liệu từ repository hoặc nguồn khác
//                val addReaderData: List<CardRequest> = cardRequestRepository.getPendingRequests()/* lấy dữ liệu của bạn */
//                requestReaderCardAdapter = CardRequestAdapter(addReaderData)
//
//                // Hiển thị danh sách Request Add Reader Card
//                recyclerView.adapter = requestReaderCardAdapter
//                currentAdapter = requestReaderCardAdapter
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("LoadAddReaderData", e.toString())
//            } finally {
//                loadingDialog.dismiss()
//            }
//        }
//    }
//
//    private fun loadReportLostData() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            loadingDialog.show()
//            try {
//                // Giả sử bạn lấy dữ liệu từ repository hoặc nguồn khác
//                val addReaderData: List<CardRequest> = cardRequestRepository.getPendingRequests()/* lấy dữ liệu của bạn */
//                requestReaderCardAdapter = CardRequestAdapter(addReaderData)
//
//                // Hiển thị danh sách Request Add Reader Card
//                recyclerView.adapter = requestReaderCardAdapter
//                currentAdapter = requestReaderCardAdapter
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.e("LoadAddReaderData", e.toString())
//            } finally {
//                loadingDialog.dismiss()
//            }
//        }
//    }

}