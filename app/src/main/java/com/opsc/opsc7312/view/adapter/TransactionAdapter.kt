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

class TransactionAdapter(private val onItemClick: (Transaction) -> Unit) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    // This class adapted from geeksforgeeks
    // https://www.geeksforgeeks.org/android-recyclerview/
    // BaibhavOjha
    // https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user

    private val transactions = mutableListOf<Transaction>()

    // Function to update the adapter's data
    fun updateTransactions(data: List<Transaction>) {
        transactions.clear()
        transactions.addAll(data)
        notifyDataSetChanged()
    }


    // Create view holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item_layout, parent, false)
        return ViewHolder(itemView)
    }

    // Get item count
    override fun getItemCount(): Int {
        return transactions.size
    }

    // Bind data to view holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]


        // Set click listener for item
        holder.itemView.setOnClickListener {
            onItemClick(transaction)
        }

        // Set icon, background color, task name, and hours text
        val iconImageResId = AppConstants.ICONS[transaction.category.icon]
        val categoryBackgroundColor = AppConstants.COLOR_DICTIONARY[transaction.category.color]

        val originalColor = if (categoryBackgroundColor != null) {
            ContextCompat.getColor(holder.itemView.context, categoryBackgroundColor)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.primary) // Set a default color
        }

        holder.transactionName.text = transaction.name

        val isRecurring = if(transaction.isRecurring){
            "Recurring payment"
        } else {
            "One-time payment"
        }

        holder.isRecurring.text = isRecurring

        holder.amount.text = "${transaction.amount} ZAR"

        val amountColor = if (transaction.type == "Income") {
            ContextCompat.getColor(holder.itemView.context, R.color.green)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.red) // Set a default color
        }

        holder.amount.setTextColor(amountColor)


        if (iconImageResId != null) {
            holder.iconImage.setImageResource(iconImageResId)
        }

        if (categoryBackgroundColor != null) {

            holder.categoryFrame.setBackgroundColor(originalColor)
            val cornerRadius = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.corner_radius)
            val shapeDrawable = GradientDrawable()
            shapeDrawable.setColor(originalColor)
            shapeDrawable.cornerRadius = cornerRadius.toFloat()
            holder.categoryFrame.background = shapeDrawable
        }

        val cornerRadius = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.circle_corner_radius)
        val shapeDrawable = GradientDrawable()
        shapeDrawable.setColor(originalColor)
        shapeDrawable.cornerRadius = cornerRadius.toFloat()
        shapeDrawable.setStroke(2, ContextCompat.getColor(holder.itemView.context, R.color.light_grey))
        holder.categoryFrame.background = shapeDrawable


    }

    // View holder class
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImage: ImageView = itemView.findViewById(R.id.card_img)
        val transactionName: TextView = itemView.findViewById(R.id.name)
        val categoryFrame: LinearLayout = itemView.findViewById(R.id.cat_img_container)
        val isRecurring: TextView = itemView.findViewById(R.id.isRecurring)
        val amount: TextView = itemView.findViewById(R.id.amount)
    }

}
