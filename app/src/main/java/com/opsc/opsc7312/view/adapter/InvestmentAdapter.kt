package com.opsc.opsc7312.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.model.data.model.Investment
import com.opsc.opsc7312.model.data.model.Stock

class InvestmentAdapter(private val context: Context, private val onItemClick: (Investment, Double) -> Unit) : RecyclerView.Adapter<InvestmentAdapter.ViewHolder>() {

    // Mutable list to hold the transactions
    private val investments = mutableListOf<Investment>()

    // Function to update the adapter's data with a new list of transactions
    fun updateInvestments(data: List<Investment>) {
        investments.clear() // Clear the existing transactions
        investments.addAll(data) // Add new transactions to the list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    // Create a new ViewHolder for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for each transaction item and create a ViewHolder
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.stock_item, parent, false)
        return ViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Get the total number of transactions
    override fun getItemCount(): Int {
        return investments.size // Return the size of the transactions list
    }

    // Bind the transaction data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val investment = investments[position]

        // Load the image using Glide
        Glide.with(context)
            .load(investment.stockData?.logoUrl) // Use image URL or resource ID
            .placeholder(R.drawable.baseline_image_search_24) // Placeholder image
            .circleCrop()
            .into(holder.stockImage)

        holder.name.text = investment.stockData?.name

        holder.symbol.text = investment.symbol

        holder.price.text = "R${AppConstants.formatAmount(currentValue(investment))}"

        val percentage = calculatePercentageGainOrLoss(investment)

        if(percentage >= 0){
            holder.percentage.text = "+${AppConstants.formatAmount(percentage)}%"
            holder.percentage.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        } else{
            holder.percentage.text = "${AppConstants.formatAmount(percentage)}%"
            holder.percentage.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }

        holder.itemView.setOnClickListener {
            onItemClick(investment, percentage)
        }
    }

    // ViewHolder class to hold the views for each transaction item
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stockImage: ImageView = itemView.findViewById(R.id.stock_img);
        val symbol: TextView = itemView.findViewById(R.id.stock_symbol);
        val name: TextView = itemView.findViewById(R.id.stock_name);
        val price: TextView = itemView.findViewById(R.id.stock_price);
        val percentage: TextView = itemView.findViewById(R.id.stock_gain);

    }

    private fun percentageChange(currentPrice: Double, previousClosePrice: Double): Double {
        return ((currentPrice - previousClosePrice) / previousClosePrice) * 100;
    }

    private fun calculateGainOrLoss(investment: Investment): Double {
        val currentTotalValue = investment.stockData?.currentPrice?.times(investment.quantity) ?: 0.0
        return currentTotalValue - investment.currentValue
    }

    private fun currentValue(investment: Investment): Double {
        return investment.stockData?.currentPrice?.times(investment.quantity) ?: 0.0
    }
    private fun calculatePercentageGainOrLoss(investment: Investment): Double {
        val gainOrLoss = calculateGainOrLoss(investment)
        return if (investment.currentValue != 0.0) {
            (gainOrLoss / investment.currentValue) * 100
        } else {
            0.0 // Avoid division by zero
        }
    }
}