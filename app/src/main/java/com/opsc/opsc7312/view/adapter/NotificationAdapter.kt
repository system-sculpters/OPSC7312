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
import com.opsc.opsc7312.model.data.model.Category

// This class adapted from geeksforgeeks
// https://www.geeksforgeeks.org/android-recyclerview/
// BaibhavOjha
// https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user

class NotificationAdapter (private val onItemClick: (Category) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}