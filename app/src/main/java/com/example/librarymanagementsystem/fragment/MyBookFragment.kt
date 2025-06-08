package com.example.librarymanagementsystem.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.activity.ActivityDetailBook
import com.example.librarymanagementsystem.adapter.MyBookAdapter
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.model.Book
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
    private lateinit var myBookAdapter: MyBookAdapter
    private lateinit var bookList: MutableList<Book>
    private lateinit var loadingDialog: LoadingDialog

    private val userID = Firebase.auth.currentUser!!.uid

    private val myBookManager = MyBookManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_book, container, false)

        loadingDialog = LoadingDialog(requireContext())
        recycleView = view.findViewById(R.id.myBookRV)

        val myBookID = arguments?.getString("MYBOOK_ID") ?: BORROWED_ID

        loadMyBook(myBookID)

        return view
    }

    companion object {
        fun newInstance(myBookID: String): MyBookFragment {
            val fragment = MyBookFragment()
            val args = Bundle()
            args.putString("MYBOOK_ID", myBookID)
            fragment.arguments = args
            return fragment
        }
    }

    private fun loadMyBook(myBookID: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            try {
                if (myBookID == BORROWED_ID) {
                    val bookMap = myBookManager.getReaderBorrowingBooks(userID)

                    myBookAdapter = MyBookAdapter(
                        bookMap.keys.toList(),
                        bookMap,
                        myBookID
                    ) { book ->
                        val intent = Intent(requireContext(), ActivityDetailBook::class.java)
                        intent.putExtra("book", book)
                        startActivity(intent)
                    }

                    recycleView.adapter = myBookAdapter
                } else {
                    // Các section khác
                    bookList = when (myBookID) {
                        PENDING_ID -> myBookManager.getReaderPendingBooks(userID).toMutableList()
                        LOST_ID -> myBookManager.getReaderPendingLosts(userID).toMutableList()
                        else -> mutableListOf()
                    }

                    myBookAdapter = MyBookAdapter(
                        bookList,
                        emptyMap(), // Không cần borrow info
                        myBookID
                    ) { book ->
                        val intent = Intent(requireContext(), ActivityDetailBook::class.java)
                        intent.putExtra("book", book)
                        startActivity(intent)
                    }

                    recycleView.adapter = myBookAdapter
                }
            } catch (e: Exception) {
                Log.e("MyBookFragment", "Error loading books: ${e.message}")
            } finally {
                loadingDialog.dismiss()
            }
        }
    }

}
