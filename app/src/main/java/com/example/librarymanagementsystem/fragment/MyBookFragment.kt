package com.example.librarymanagementsystem.fragment

import android.content.Intent
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

        // Load data from data base
        lifecycleScope.launch {
            val books = bookRepository.getBooks()

            bookList.clear()
            bookList.addAll(books)

            myBookAdapter = MyBookAdapter(bookList) { book ->
                // TODO: BookDetailActivity
//                val intent = Intent(requireContext(), BookDetailActivity::class.java)
//                intent.putExtra("BOOK_ID", book.id)
//                startActivity(intent)
            }

            recycleView.adapter = myBookAdapter
        }
    }
}