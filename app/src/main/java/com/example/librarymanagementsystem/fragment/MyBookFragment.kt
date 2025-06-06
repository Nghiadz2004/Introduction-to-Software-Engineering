package com.example.librarymanagementsystem.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.adapter.MyBookAdapter
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.model.BorrowBook
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.BorrowingRepository
import kotlinx.coroutines.launch

class MyBookFragment : Fragment() {
    private lateinit var recycleView: RecyclerView
    private lateinit var myBookAdapter: MyBookAdapter
    private val bookList = mutableListOf<Book>()
    private val borrowBookList = mutableListOf<BorrowBook>()

    private val borrowingRepo = BorrowingRepository()
    private val bookRepo = BookRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_my_book, container, false)

    companion object {
        fun newInstance(userID: String): MyBookFragment {
            val fragment = MyBookFragment()
            val args = Bundle()
            args.putString("USER_ID", userID)
            fragment.arguments = args
            return fragment
        }
    }

    @OptIn(UnstableApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycleView = view.findViewById(R.id.myBookRV)

        // Giả định bạn có readerId từ session hoặc auth
        val readerId = arguments?.getString("USER_ID") ?: ""

//        lifecycleScope.launch {
//            try {
//                // Lấy sách đã mượn
//                val borrowBooks = borrowingRepo.getBorrowBooksByReader(readerId)
//                borrowBookList.clear()
//                borrowBookList.addAll(borrowBooks)
//
//                // Lấy chi tiết sách
//                val allBooks = bookRepo.getBooks()
//                val booksToShow = allBooks.filter { book ->
//                    borrowBooks.any { it.bookId == book.id & it.bookId == }
//                }
//
//                bookList.clear()
//                bookList.addAll(booksToShow)
//
//                myBookAdapter = MyBookAdapter(bookList) { book ->
//                    // Mở chi tiết sách nếu muốn
////                    val intent = Intent(requireContext(), BookDetailActivity::class.java)
////                    intent.putExtra("BOOK_ID", book.id)
////                    startActivity(intent)
//                }
//
//                recycleView.adapter = myBookAdapter
//            } catch (e: Exception) {
//                Log.e("MyBookFragment", "Error loading books: ${e.message}")
//            }
//        }
    }
}
