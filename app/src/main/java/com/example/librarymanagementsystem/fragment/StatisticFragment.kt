package com.example.librarymanagementsystem.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.service.StatisticManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class StatisticFragment : Fragment() {

    private lateinit var totalBooksNumberTV: TextView
    private lateinit var totalBooksLentNumberTV: TextView
    private lateinit var totalReturnBooksNumberTV: TextView
    private lateinit var totalLostBooksNumberTV: TextView
    private lateinit var totalFineNumberTV: TextView

    private lateinit var totalBooksLentNumberTV_2: TextView
    private lateinit var totalReturnBooksNumberTV_2: TextView
    private lateinit var totalLostBooksNumberTV_2: TextView
    private lateinit var totalFineNumberTV_2: TextView

    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistic, container, false)

        // Ánh xạ view
        totalBooksNumberTV = view.findViewById(R.id.totalBooksNumberTV)
        totalBooksLentNumberTV = view.findViewById(R.id.totalBooksLentNumberTV)
        totalReturnBooksNumberTV = view.findViewById(R.id.totalReturnBooksNumberTV)
        totalLostBooksNumberTV = view.findViewById(R.id.totalLostBooksNumberTV)
        totalFineNumberTV = view.findViewById(R.id.totalFineNumberTV)

        totalBooksLentNumberTV_2 = view.findViewById(R.id.totalBooksLentNumberTV_2)
        totalReturnBooksNumberTV_2 = view.findViewById(R.id.totalReturnBooksNumberTV_2)
        totalLostBooksNumberTV_2 = view.findViewById(R.id.totalLostBooksNumberTV_2)
        totalFineNumberTV_2 = view.findViewById(R.id.totalFineNumberTV_2)

        // Khởi tạo LoadingDialog
        loadingDialog = LoadingDialog(requireContext())

        loadStatistic()

        return view
    }

    private fun loadStatistic() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Hiện dialog
            loadingDialog.show()

            // Tải dữ liệu
            totalBooksNumberTV.text = StatisticManager().getTotalBooks().toString()
            totalBooksLentNumberTV.text = StatisticManager().getTotalBooksLent().toString()
            totalReturnBooksNumberTV.text = StatisticManager().getTotalReturnBooks().toString()
            totalLostBooksNumberTV.text = StatisticManager().getTotalLostBooks().toString()
            totalFineNumberTV.text = StatisticManager().getTotalFine().toString()

            totalBooksLentNumberTV_2.text = StatisticManager().getTotalBooksLent(7).toString()
            totalReturnBooksNumberTV_2.text = StatisticManager().getTotalReturnBooks(7).toString()
            totalLostBooksNumberTV_2.text = StatisticManager().getTotalLostBooks(7).toString()
            totalFineNumberTV_2.text = StatisticManager().getTotalFine(7).toString()

            // Ẩn dialog khi tải xong
            loadingDialog.dismiss()
        }
    }
}
