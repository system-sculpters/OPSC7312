package com.opsc.opsc7312.view.adapter

import android.graphics.drawable.GradientDrawable
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

class SelectCategoryAdapter (private val onItemClick: (Category) -> Unit) :
    RecyclerView.Adapter<SelectCategoryAdapter.IconViewHolder>() {
    // This class adapted from geeksforgeeks
    // https://www.geeksforgeeks.org/android-recyclerview/
    // BaibhavOjha
    // https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user

    private var categories = mutableListOf<Category>()

    fun updateCategories(data: List<Category>) {
        categories.clear()
        categories.addAll(data)
        notifyDataSetChanged()
    }


    // Adapter for displaying icons in a RecyclerView

    // Variables to keep track of selected item position and icon private var selectedItemPosition = RecyclerView.NO_POSITION
    private var selectedItemPosition = RecyclerView.NO_POSITION
    private var selectedCategory: Category? = null

    // ViewHolder for icons
    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val iconNameText: TextView = itemView.findViewById(R.id.icon_name)
        val background: ConstraintLayout = itemView.findViewById(R.id.background)

        // Initialize click listener for item
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Update selected item position and trigger onItemClick callback
                    selectedItemPosition = position
                    onItemClick(categories[position])
                }
            }
        }
    }

    // Function to get the selected item's icon
    fun getSelectedItem(): Category? {
        return if (selectedItemPosition != RecyclerView.NO_POSITION) {
            categories[selectedItemPosition]
        } else {
            null
        }
    }

    // Function to set the selected icon
    fun setSelectedCategory(category: Category) {
        // Keep track of previous selected position
        val previousSelectedPosition = selectedItemPosition
        selectedCategory = category
        // Notify adapter of item change at previous position
        if (previousSelectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelectedPosition)
        }
        // Update selected position based on the new icon
        selectedCategory?.let { categoryValue ->
            val newSelectedPosition = categories.indexOf(categoryValue)
            if (newSelectedPosition != -1) {
                selectedItemPosition = newSelectedPosition
                // Notify adapter of item change at new selected position
                notifyItemChanged(selectedItemPosition)
            }
        }
    }

    // Create ViewHolder for icons
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.select_category_item_layout, parent, false)
        return IconViewHolder(itemView)
    }

    // Bind icon to ViewHolder
    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val currentItem = categories[position]

        val colorResId = AppConstants.COLOR_DICTIONARY[currentItem.color]
        val icon = AppConstants.ICONS[currentItem.icon]

        if (colorResId != null) {
            val originalColor = ContextCompat.getColor(holder.itemView.context,
                colorResId
            )
            holder.background.setBackgroundColor(originalColor)

            val cornerRadius = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.corner_radius_main)
            val shapeDrawable = GradientDrawable()
            shapeDrawable.setColor(originalColor)
            shapeDrawable.cornerRadius = cornerRadius.toFloat()
            holder.background.background = shapeDrawable
        }



        if (icon != null) {
            holder.iconImageView.setImageResource(icon)
        }
        holder.iconNameText.text = currentItem.name
    }

    // Get item count
    override fun getItemCount(): Int {
        return categories.size
    }
}