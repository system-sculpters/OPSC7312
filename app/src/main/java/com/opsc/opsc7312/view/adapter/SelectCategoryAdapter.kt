package com.opsc.opsc7312.view.adapter

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.model.data.model.Category

// This class adapted from geeksforgeeks
// https://www.geeksforgeeks.org/android-recyclerview/
// BaibhavOjha
// https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user

class SelectCategoryAdapter(private val onItemClick: (Category) -> Unit) :
    RecyclerView.Adapter<SelectCategoryAdapter.IconViewHolder>() {

    // Mutable list to hold the categories
    private var categories = mutableListOf<Category>()

    // Updates the list of categories and notifies the RecyclerView to refresh
    fun updateCategories(data: List<Category>) {
        categories.clear() // Clear the existing categories
        categories.addAll(data) // Add new categories to the list
        notifyDataSetChanged() // Notify the adapter to refresh the view
    }

    // Adapter for displaying icons in a RecyclerView

    // Variable to keep track of the selected item position
    private var selectedItemPosition = RecyclerView.NO_POSITION
    // Variable to hold the currently selected category
    private var selectedCategory: Category? = null

    // ViewHolder class for holding the views for each icon item
    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ImageView for displaying the icon
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        // TextView for displaying the icon name
        val iconNameText: TextView = itemView.findViewById(R.id.icon_name)
        // ConstraintLayout for setting the background of the icon item
        val background: ConstraintLayout = itemView.findViewById(R.id.background)

        // Initialize click listener for the item view
        init {
            itemView.setOnClickListener {
                val position = adapterPosition // Get the position of the clicked item
                if (position != RecyclerView.NO_POSITION) {
                    // Update the selected item position and trigger the onItemClick callback
                    selectedItemPosition = position
                    onItemClick(categories[position]) // Invoke the callback with the clicked category
                }
            }
        }
    }

    // Function to get the currently selected item's category
    fun getSelectedItem(): Category? {
        return if (selectedItemPosition != RecyclerView.NO_POSITION) {
            categories[selectedItemPosition] // Return the selected category
        } else {
            null // Return null if no item is selected
        }
    }

    // Function to set the selected category
    fun setSelectedCategory(category: Category) {
        // Keep track of the previous selected position
        val previousSelectedPosition = selectedItemPosition
        selectedCategory = category // Set the new selected category

        // Notify adapter of item change at the previous selected position
        if (previousSelectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelectedPosition)
        }

        // Update selected position based on the new category
        selectedCategory?.let { categoryValue ->
            Log.d("category", "this is the category: $categoryValue")
            Log.d("category dataList", "this is the dataList $categories")
            val newSelectedPosition = categories.indexOf(categoryValue) // Get the index of the new selected category
            if (newSelectedPosition != -1) {
                selectedItemPosition = newSelectedPosition // Update the selected item position
                // Notify adapter of item change at the new selected position
                notifyItemChanged(selectedItemPosition)
            }
        }
    }

    // Create a ViewHolder for icons
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        // Inflate the layout for the icon item and create a ViewHolder instance
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_category_item_layout, parent, false)
        return IconViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Bind the category data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val currentItem = categories[position] // Get the current category item

        // Get the color resource ID and icon resource ID for the current category
        val colorResId = AppConstants.COLOR_DICTIONARY[currentItem.color]
        val icon = AppConstants.ICONS[currentItem.icon]

        // Set the background color for the item based on the category's color
        if (colorResId != null) {
            val originalColor = ContextCompat.getColor(holder.itemView.context, colorResId)
            holder.background.setBackgroundColor(originalColor) // Set background color

            // Create a rounded shape drawable for the background
            val cornerRadius = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.corner_radius_main)
            val shapeDrawable = GradientDrawable()
            shapeDrawable.setColor(originalColor)
            shapeDrawable.cornerRadius = cornerRadius.toFloat() // Set the corner radius
            holder.background.background = shapeDrawable // Set the shape drawable as the background
        }

        // Set the icon image if it exists
        if (icon != null) {
            holder.iconImageView.setImageResource(icon) // Set the icon resource
        }
        // Set the name of the category in the TextView
        holder.iconNameText.text = currentItem.name
    }

    // Returns the total number of categories in the list
    override fun getItemCount(): Int {
        return categories.size // Return the size of the categories list
    }
}
