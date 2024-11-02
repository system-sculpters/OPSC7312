package com.opsc.opsc7312.view.observers

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.model.data.model.Investment
import com.opsc.opsc7312.model.data.model.InvestmentResponse
import com.opsc.opsc7312.view.adapter.InvestmentAdapter

class InvestmentObserver(
    private val adapter: InvestmentAdapter,
    private val amount: TextView,
    private val percentageIncrease: TextView,
    private val context: Context
) : Observer<InvestmentResponse> {

    // Invoked when the observed list of goals changes.
    override fun onChanged(value: InvestmentResponse) {
        // Refresh the GoalAdapter with the latest goals.
        adapter.updateInvestments(value.investments)

        // Call the method to update the displayed progress of the goals.
        val totalValue = getTotalValue(value.investments, value.balance)

        amount.text = "${AppConstants.formatAmount(totalValue)} ZAR"

        val percentage = percentageChange(totalValue, 10_000.00)

        if(percentage >= 0){
            percentageIncrease.text = "+${AppConstants.formatAmount(percentage)}%"
            percentageIncrease.setTextColor(ContextCompat.getColor(context, R.color.green))
        } else{
            percentageIncrease.text = "${AppConstants.formatAmount(percentage)}%"
            percentageIncrease.setTextColor(ContextCompat.getColor(context, R.color.red))
        }
        // Log the number of goals retrieved for debugging purposes.
        Log.d("Category", "Stocks retrieved: ${value.investments.size}\n $value")
    }


    private fun percentageChange(currentPrice: Double, previousClosePrice: Double): Double {
        return ((currentPrice - previousClosePrice) / previousClosePrice) * 100;
    }

    private fun getTotalValue(investments: List<Investment>, balance: Double): Double {
        var totalValue = balance

        for(investment in investments){
            totalValue += investment.quantity * investment.stockData?.currentPrice!!
        }

        return totalValue
    }
}