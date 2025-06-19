package com.example.libmanagementsystem.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libmanagementsystem.R
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import com.example.libmanagementsystem.adapter.MyFavoriteAdapter
import com.example.libmanagementsystem.dialog.LoadingDialog
import com.example.libmanagementsystem.model.Book
import com.example.libmanagementsystem.repository.BookRepository
import com.example.libmanagementsystem.repository.FavoriteRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import android.content.Intent
import com.example.libmanagementsystem.activity.ActivityDetailBook

class MyFavoriteFragment : Fragment() {
    private lateinit var recycleView: RecyclerView
    private lateinit var myFavoriteAdapter: MyFavoriteAdapter

    private var bookList = mutableListOf<Book>()
    private var favoriteRepository = FavoriteRepository()
    private var bookRepository = BookRepository()

    private lateinit var userID: String

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_favorite, container, false)
        userID = Firebase.auth.currentUser!!.uid

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
                val favBookList = favoriteRepository.getFavoriteBooksId(userID)
                val booksToShow = bookRepository.getBooksByIds(favBookList?.bookIdList ?: emptyList())

                bookList.clear()
                bookList.addAll(booksToShow)

                myFavoriteAdapter = MyFavoriteAdapter(bookList) { book ->
                    val intent = Intent(requireContext(), ActivityDetailBook::class.java)
                    intent.putExtra("book", book)
                    startActivity(intent)
                }

                recycleView.adapter = myFavoriteAdapter
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MyFavoriteFragment", e.toString())
            } finally {
                loadingDialog.dismiss()
            }
        }
    }
}