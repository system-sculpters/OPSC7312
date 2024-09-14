package com.opsc.opsc7312.view.adapter

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.model.data.model.AnalyticsResponse
import com.opsc.opsc7312.model.data.model.CategoryExpense
import com.opsc.opsc7312.model.data.model.IncomeExpense
import java.util.Calendar

class AnalyticsAdapter(private val context: Context,
    private val pieChart: PieChart, private val incomeExpenseChart: BarChart,
                       private val incomeChart: BarChart,
                       private val totalIncome: TextView, private val textColor: Int) {

    fun updateGraph(value: AnalyticsResponse) {
        setupPieChart(value.categoryStats)
        setupIncomeExpenseChart(value.dailyTransactions)
        setupIncome(value.transactionsByMonth)
    }

    private fun setupPieChart(categoryList: List<CategoryExpense>) {
        val pieEntries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        for (category in categoryList) {
            pieEntries.add(PieEntry(category.transactionCount.toFloat(), category.name))

            // Get color from the dictionary or fallback to a default color
            val colorResId = getColor(category.color) ?: R.color.black // Default to black if color not found
            val color = ContextCompat.getColor(context, colorResId)
            colors.add(color)
        }

        val dataSet = PieDataSet(pieEntries, "")
        dataSet.colors = colors

        val data = PieData(dataSet)
        pieChart.data = data

        // Customize PieChart appearance
        pieChart.description.isEnabled = false
        pieChart.isRotationEnabled = true
        pieChart.setUsePercentValues(true)

        // Remove entry labels (text on the chart)
        pieChart.setDrawEntryLabels(false)

        // Move the legend to the right
        val legend = pieChart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false) // Keep the legend outside the chart

        // Optional: Customize legend appearance (font size, color, etc.)
        legend.textSize = 12f
        legend.textColor = ContextCompat.getColor(context, R.color.dark_grey)

        pieChart.animateY(1400)
        pieChart.invalidate() // Refresh the chart
    }



    private fun setupIncomeExpenseChart(incomeExpense: List<IncomeExpense>) {
        val incomeEntries = ArrayList<BarEntry>()
        val expenseEntries = ArrayList<BarEntry>()
        val labels = mutableListOf<String>()

        // Populating data for the income and expense bars
        for (i in incomeExpense.indices) {


            incomeEntries.add(BarEntry(i.toFloat(), incomeExpense[i].income.toFloat()))
            expenseEntries.add(BarEntry(i.toFloat(), incomeExpense[i].expense.toFloat()))
            labels.add(incomeExpense[i].label)
        }

        // Creating datasets for income and expenses
        val incomeDataSet = BarDataSet(incomeEntries, "Income")

        val greenResId = getColor("Green") ?: R.color.black // Default to black if color not found
        val colorGreen = ContextCompat.getColor(context, greenResId)
        incomeDataSet.color = colorGreen  // Set color for income bars
        incomeDataSet.valueTextColor = textColor

        val expenseDataSet = BarDataSet(expenseEntries, "Expense")

        val redResId = getColor("Red") ?: R.color.black // Default to black if color not found
        val colorRed = ContextCompat.getColor(context, redResId)

        expenseDataSet.color = colorRed  // Set color for expense bars
        expenseDataSet.valueTextColor = textColor

        // Combine the datasets into BarData
        val data = BarData(incomeDataSet, expenseDataSet)

        // Customize the bar chart appearance


        // Customize the bar width
        val barWidth = 0.2f  // Width of each bar
        val barSpace = 0.05f // Space between bars within a group
        val groupSpace = 0.4f // Space between groups

        // Set bar width
        data.barWidth = barWidth

        // Set the BarData to the chart
        incomeExpenseChart.data = data

        val legend = incomeExpenseChart.legend
        legend.textColor = textColor

        // Customize X-axis labels
        val xAxis = incomeExpenseChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)  // Set custom labels
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)  // Disable grid lines
        xAxis.granularity = 1f  // Ensure labels align with bars
        xAxis.isGranularityEnabled = true
        xAxis.textColor = textColor


        val yAxisLeft = incomeExpenseChart.axisLeft
        yAxisLeft.textColor = textColor  // Replace with your color

        val yAxisRight = incomeExpenseChart.axisRight
        yAxisRight.textColor = textColor

        // Customize other chart properties
        incomeExpenseChart.axisRight.isEnabled = false  // Disable right Y-axis
        incomeExpenseChart.description.isEnabled = false  // Disable description
        incomeExpenseChart.setFitBars(true)  // Make bars fit nicely
        incomeExpenseChart.setPinchZoom(false)  // Disable pinch-to-zoom
        incomeExpenseChart.animateY(1500)  // Add animation

        // Group the bars
        incomeExpenseChart.groupBars(0f, groupSpace, barSpace)  // Group the bars together

        // Refresh the chart
        incomeExpenseChart.invalidate()

    }

    private fun setupIncome(incomeExpense: List<IncomeExpense>) {
        totalIncome.text = totalIncome(incomeExpense)

        val incomeEntries = ArrayList<BarEntry>()
        val labels = mutableListOf<String>()

        // Populating data for the income bars
        for (i in incomeExpense.indices) {
            incomeEntries.add(BarEntry(i.toFloat(), incomeExpense[i].income.toFloat()))
            labels.add(incomeExpense[i].label)
        }

        // Creating the dataset for income
        val incomeDataSet = BarDataSet(incomeEntries, "Income")

        // Setting color for income bars
        val colorBlue = ContextCompat.getColor(context, R.color.blue)  // Replace with your desired color
        incomeDataSet.color = colorBlue
        incomeDataSet.valueTextColor = textColor

        // Create BarData with the dataset
        val data = BarData(incomeDataSet)

        // Set bar width
        data.barWidth = 0.7f  // Adjust this value for desired bar width

        // Set the data to the chart
        incomeChart.data = data

        val legend = incomeChart.legend
        legend.textColor = textColor
        // Customize X-axis labels
        val xAxis = incomeChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)  // Set custom labels
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)  // Disable grid lines
        xAxis.granularity = 1f  // Ensure labels align with bars
        xAxis.isGranularityEnabled = true
        xAxis.textColor = textColor


        val yAxisLeft = incomeChart.axisLeft
        yAxisLeft.textColor = textColor // Replace with your color

        val yAxisRight = incomeChart.axisRight
        yAxisRight.textColor = textColor

        // Customize the Y-axis and other chart properties
        incomeChart.axisRight.isEnabled = false  // Disable right Y-axis
        incomeChart.description.isEnabled = false  // Disable description
        incomeChart.setFitBars(true)  // Make bars fit nicely
        incomeChart.setPinchZoom(false)  // Disable pinch-to-zoom
        incomeChart.animateY(1500)  // Add animation

        // Refresh the chart
        incomeChart.invalidate()
    }

    private fun totalIncome(income: List<IncomeExpense>): String{
        var total = 0.0
        for (inc in income){
            total+= inc.income
        }
        return "R ${AppConstants.formatAmount(total)}"
    }

    private fun getColor(color: String): Int {
        return AppConstants.COLOR_DICTIONARY[color] ?: R.color.dark_grey // Return black if color not found
    }
}