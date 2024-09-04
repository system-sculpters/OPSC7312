package com.opsc.opsc7312.view.adapter

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

class ColorAdapter (private val dataList:ArrayList<Color>,
                    private val onItemClick: (Color) -> Unit ): RecyclerView.Adapter<ColorAdapter.ViewHolderClass>() {
    // This class adapted from geeksforgeeks
    // https://www.geeksforgeeks.org/android-recyclerview/
    // BaibhavOjha
    // https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user

    private var selectedItemPosition = RecyclerView.NO_POSITION
    private var selectedCategory: Color? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClass {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.color_item_layout, parent, false)
        return ViewHolderClass(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ViewHolderClass, position: Int) {
        val currentItem = dataList[position]
        val colorResId = AppConstants.COLOR_DICTIONARY[currentItem.name]
        val iconImageResId = AppConstants.COLOR_IMAGE_DICTIONARY[currentItem.name]

        val originalColor = if (colorResId != null) {
            ContextCompat.getColor(holder.itemView.context, colorResId)
        } else {
            ContextCompat.getColor(holder.itemView.context, R.color.primary) // Set a default color
        }


        val test = ContextCompat.getColor(holder.itemView.context, R.color.dark_grey)

        if (position == selectedItemPosition) {
            holder.cardView.setCardBackgroundColor(test)
            if (iconImageResId != null) {
                holder.checkedImageView.setImageResource(iconImageResId)
            }

        } else {
            holder.cardView.setCardBackgroundColor(originalColor)
            holder.checkedImageView.setImageResource(R.drawable.transparent)
        }

    }

    // Sets the selected category and updates the UI
    fun setSelectedCategory(colors: Color?) {
        val previousSelectedPosition = selectedItemPosition
        selectedCategory = colors
        if (previousSelectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousSelectedPosition)
        }
        selectedCategory?.let { color ->
            val newSelectedPosition = dataList.indexOf(color)
            if (newSelectedPosition != -1) {
                selectedItemPosition = newSelectedPosition
                notifyItemChanged(selectedItemPosition)
            }
        }
    }

    // Gets the selected item
    fun getSelectedItem(): Color? {
        return if (selectedItemPosition != RecyclerView.NO_POSITION) {
            dataList[selectedItemPosition]
        } else {
            null
        }
    }

    // ViewHolder class for holding the views
    inner class ViewHolderClass(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        //val rvTitle: TextView = itemView.findViewById(R.id.title)
        val cardView: CardView = itemView.findViewById(R.id.cat_card_view)
        val checkedImageView: ImageView = itemView.findViewById(R.id.icon_image_view)

        init {
            cardView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                // Update selected item position
                val previousSelectedItem = selectedItemPosition
                selectedItemPosition = position

                // Refresh UI for previous and current selected items
                notifyItemChanged(previousSelectedItem)
                notifyItemChanged(selectedItemPosition)
                // Trigger onItemClick event

                // Trigger onItemClick event
                onItemClick(dataList[position])
            }
        }
    }

}