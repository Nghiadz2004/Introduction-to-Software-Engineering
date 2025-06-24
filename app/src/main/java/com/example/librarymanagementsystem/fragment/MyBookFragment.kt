package com.example.librarymanagementsystem.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.activity.ActivityDetailBook
import com.example.librarymanagementsystem.adapter.MyBookAdapter
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.model.BookDisplayItem
import com.example.librarymanagementsystem.service.MyBookManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

// My book section id
private const val BORROWED_ID = "BORROWED"
private const val PENDING_ID = "PENDING"
private const val LOST_ID = "LOST"

class MyBookFragment : Fragment() {
    private lateinit var recycleView: RecyclerView
    private lateinit var loadingDialog: LoadingDialog
    private val myBookManager = MyBookManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_book, container, false)

        recycleView = view.findViewById(R.id.myBookRV)
        recycleView.layoutManager = LinearLayoutManager(view.context)

        loadingDialog = LoadingDialog(requireContext())

        val myBookID = arguments?.getString("MYBOOK_ID") ?: BORROWED_ID

        val user = Firebase.auth.currentUser
        if (user == null) {
            Log.e("MyBookFragment", "User not logged in.")
            return view
        }

        loadMyBook(user.uid, myBookID)
        return view
    }

    companion object {
        fun newInstance(myBookID: String): MyBookFragment {
            val fragment = MyBookFragment()
            val args = Bundle().apply {
                putString("MYBOOK_ID", myBookID)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private fun loadMyBook(userID: String, myBookID: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            try {
                val bookItems: List<BookDisplayItem> = when (myBookID) {
                    BORROWED_ID -> {
                        val bookMap = myBookManager.getReaderBorrowingBooks(userID)
                        bookMap.map { (book, borrow) ->
                            BookDisplayItem(book, borrow)
                        }
                    }

                    PENDING_ID -> {
                        val books = myBookManager.getReaderPendingBooks(userID)
                        books.map { book -> BookDisplayItem(book, null) }
                    }

                    LOST_ID -> {
                        val books = myBookManager.getReaderPendingLosts(userID)
                        books.map { book -> BookDisplayItem(book, null) }
                    }

                    else -> emptyList()
                }

                recycleView.adapter = MyBookAdapter(
                    bookItems,
                    myBookID,
                    onItemClick = { item ->
                        val intent = Intent(requireContext(), ActivityDetailBook::class.java).apply {
                            putExtra("book", item.book)
                            putExtra("expectedReturnDate", item.borrowBook?.expectedReturnDate)
                        }
                        startActivity(intent)
                    },
                    onReportLost = { item ->
                        lifecycleScope.launch {
                            try {
                                val borrow = item.borrowBook!!
                                myBookManager.submitLostRequest(
                                    borrow.requestId!!,
                                    Firebase.auth.currentUser!!.uid,
                                    borrow.bookId!!,
                                    borrow.copyId!!
                                )

                                // Remove from recycle view
                                (recycleView.adapter as? MyBookAdapter)?.removeItem(item)

                            } catch (e: Exception) {
                                Log.e("REPORT_LOST", "Error: ${e.message}")
                            }
                        }
                    },
                    onCancelPending = { item ->
                        lifecycleScope.launch {
                            try {
                                myBookManager.cancelPendingRequest(
                                    item.book.id!!,
                                    userID
                                )

                                // Remove from recycle view
                                (recycleView.adapter as? MyBookAdapter)?.removeItem(item)

                            } catch (e: Exception) {
                                Log.e("REMOVE_REPORT_LOST", "Error: ${e.message}", e)
                            }
                        }
                    },
                    onCancelLost = { item ->
                        lifecycleScope.launch {
                            try {
                                Log.e("LOSTBOOK", item.book.id.toString())
                                Log.e("LOSTBOOK", userID)
                                myBookManager.cancelLostRequest(
                                    item.book.id!!,
                                    userID
                                )

                                // Remove from recycle view
                                (recycleView.adapter as? MyBookAdapter)?.removeItem(item)

                            } catch (e: Exception) {
                                Log.e("REMOVE_REPORT_LOST", "Error: ${e.message}", e)
                            }
                        }
                    }
                )


            } catch (e: Exception) {
                Log.e("MyBookFragment", "Error loading books: ${e.message}")
            } finally {
                loadingDialog.dismiss()
            }
        }
    }
}

