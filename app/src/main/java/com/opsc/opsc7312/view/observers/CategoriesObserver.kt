package com.opsc.opsc7312.view.observers

import android.util.Log
import androidx.lifecycle.Observer
import com.opsc.opsc7312.model.data.Category
import com.opsc.opsc7312.view.adapter.CategoryAdapter

class CategoriesObserver(private val adapter: CategoryAdapter?
): Observer<List<Category>> {
    // This class was adapted from stackoverflow
    // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
    // Kevin Robatel
    // https://stackoverflow.com/users/244702/kevin-robatel

    // Method called when the observed data changes
    override fun onChanged(value: List<Category>) {
        // Update the data in the CategoryListAdapter
        adapter?.updateCategories(value)

        // Update the categories in the associated ActivityFragment

        Log.d("Category", "Category retrieved: ${value.size}\n $value")
    }
}