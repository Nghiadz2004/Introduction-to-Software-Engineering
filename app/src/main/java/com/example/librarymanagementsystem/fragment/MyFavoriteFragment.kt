package com.example.librarymanagementsystem.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.librarymanagementsystem.adapter.MyFavoriteAdapter
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.FavoriteRepository

// TODO: Pass readerID first
class MyFavoriteFragment : Fragment() {
    private lateinit var recycleView: RecyclerView
    private lateinit var myFavoriteAdapter: MyFavoriteAdapter

    private var bookList = mutableListOf<Book>()
    private var favoriteRepository = FavoriteRepository()
    private var bookRepository = BookRepository()

    private lateinit var userID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_my_favorite, container, false)

        recycleView = view.findViewById(R.id.myFavoriteRV)
        userID = arguments?.getString("USER_ID") ?: ""

        loadMyFavorite(userID)

        return view
    }

    companion object {
        fun newInstance(userID: String): MyFavoriteFragment {
            val fragment = MyFavoriteFragment()
            val args = Bundle()
            args.putString("USER_ID", userID)
            fragment.arguments = args
            return fragment
        }
    }

    private fun loadMyFavorite(userID: String) {

        lifecycleScope.launch {
            try {
                val favBookList = favoriteRepository.getFavoriteBooksId(userID)
                val allBooks = bookRepository.getBooks()
                val booksToShow = allBooks.filter { book ->
                    favBookList?.bookIdList?.contains(book.id) == true
                }

                bookList.clear()
                bookList.addAll(booksToShow)

                myFavoriteAdapter = MyFavoriteAdapter(userID, bookList) { book ->
                    // TODO: Open BookDetailActivity
    //                val intent = Intent(requireContext(), BookDetailActivity::class.java)
    //                intent.putExtra("BOOK_ID", book.id)
    //                startActivity(intent)
                }

                recycleView.adapter = myFavoriteAdapter
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MyFavoriteFragment", e.toString())
            }
        }
    }
}