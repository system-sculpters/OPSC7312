package com.opsc.opsc7312.view.adapter

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.widget.ProgressBar
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
import com.github.mikephil.charting.formatter.ValueFormatter
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.model.data.model.AnalyticsResponse
import com.opsc.opsc7312.model.data.model.CategoryExpense
import com.opsc.opsc7312.model.data.model.Goal
import com.opsc.opsc7312.model.data.model.IncomeExpense
import com.opsc.opsc7312.view.custom.PercentValueFormatter


class AnalyticsAdapter(
    private val context: Context,
    private val pieChart: PieChart,
    private val incomeExpenseChart: BarChart,
    private val incomeChart: BarChart,
    private val totalIncome: TextView,
    private val textColor: Int,
    private val amount: TextView,
    private val remainingAmount: TextView,
    private val progressBar: ProgressBar
) {
    // This class was adapted from YouTube
    // https://youtu.be/-TGUV_LbcmE?si=VItXcDdnX_I8CsiE
    // Admin Grabs Media
    // https://www.youtube.com/@AdminGrabsMedia

    // Updates all charts and statistics displayed in the analytics section.
    fun updateGraph(value: AnalyticsResponse) {
        setupPieChart(value.categoryStats) // Set up pie chart based on category statistics.
        setupIncomeExpenseChart(value.dailyTransactions) // Set up income vs expense chart with daily transactions.
        updateIncomeChart("6 Months", value.transactionsByMonth) // Update income chart for the last 6 months.
        setupGoals(value.goals) // Set up the goal progress display based on the user's goals.
    }

    // Updates the income vs expense chart with the provided data.
    fun updateIncomeExpenseChart(value: List<IncomeExpense>) {
        setupIncomeExpenseChart(value) // Initialize the income vs expense chart with new data.
    }

    // Updates the income chart based on the selected month duration.
    fun updateIncomeChart(month: String, value: List<IncomeExpense>) {
        var incomeList: List<IncomeExpense> = listOf()

        Log.d("this is the values", "List count: ${value.size}\nList: $value")
        if (month == "3 Months") {
            incomeList = value.take(3) // Get data for the last 3 months.
        } else if (month == "6 Months") {
            incomeList = value.take(6) // Get data for the last 6 months.
        } else {
            incomeList = value // If no specific duration, use the entire list.
        }

        Log.d("this is the income list", "List count: ${incomeList.size}\nList: $incomeList")
        setupIncome(incomeList) // Set up the income chart with the selected data.
    }

    // Sets up the goal progress display based on the user's goals.
    private fun setupGoals(goalsList: List<Goal>) {
        Log.d("this is the goals list", "List count: ${goalsList.size}\nList: $goalsList")

        var totalTargetAmount = 0.0 // Initialize the total target amount for all goals.
        var totalCurrentAmount = 0.0 // Initialize the total current amount for all goals.

        // Calculate total current and target amounts for each goal.
        for (goal in goalsList) {
            totalCurrentAmount += goal.currentamount // Sum the current amounts.
            totalTargetAmount += goal.targetamount // Sum the target amounts.
        }

        // Calculate the remaining amount needed to reach the target.
        val amountLeft = totalTargetAmount - totalCurrentAmount

        // Update the displayed amounts for current and target.
        amount.text = "${AppConstants.formatAmount(totalCurrentAmount)}/${AppConstants.formatAmount(totalTargetAmount)} ZAR"
        remainingAmount.text = "${AppConstants.formatAmount(amountLeft)} ZAR remaining to achieve your goal"

        // Calculate and set the progress for the progress bar.
        val progress = if (totalTargetAmount > 0) {
            (totalCurrentAmount / totalTargetAmount * 100).toInt() // Calculate progress as a percentage.
        } else {
            0 // No progress if target amount is zero.
        }
        progressBar.progress = progress // Update progress bar with calculated progress.

        Log.d("this is the goals list", "\n$amountLeft $totalTargetAmount $totalCurrentAmount" +
                "${AppConstants.formatAmount(totalCurrentAmount)}/${AppConstants.formatAmount(totalTargetAmount)} ZAR\n" +
                "${AppConstants.formatAmount(amountLeft)} ZAR remaining to achieve your goal")
    }

    // Sets up the pie chart to visualize category expenses.
    private fun setupPieChart(categoryList: List<CategoryExpense>) {
        // This method was adapted from YouTube
        // https://youtu.be/sdKfUClMo0s?si=D7tovvmjcGXoC-N4
        // Admin Grabs Media
        // https://www.youtube.com/@AdminGrabsMedia

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
        data.setValueFormatter(PercentValueFormatter())
        pieChart.data = data

        // Customize PieChart appearance
        pieChart.description.isEnabled = false
        pieChart.isRotationEnabled = true
        pieChart.setUsePercentValues(true)

        // Remove entry labels (text on the chart)
        pieChart.setDrawEntryLabels(false)

        // Adjust the hole size to create more space for labels
        pieChart.holeRadius = 50f // Increase this to make the hole bigger
        pieChart.transparentCircleRadius = 55f // Optional, increase the transparent circle radius

        // Move the PieChart to the left by increasing the right offset and decreasing the left offset
        pieChart.setExtraOffsets(0f, 10f, 50f, 10f) // Left, Top, Right, Bottom offsets

        // Customize the legend (labels) appearance and position
        val legend = pieChart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT // Place the labels at the end (right)
        legend.orientation = Legend.LegendOrientation.VERTICAL // Display labels in vertical orientation
        legend.setDrawInside(false) // Keep the legend outside the chart
        legend.textSize = 12f
        legend.textColor = textColor
        legend.xOffset = -70f // Adjust this to move the legend more to the right

        // Optionally, reduce the label size if needed
        data.setValueTextSize(10f)

        // Animate and refresh the chart
        pieChart.animateY(1400)

        // Set the color of the center hole using the ?attr/colorItemLayoutBg attribute
        val typedValue = TypedValue()
        val theme = context.theme
        theme?.resolveAttribute(R.attr.colorItemLayoutBg, typedValue, true)
        val holeColor = ContextCompat.getColor(context, typedValue.resourceId)
        pieChart.setHoleColor(holeColor)

        pieChart.invalidate() // Refresh the chart
    }


    private fun setupIncomeExpenseChart(incomeExpense: List<IncomeExpense>) {
        // This method was adapted from YouTube
        // https://youtu.be/-TGUV_LbcmE?si=VItXcDdnX_I8CsiE
        // Admin Grabs Media
        // https://www.youtube.com/@AdminGrabsMedia
        val reversedList = incomeExpense.reversed()
        val incomeEntries = ArrayList<BarEntry>()
        val expenseEntries = ArrayList<BarEntry>()
        val labels = mutableListOf<String>()

        // Populating data for the income and expense bars
        for (i in reversedList.indices) {


            incomeEntries.add(BarEntry(i.toFloat(), reversedList[i].income.toFloat()))
            expenseEntries.add(BarEntry(i.toFloat(), reversedList[i].expense.toFloat()))
            labels.add(reversedList[i].label)
        }

        // Creating datasets for income and expenses
        val incomeDataSet = BarDataSet(incomeEntries, context.getString(R.string.income))

        val greenResId = getColor("Green") ?: R.color.black // Default to black if color not found
        val colorGreen = ContextCompat.getColor(context, greenResId)
        incomeDataSet.color = colorGreen  // Set color for income bars
        incomeDataSet.valueTextColor = textColor

        val expenseDataSet = BarDataSet(expenseEntries, context.getString(R.string.expense))

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
        // This method was adapted from YouTube
        // https://youtu.be/-TGUV_LbcmE?si=VItXcDdnX_I8CsiE
        // Admin Grabs Media
        // https://www.youtube.com/@AdminGrabsMedia
        val lastSixMonths = incomeExpense.reversed()
        Log.d("incomeExpense list", "this is the count: ${incomeExpense.size}")
        totalIncome.text = totalIncome(lastSixMonths)

        val incomeEntries = ArrayList<BarEntry>()
        val labels = mutableListOf<String>()

        // Populating data for the income bars
        for (i in lastSixMonths.indices) {
            incomeEntries.add(BarEntry(i.toFloat(), lastSixMonths[i].income.toFloat()))
            labels.add(lastSixMonths[i].label)
        }

        // Creating the dataset for income
        val incomeDataSet = BarDataSet(incomeEntries, context.getString(R.string.income))

        // Setting color for income bars
        val colorBlue = ContextCompat.getColor(context, R.color.blue)  // Replace with your desired color
        incomeDataSet.color = colorBlue
        incomeDataSet.valueTextColor = textColor

        // Create BarData with the dataset
        val data = BarData(incomeDataSet)

        // Set bar width
        data.barWidth = 0.7f  // Adjust this value for desired bar width
        if(incomeEntries.size > 6){
            data.barWidth = 0.4f  // Adjust this value for desired bar width
        }
        // Set the data to the chart
        incomeChart.data = data

        val legend = incomeChart.legend
        legend.textColor = textColor
        // Customize X-axis labels
        val xAxis = incomeChart.xAxis
        //xAxis.setLabelCount(lastSixMonths.size, true)
        //xAxis.setAvoidFirstLastClipping(true)
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)  // Set custom labels
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)  // Disable grid lines
        xAxis.granularity = 1f  // Ensure labels align with bars
        xAxis.isGranularityEnabled = true
        xAxis.textColor = textColor

        // if there are more than six entries
        if(incomeEntries.size > 6){
            xAxis.setLabelRotationAngle(45f)  // Rotate labels to avoid overlapping
            xAxis.spaceMin = 0.35f  // Adjust spacing to make room for labels

        }


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

    // Function to calculate the total income from a list of IncomeExpense objects
    private fun totalIncome(income: List<IncomeExpense>): String {
        var total = 0.0 // Initialize total income to 0.0

        // Iterate through each IncomeExpense object in the income list
        for (inc in income) {
            total += inc.income // Add the income of the current object to the total
        }

        // Return the total income formatted as a string with a "R" prefix
        return "R ${AppConstants.formatAmount(total)}"
    }


    // Helper function to retrieve color resource based on category name.
    private fun getColor(category: String): Int? {
        return when (category) {
            "Green" -> R.color.green
            "Red" -> R.color.red
            "Blue" -> R.color.blue
            "Yellow" -> R.color.yellow
            // Add more color mappings as needed.
            else -> null // Return null if no color mapping found.
        }
    }
}
