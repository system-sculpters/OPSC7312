package com.opsc.opsc7312.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R

// This class adapted from geeksforgeeks
// https://www.geeksforgeeks.org/android-recyclerview/
// BaibhavOjha
// https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user

class IconAdapter (private val iconList: ArrayList<Int>, private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<IconAdapter.IconViewHolder>() {


    // Adapter for displaying icons in a RecyclerView

    // Variables to keep track of selected item position and icon private var selectedItemPosition = RecyclerView.NO_POSITION
    private var selectedItemPosition = RecyclerView.NO_POSITION
    private var selectedIcon: Int? = null

    // ViewHolder for icons
    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val iconNameText: TextView = itemView.findViewById(R.id.icon_name)

        // Initialize click listener for item
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Update selected item position and trigger onItemClick callback
                    selectedItemPosition = position
                    onItemClick(iconList[position])
                }
            }
        }
    }

    // Function to get the selected item's icon
    fun getSelectedItem(): Int? {
        return if (selectedItemPosition != RecyclerView.NO_POSITION) {
            iconList[selectedItemPosition]
        } else {
            null
        }
    }

    // Function to set the selected icon
    fun setSelectedIcon(icon: Int?) {
        // Keep track of previous selected position
        val previousSelectedPosition = selectedItemPosition
        selectedIcon = icon
        // Notify adapter of item change at previous position
        if (previousSelectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelectedPosition)
        }
        // Update selected position based on the new icon
        selectedIcon?.let { iconValue ->
            Log.d("icon", "this is the icon: $iconValue")
            Log.d("icon dataList", "this is the datalist $iconList")
            val newSelectedPosition = iconList.indexOf(iconValue)
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
            .inflate(R.layout.icon_item_layout, parent, false)
        return IconViewHolder(itemView)
    }

    // Bind icon to ViewHolder
    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val iconResId = iconList[position]
        val iconName = AppConstants.ICONS.entries.map { it.key }
        holder.iconImageView.setImageResource(iconResId)
        holder.iconNameText.text = iconName[position]
    }

    // Get item count
    override fun getItemCount(): Int {
        return iconList.size
    }
}