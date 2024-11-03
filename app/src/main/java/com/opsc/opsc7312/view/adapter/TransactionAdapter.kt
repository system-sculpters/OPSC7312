package com.opsc.opsc7312.view.adapter

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.util.TypedValue
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
        val transaction = transactions[position]

        holder.itemView.setOnClickListener {
            onItemClick(transaction)
        }

        // Set the category-related visuals based on whether it's uncategorized or not
        val isUncategorized = transaction.categoryId == AppConstants.UNCATEGORIZED

        Log.d("isUncategorized", "isUncategorized: $isUncategorized")

        // Default category properties
        var iconImageResId = R.drawable.baseline_close_24
        var categoryBackgroundColor = ContextCompat.getColor(holder.itemView.context, R.color.primary)

        // If it's uncategorized, use text color as the background
        if (isUncategorized) {
            categoryBackgroundColor = getTextColor(holder)  // Use textColor as background
            iconImageResId = R.drawable.baseline_close_24
            holder.iconImage.setImageResource(iconImageResId)
            holder.iconImage.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.red))
            setCategoryBackground(holder.categoryFrame, categoryBackgroundColor)

        } else {

            holder.iconImage.clearColorFilter()
            // If categorized, use the category's color
            categoryBackgroundColor = AppConstants.COLOR_DICTIONARY[transaction.category.color]!!
            iconImageResId = AppConstants.ICONS[transaction.category.icon] ?: R.drawable.baseline_close_24
            // Set the icon without tinting
            holder.iconImage.setImageResource(iconImageResId)
            holder.categoryFrame.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.primary))
            categoryBackground(holder, categoryBackgroundColor)
        }

        // Set transaction details
        holder.transactionName.text = transaction.name
        holder.isRecurring.text = if (transaction.isrecurring) holder.itemView.context.getString(R.string.recurring_payment) else holder.itemView.context.getString(R.string.one_time_payment)
        holder.amount.text = "${AppConstants.formatAmount(transaction.amount)} ZAR"

        // Set color of amount based on transaction type (Income or Expense)
        val amountColor = if (transaction.type == "INCOME") {
            ContextCompat.getColor(holder.itemView.context, R.color.green)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.red)
        }
        holder.amount.setTextColor(amountColor)
    }
    // Set the background color and shape for the category frame
    private fun setCategoryBackground(frame: LinearLayout, backgroundColor: Int) {
        val cornerRadius = frame.context.resources.getDimensionPixelSize(R.dimen.circle_corner_radius)
        val shapeDrawable = GradientDrawable().apply {
            setColor(backgroundColor)
            this.cornerRadius = cornerRadius.toFloat()
            setStroke(2, ContextCompat.getColor(frame.context, R.color.light_grey))
        }
        frame.background = shapeDrawable
    }

    private fun categoryBackground(holder: ViewHolder, originalColor: Int){
        // Create a shape drawable for the category frame with a border
        val cornerRadius = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.circle_corner_radius)
        val shapeDrawable = GradientDrawable() // Create a new shape drawable
        shapeDrawable.setColor(ContextCompat.getColor(holder.itemView.context, originalColor)) // Set the background color
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

    // Fetches the theme-based text color for uncategorized transactions
    private fun getTextColor(holder: ViewHolder): Int {
        val typedValue = TypedValue()
        val theme = holder.itemView.context.theme
        theme.resolveAttribute(R.attr.colorItemLayoutBg, typedValue, true)
        return typedValue.data
    }
}
