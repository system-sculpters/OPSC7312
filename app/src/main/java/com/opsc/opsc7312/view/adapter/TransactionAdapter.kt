package com.opsc.opsc7312.view.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.model.data.model.Transaction

// This class adapted from geeksforgeeks
// https://www.geeksforgeeks.org/android-recyclerview/
// BaibhavOjha
// https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user

class TransactionAdapter(private val onItemClick: (Transaction) -> Unit) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    // Mutable list to hold the transactions
    private val transactions = mutableListOf<Transaction>()

    // Function to update the adapter's data with a new list of transactions
    fun updateTransactions(data: List<Transaction>) {
        transactions.clear() // Clear the existing transactions
        transactions.addAll(data) // Add new transactions to the list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    // Create a new ViewHolder for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for each transaction item and create a ViewHolder
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item_layout, parent, false)
        return ViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Get the total number of transactions
    override fun getItemCount(): Int {
        return transactions.size // Return the size of the transactions list
    }

    // Bind the transaction data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position] // Get the current transaction

        // Set a click listener for the transaction item
        holder.itemView.setOnClickListener {
            onItemClick(transaction) // Trigger the onItemClick callback with the current transaction
        }

        // Retrieve the icon and background color for the transaction's category
        val iconImageResId = AppConstants.ICONS[transaction.category.icon]
        val categoryBackgroundColor = AppConstants.COLOR_DICTIONARY[transaction.category.color]

        // Determine the original background color, defaulting to primary color if not found
        val originalColor = if (categoryBackgroundColor != null) {
            ContextCompat.getColor(holder.itemView.context, categoryBackgroundColor)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.primary) // Set a default color
        }

        // Set the transaction name text
        holder.transactionName.text = transaction.name

        // Determine if the transaction is recurring or one-time and set the corresponding text
        val isRecurring = if (transaction.isrecurring) {
            "Recurring payment" // Label for recurring transactions
        } else {
            "One-time payment" // Label for one-time transactions
        }
        holder.isRecurring.text = isRecurring // Set the text for recurring status

        // Format the amount and set the text in the amount TextView
        holder.amount.text = "${AppConstants.formatAmount(transaction.amount)} ZAR"

        // Determine the color for the amount based on the transaction type
        val amountColor = if (transaction.type == "INCOME") {
            ContextCompat.getColor(holder.itemView.context, R.color.green) // Color for income transactions
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.red) // Color for expense transactions
        }
        holder.amount.setTextColor(amountColor) // Set the color for the amount TextView

        // Set the icon image if it exists
        if (iconImageResId != null) {
            holder.iconImage.setImageResource(iconImageResId) // Set the icon resource for the transaction
        }

        // Set the background color and shape for the category frame
        if (categoryBackgroundColor != null) {
            holder.categoryFrame.setBackgroundColor(originalColor) // Set the background color
            val cornerRadius = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.corner_radius)
            val shapeDrawable = GradientDrawable() // Create a shape drawable for the background
            shapeDrawable.setColor(originalColor) // Set the background color of the shape
            shapeDrawable.cornerRadius = cornerRadius.toFloat() // Set the corner radius
            holder.categoryFrame.background = shapeDrawable // Set the shape drawable as the background
        }

        // Create a shape drawable for the category frame with a border
        val cornerRadius = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.circle_corner_radius)
        val shapeDrawable = GradientDrawable() // Create a new shape drawable
        shapeDrawable.setColor(originalColor) // Set the background color
        shapeDrawable.cornerRadius = cornerRadius.toFloat() // Set the corner radius
        shapeDrawable.setStroke(2, ContextCompat.getColor(holder.itemView.context, R.color.light_grey)) // Set the stroke color
        holder.categoryFrame.background = shapeDrawable // Set the drawable as the background of the category frame
    }

    // ViewHolder class to hold the views for each transaction item
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ImageView for displaying the transaction icon
        val iconImage: ImageView = itemView.findViewById(R.id.card_img)
        // TextView for displaying the transaction name
        val transactionName: TextView = itemView.findViewById(R.id.name)
        // LinearLayout for displaying the category frame
        val categoryFrame: LinearLayout = itemView.findViewById(R.id.cat_img_container)
        // TextView for indicating if the transaction is recurring
        val isRecurring: TextView = itemView.findViewById(R.id.isRecurring)
        // TextView for displaying the transaction amount
        val amount: TextView = itemView.findViewById(R.id.amount)
    }

}
