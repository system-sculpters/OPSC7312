package com.opsc.opsc7312.view.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.opsc.opsc7312.R
import com.opsc.opsc7312.model.data.model.AnalyticsResponse
import com.opsc.opsc7312.model.data.model.StockHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StockHistoryAdapter(private val context: Context, private val lineChart: LineChart) {
    fun updateGraph(value: List<StockHistory>) {
        // Prepare entries
        val entries = prepareStockData(value)

        setupGraph(entries)
    }

    private fun prepareStockData(stockHistoryList: List<StockHistory>): List<Entry> {
        val entries = mutableListOf<Entry>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

        for (stock in stockHistoryList) {
            val date = dateFormat.parse(stock.date) ?: continue
            val timestamp = date.time.toFloat() // Convert to milliseconds

            // Create an Entry with the timestamp and closing price
            entries.add(Entry(timestamp, stock.close))
        }

        return entries
    }

    private fun setupGraph(entries: List<Entry>) {
        val lineDataSet = LineDataSet(entries, context.getString(R.string.stock_price))
        lineDataSet.color = ContextCompat.getColor(context, R.color.blue)
        lineDataSet.valueTextColor = ContextCompat.getColor(context, R.color.black)
        lineDataSet.valueTextSize = 12f

        // Create LineData object
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.invalidate() // Refresh the chart

        // Customize the x-axis to display dates and position it at the bottom
        lineChart.xAxis.apply {
            valueFormatter = object : ValueFormatter() {
                private val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return dateFormat.format(Date(value.toLong()))
                }
            }
            position = XAxis.XAxisPosition.BOTTOM  // Position labels at the bottom
            setDrawGridLines(false)               // Remove x-axis grid lines
        }

        // Hide the right y-axis labels
        lineChart.axisRight.isEnabled = false

        // Remove grid lines from the left y-axis
        lineChart.axisLeft.setDrawGridLines(false)

        // Disable description label
        lineChart.description.isEnabled = false
    }

}