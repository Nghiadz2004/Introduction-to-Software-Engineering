package com.example.librarymanagementsystem.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.adapter.MyBookAdapter
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import kotlinx.coroutines.launch

class MyBookFragment :
    Fragment()
{
    private lateinit var recycleView: RecyclerView
    private lateinit var myBookAdapter: MyBookAdapter
    private var bookList = mutableListOf<Book>()
    private var bookRepository = BookRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycleView = view.findViewById(R.id.myBookRV)
        myBookAdapter = MyBookAdapter(bookList) {book ->
            // TODO: Go to book detail activity when being pressed.
            val intent = Intent(this, ::class.java)

            startActivity(intent)
        }
        recycleView.adapter = myBookAdapter

        // Load dữ liệu (ví dụ từ repository)
//        lifecycleScope.launch {
//            bookRepository.getBooks { books ->
//                bookList.clear()
//                bookList.addAll(books)
//                // Book detail is fragment:
//                myBookAdapter = MyBookAdapter(bookList) { book ->
//                    val fragment = BookDetailFragment.newInstance(book.id ?: "")
//                    parentFragmentManager.beginTransaction()
//                        .replace(R.id.fragmentContainer, fragment)
//                        .addToBackStack(null)
//                        .commit()
//                }
//
//                recycleView.adapter = myBookAdapter
//
//                // Book detail is activity
//                myBookAdapter = MyBookAdapter(bookList) {book ->
//                    // TODO: Go to book detail activity when being pressed.
//                    val intent = Intent(this, ::class.java)
//
//                    startActivity(intent)
//                }
//
//                recycleView.adapter = myBookAdapter
//            }
//        }
    }
}