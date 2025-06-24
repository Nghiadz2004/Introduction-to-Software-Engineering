package com.example.librarymanagementsystem.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import com.example.librarymanagementsystem.adapter.MyFavoriteAdapter
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import com.example.librarymanagementsystem.repository.FavoriteRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.content.Intent
import android.widget.TextView
import com.example.librarymanagementsystem.activity.ActivityDetailBook
import com.example.librarymanagementsystem.cache.FavoriteCache
import com.example.librarymanagementsystem.service.FavoriteManager

class MyFavoriteFragment : Fragment() {
    private lateinit var recycleView: RecyclerView
    private lateinit var myFavoriteAdapter: MyFavoriteAdapter
    private lateinit var notifyTV: TextView
    private val bookRepository = BookRepository()
    private var bookList = mutableListOf<Book>()
    private var favoriteManager = FavoriteManager(bookRepository)

    private lateinit var userID: String

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_favorite, container, false)
        userID = Firebase.auth.currentUser!!.uid

        notifyTV = view.findViewById(R.id.notifyTV)
        recycleView = view.findViewById(R.id.myFavoriteRV)
        recycleView.layoutManager = LinearLayoutManager(requireContext())

        loadingDialog = LoadingDialog(requireContext())

        loadMyFavorite(userID)

        return view
    }

    private fun loadMyFavorite(userID: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            loadingDialog.show()
            try {
                val booksToShow = favoriteManager.getFavoriteBooks()

                bookList.clear()
                bookList.addAll(booksToShow)

                if (bookList.isEmpty()) {
                    // Danh sách trống
                    notifyTV.visibility = View.VISIBLE
                    recycleView.visibility = View.GONE
                } else {
                    // Danh sách có phần tử
                    notifyTV.visibility = View.GONE
                    recycleView.visibility = View.VISIBLE

                    myFavoriteAdapter = MyFavoriteAdapter(bookList) { book ->
                        val intent = Intent(requireContext(), ActivityDetailBook::class.java)
                        intent.putExtra("book", book)
                        startActivity(intent)
                    }

                    recycleView.adapter = myFavoriteAdapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MyFavoriteFragment", e.toString())
            } finally {
                loadingDialog.dismiss()
            }
        }
    }
}