package com.example.librarymanagementsystem.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.adapter.BookHomeAdapter
import com.example.librarymanagementsystem.model.Book
import com.example.librarymanagementsystem.repository.BookRepository
import kotlinx.coroutines.launch

class NewReleaseFragment : Fragment() {

    private lateinit var rvNewReleaseAll: RecyclerView
    private val bookRepository = BookRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_release, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvNewReleaseAll = view.findViewById(R.id.rvNewReleaseAll)

        // Thiết lập GridLayoutManager với 3 cột
        rvNewReleaseAll.layoutManager = GridLayoutManager(requireContext(), 3)

        // Sau khi đã có RecyclerView, bắt đầu load dữ liệu
        loadAllNewReleaseBooks()
    }

    private fun loadAllNewReleaseBooks() {
        lifecycleScope.launch {
            try {
                // Lấy toàn bộ sách từ Firestore
                val allBooks: List<Book> = bookRepository.getBooks()

                // Lọc những sách có publishYear != null, rồi sắp xếp giảm dần
                val sortedBooks = allBooks
                    .filter { it.publishYear != null }
                    .sortedByDescending { it.publishYear }
                    .take(60) // lấy tối đa 60 quyển

                // Gắn adapter
                rvNewReleaseAll.adapter = BookHomeAdapter(sortedBooks)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("NewReleaseFragment", "Exception khi load all new release", e)
                Toast.makeText(requireContext(), "Lỗi khi tải sách: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        // Tạo phương thức helper để dễ gọi fragment từ HomeFragment
        fun newInstance(): NewReleaseFragment {
            return NewReleaseFragment()
        }
    }
}