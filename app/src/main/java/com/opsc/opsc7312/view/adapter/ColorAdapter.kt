package com.opsc.opsc7312.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.model.data.model.Color

// This class adapted from geeksforgeeks
// https://www.geeksforgeeks.org/android-recyclerview/
// BaibhavOjha
// https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user


class ColorAdapter(private val dataList: ArrayList<Color>,
                   private val onItemClick: (Color) -> Unit) : RecyclerView.Adapter<ColorAdapter.ViewHolderClass>() {

    private var selectedItemPosition = RecyclerView.NO_POSITION // Tracks the position of the currently selected item
    private var selectedColor: Color? = null // Stores the currently selected color

    // Creates a new ViewHolder for the color item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        // Inflate the color item layout and create a ViewHolder
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.color_item_layout, parent, false)
        return ViewHolderClass(itemView) // Return the newly created ViewHolder
    }

    // Returns the total number of items in the data list
    override fun getItemCount(): Int {
        return dataList.size // Return the size of the data list
    }

    // Binds the data from the list to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position] // Get the current color item
        val colorResId = AppConstants.COLOR_DICTIONARY[currentItem.name] // Retrieve the color resource ID
        val iconImageResId = AppConstants.COLOR_IMAGE_DICTIONARY[currentItem.name] // Retrieve the icon resource ID

        // Get the original color based on the resource ID or set a default color
        val originalColor = if (colorResId != null) {
            ContextCompat.getColor(holder.itemView.context, colorResId)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.primary) // Default color if not found
        }

        // Color for the selected item (dark grey)
        val test = ContextCompat.getColor(holder.itemView.context, R.color.dark_grey)

        // Update the UI based on whether the item is selected
        if (position == selectedItemPosition) {
            holder.cardView.setCardBackgroundColor(test) // Set background color for the selected item
            if (iconImageResId != null) {
                holder.checkedImageView.setImageResource(iconImageResId) // Set the icon for the selected item
            }
        } else {
            holder.cardView.setCardBackgroundColor(originalColor) // Set the background color for unselected items
            holder.checkedImageView.setImageResource(R.drawable.transparent) // Hide the icon for unselected items
        }
    }

    // Sets the selected color and updates the UI accordingly
    fun setSelectedColor(colors: Color?) {
        val previousSelectedPosition = selectedItemPosition // Store the previous selected position
        selectedColor = colors // Update the selected color
        if (previousSelectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelectedPosition) // Refresh the UI for the previous selected item
        }
        selectedColor?.let { color ->
            Log.d("color", "this is the color: $color") // Log the selected color for debugging
            Log.d("color dataList", "this is the datalist $dataList") // Log the data list for debugging
            val newSelectedPosition = dataList.indexOf(color) // Find the new selected position
            if (newSelectedPosition != -1) {
                selectedItemPosition = newSelectedPosition // Update the selected item position
                notifyItemChanged(selectedItemPosition) // Refresh the UI for the new selected item
            }
        }
    }

    // Retrieves the currently selected color item
    fun getSelectedItem(): Color? {
        return if (selectedItemPosition != RecyclerView.NO_POSITION) {
            dataList[selectedItemPosition] // Return the selected color item
        } else {
            null // Return null if no item is selected
        }
    }

    // ViewHolder class for holding the views of each color item
    inner class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val cardView: CardView = itemView.findViewById(R.id.cat_card_view) // CardView for displaying the color
        val checkedImageView: ImageView = itemView.findViewById(R.id.icon_image_view) // ImageView for displaying the icon

        init {
            cardView.setOnClickListener(this) // Set the click listener for the card view
        }

        // Handle click events for the color items
        override fun onClick(v: View?) {
            val position = adapterPosition // Get the adapter position of the clicked item
            if (position != RecyclerView.NO_POSITION) {
                // Update selected item position and refresh the UI
                val previousSelectedItem = selectedItemPosition
                selectedItemPosition = position

                // Notify changes for previously and currently selected items
                notifyItemChanged(previousSelectedItem)
                notifyItemChanged(selectedItemPosition)

                // Trigger the onItemClick event to notify listeners
                onItemClick(dataList[position]) // Invoke the callback with the clicked color
            }
        }
    }
}
