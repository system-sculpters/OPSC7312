package com.opsc.opsc7312.view.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.model.data.model.Category


class CategoryAdapter(private val onItemClick: (Category) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // This class adapted from geeksforgeeks
    // https://www.geeksforgeeks.org/android-recyclerview/
    // BaibhavOjha
    // https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user

    private var categories = mutableListOf<Category>() // List to hold category data

    // Updates the adapter's category list with new data and refreshes the view
    fun updateCategories(data: List<Category>) {
        categories.clear() // Clear the existing categories
        categories.addAll(data) // Add the new categories to the list
        notifyDataSetChanged() // Notify the adapter to refresh the UI
    }

    companion object {
        private const val VIEW_TYPE_CREATE = 0 // Identifier for the create button view type
        private const val VIEW_TYPE_CATEGORY = 1 // Identifier for the regular category view type
    }

    // Inflates the appropriate layout based on the view type and creates a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CREATE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.create_category_item_layout, parent, false)
            CreateViewHolder(view) // Create and return a ViewHolder for the create button
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item_layout, parent, false)
            CategoryViewHolder(view) // Create and return a ViewHolder for the category item
        }
    }

    // Determines the view type of the item at the specified position
    override fun getItemViewType(position: Int): Int {
        return if (categories[position].isCreateButton) VIEW_TYPE_CREATE else VIEW_TYPE_CATEGORY // Check if the item is a create button
    }

    // Returns the total number of items in the adapter
    override fun getItemCount(): Int {
        return categories.size // Return the number of categories in the list
    }

    // Binds data to the ViewHolder for the item at the specified position
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = categories[position] // Get the current category item

        if (holder.itemViewType == VIEW_TYPE_CREATE) {
            // Handle the case where the ViewHolder is for the create button
            val createHolder = holder as CreateViewHolder
            createHolder.icon.setImageResource(R.drawable.baseline_add_category) // Set the "+" icon
            createHolder.itemView.setOnClickListener {
                onItemClick(currentItem) // Handle click event for the create button
            }
        } else {
            // Handle the case where the ViewHolder is for a regular category item
            val categoryHolder = holder as CategoryViewHolder
            categoryHolder.itemView.setOnClickListener {
                onItemClick(currentItem) // Handle click event for the category item
            }

            // Set background color and icon based on the category attributes
            val colorResId = AppConstants.COLOR_DICTIONARY[currentItem.color] // Get the color resource ID
            val icon = AppConstants.ICONS[currentItem.icon] // Get the icon resource ID

            if (colorResId != null) {
                val originalColor = ContextCompat.getColor(holder.itemView.context, colorResId) // Get the color
                val cornerRadius = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.corner_radius) // Get corner radius

                // Create a GradientDrawable for the category background
                val shapeDrawable = GradientDrawable()
                shapeDrawable.setColor(originalColor)
                shapeDrawable.cornerRadius = cornerRadius.toFloat()

                categoryHolder.catItem.background = shapeDrawable // Apply the background to the item
            }

            // Set the icon and name for the category item
            if (icon != null) {
                categoryHolder.iconImage.setImageResource(icon) // Set the icon image
            }
            categoryHolder.catName.text = currentItem.name // Set the category name
        }
    }

    // ViewHolder for regular category items
    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImage: ImageView = itemView.findViewById(R.id.iconImageView) // ImageView for the category icon
        val catItem: LinearLayout = itemView.findViewById(R.id.parentLayout) // Parent layout for category item
        val catName: TextView = itemView.findViewById(R.id.cat_label) // TextView for displaying the category name
    }

    // ViewHolder for the create category button
    inner class CreateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iv_category_icon) // ImageView for the create button icon
    }
}
