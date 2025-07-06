package com.example.librarymanagementsystem.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.librarymanagementsystem.R
import com.example.librarymanagementsystem.dialog.LoadingDialog
import com.example.librarymanagementsystem.service.StatisticManager
import kotlinx.coroutines.launch
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import com.github.mikephil.charting.components.Legend
import java.text.NumberFormat
import java.util.Locale
import android.util.Log

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

    private lateinit var pieChart1: PieChart
    private lateinit var pieChart2: PieChart
    private lateinit var pieChart3: PieChart
    private lateinit var pieChart4: PieChart

    private lateinit var loadingDialog: LoadingDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistic, container, false)

        // View binding
        totalBooksNumberTV = view.findViewById(R.id.totalBooksNumberTV)
        totalBooksLentNumberTV = view.findViewById(R.id.totalBooksLentNumberTV)
        totalReturnBooksNumberTV = view.findViewById(R.id.totalReturnBooksNumberTV)
        totalLostBooksNumberTV = view.findViewById(R.id.totalLostBooksNumberTV)
        totalFineNumberTV = view.findViewById(R.id.totalFineNumberTV)

        totalBooksLentNumberTV_2 = view.findViewById(R.id.totalBooksLentNumberTV_2)
        totalReturnBooksNumberTV_2 = view.findViewById(R.id.totalReturnBooksNumberTV_2)
        totalLostBooksNumberTV_2 = view.findViewById(R.id.totalLostBooksNumberTV_2)
        totalFineNumberTV_2 = view.findViewById(R.id.totalFineNumberTV_2)

        pieChart1 = view.findViewById(R.id.pieChart1)
        pieChart2 = view.findViewById(R.id.pieChart2)
        pieChart3 = view.findViewById(R.id.pieChart3)
        pieChart4 = view.findViewById(R.id.pieChart4)

        // Initiate LoadingDialog
        loadingDialog = LoadingDialog(requireContext())

        loadStatistic()

        return view
    }

    private fun formatCurrencyVND(amount: Long): String {
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(amount)} VND"
    }

    private fun loadStatistic() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Show dialog
            loadingDialog.show()

            val totalBook = StatisticManager().getTotalBooks()

            val totalLent = StatisticManager().getTotalBooksLent()
            val totalReturn = StatisticManager().getTotalReturnBooks()
            val totalLost = StatisticManager().getTotalLostBooks()
            val totalFine = StatisticManager().getTotalFine()

            val totalLent7 = StatisticManager().getTotalBooksLent(7)
            val totalReturn7 = StatisticManager().getTotalReturnBooks(7)
            val totalLost7 = StatisticManager().getTotalLostBooks(7)
            val totalFine7 = StatisticManager().getTotalFine(7)

            totalBooksNumberTV.text = totalBook.toString()

            totalBooksLentNumberTV.text = totalLent.toString()
            totalReturnBooksNumberTV.text = totalReturn.toString()
            totalLostBooksNumberTV.text = totalLost.toString()
            totalFineNumberTV.text = formatCurrencyVND(totalFine.toLong())

            totalBooksLentNumberTV_2.text = totalLent7.toString()
            totalReturnBooksNumberTV_2.text = totalReturn7.toString()
            totalLostBooksNumberTV_2.text = totalLost7.toString()
            totalFineNumberTV_2.text = totalFine7.toString()

            setupPieChart(
                pieChart1,
                listOf(
                    totalLent.toFloat(),
                    totalReturn.toFloat(),
                    totalLost.toFloat()
                ),
                listOf(
                    "Lent",
                    "Return",
                    "Lost"
                ),
                "All Time",
                false
            )

            setupPieChart(
                pieChart2,
                listOf(
                    totalLent7.toFloat(),
                    totalReturn7.toFloat(),
                    totalLost7.toFloat()
                ),
                listOf(
                    "Lent",
                    "Return",
                    "Lost"
                ),
                "7 Days",
                false
            )

            val (startCurrent, endCurrent) = StatisticManager().getMonthDateRange(0)
            val (startPrevious, endPrevious) = StatisticManager().getMonthDateRange(-1)

            val currentMonthStats = StatisticManager().countCategory(startCurrent, endCurrent)
            val previousMonthStats = StatisticManager().countCategory(startPrevious, endPrevious)

            Log.e("LEGENDKEYCURRENT", currentMonthStats.keys.toList().toString())
            Log.e("LEGENDKEYPREVIOUS", previousMonthStats.keys.toList().toString())

            setupPieChart(
                pieChart3,
                currentMonthStats.values.map { it.toFloat() },
                currentMonthStats.keys.toList(),
                "All Time",
                isLegend = true,
                isLabel = false,
                isValuePercentage = true,
                bottomOffSet = 10f
            )

            setupPieChart(
                pieChart4,
                previousMonthStats.values.map { it.toFloat() },
                previousMonthStats.keys.toList(),
                "Previous Month",
                isLegend = true,
                isLabel = false,
                isValuePercentage = true,
                bottomOffSet = 10f
            )

            // Dismiss dialog
            loadingDialog.dismiss()
        }
    }

    private fun setupPieChart(
        pieChart: PieChart,
        values: List<Float>,
        labels: List<String>,
        centerText: String,
        isLegend: Boolean = true,
        isLabel: Boolean = true,
        isValue: Boolean = true,
        isValuePercentage: Boolean = false,
        bottomOffSet: Float = 0f
    ) {
        if (values.isEmpty() || values.all { it == 0f }) {
            pieChart.clear()
            pieChart.setNoDataText("No Data")
            pieChart.setNoDataTextColor(R.color.light_blue)
            pieChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD)
            return
        }

        if (values.size != labels.size) {
            throw IllegalArgumentException("Number of value and label doesnt match")
        }

        // Bỏ các phần tử có value = 0f
        val filteredData = values.mapIndexedNotNull { index, value ->
            if (value > 0f) PieEntry(value, labels[index]) else null
        }

        // Nếu tất cả đều bị loại (value == 0f), thì không hiển thị chart
        if (filteredData.isEmpty()) {
            pieChart.clear()
            pieChart.centerText = SpannableString("Không có dữ liệu").apply {
                setSpan(RelativeSizeSpan(0.9f), 0, length, 0)
                setSpan(StyleSpan(Typeface.ITALIC), 0, length, 0)
            }
            pieChart.legend.isEnabled = false
            pieChart.invalidate()
            return
        }

        val dataSet = PieDataSet(filteredData, "")
        val colors = mutableListOf<Int>()
        colors.addAll(ColorTemplate.MATERIAL_COLORS.toList())
        colors.addAll(ColorTemplate.COLORFUL_COLORS.toList())
        dataSet.colors = colors
        dataSet.sliceSpace = 1.5f
        dataSet.valueTextSize = 8f

        dataSet.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

        val pieData = PieData(dataSet)

        // Center text có định dạng
        val styledCenterText = SpannableString(centerText).apply {
            setSpan(RelativeSizeSpan(1.0f), 0, length, 0)
            setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)
        }

        // Toggle value
        pieData.setDrawValues(isValue)

        // Toggle label
        pieChart.setDrawEntryLabels(isLabel)

        // Legend management
        val legend = pieChart.legend
        legend.isEnabled = isLegend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.isWordWrapEnabled = true

        // Content management
        pieChart.data = pieData
        pieChart.setUsePercentValues(isValuePercentage)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.description.isEnabled = false
        pieChart.centerText = styledCenterText
        pieChart.setExtraOffsets(0f, 2f, 0f, bottomOffSet) // thu hẹp hoặc nới lề

        // Pie size management
        pieChart.isDrawHoleEnabled = true // bật lỗ tròn giữa
        pieChart.holeRadius = 50f
        pieChart.transparentCircleRadius = 55f

        // No data text management
        pieChart.setNoDataText("No Data")
        pieChart.setNoDataTextColor(R.color.light_blue)             // Đổi màu
        pieChart.setNoDataTextTypeface(Typeface.DEFAULT_BOLD)       // Đổi font

        pieChart.setEntryLabelTextSize(8f)
        pieChart.invalidate()
    }
}
