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

class CategoryAdapter (private val onItemClick: (Category) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

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

    companion object {
        private const val VIEW_TYPE_CREATE = 0
        private const val VIEW_TYPE_CATEGORY = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CREATE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.create_category_item_layout, parent, false)
            CreateViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item_layout, parent, false)
            CategoryViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (categories[position].isCreateButton) VIEW_TYPE_CREATE else VIEW_TYPE_CATEGORY
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = categories[position]

        if (holder.itemViewType == VIEW_TYPE_CREATE) {
            val createHolder = holder as CreateViewHolder
            createHolder.icon.setImageResource(R.drawable.baseline_add_category) // Replace with your "+" icon
            createHolder.itemView.setOnClickListener {
                onItemClick(currentItem)
            }
        } else {
            val categoryHolder = holder as CategoryViewHolder

            categoryHolder.itemView.setOnClickListener {
                onItemClick(currentItem)
            }

            val colorResId = AppConstants.COLOR_DICTIONARY[currentItem.color]
            val icon = AppConstants.ICONS[currentItem.icon]

            if (colorResId != null) {
                val originalColor = ContextCompat.getColor(holder.itemView.context,
                    colorResId
                )
                categoryHolder.catItem.setBackgroundColor(originalColor)

//                categoryHolder.cardView.setCardBackgroundColor(
//                    ContextCompat.getColor(holder.itemView.context,
//                        colorResId
//                    ))
                val cornerRadius = holder.itemView.context.resources.getDimensionPixelSize(R.dimen.corner_radius)
                val shapeDrawable = GradientDrawable()
                shapeDrawable.setColor(originalColor)
                shapeDrawable.cornerRadius = cornerRadius.toFloat()
                categoryHolder.catItem.background = shapeDrawable
            }



            if (icon != null) {
                categoryHolder.iconImage.setImageResource(icon)
            }
            categoryHolder.catName.text = currentItem.name


        }


    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iconImage: ImageView = itemView.findViewById(R.id.iconImageView)
        //val cardView: CardView = itemView.findViewById(R.id.category_list_card_view)
        val catItem: LinearLayout = itemView.findViewById(R.id.parentLayout)
        val catName: TextView = itemView.findViewById(R.id.cat_label)
    }

    inner class CreateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iv_category_icon)
    }
}