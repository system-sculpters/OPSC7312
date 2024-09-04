package com.opsc.opsc7312.view.observers

import android.util.Log
import androidx.lifecycle.Observer
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.view.adapter.CategoryAdapter
import com.opsc.opsc7312.view.adapter.SelectCategoryAdapter

class CategoriesObserver(private val adapter: CategoryAdapter?,
    private val selectionAdapter: SelectCategoryAdapter?
): Observer<List<Category>> {
    // This class was adapted from stackoverflow
    // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
    // Kevin Robatel
    // https://stackoverflow.com/users/244702/kevin-robatel

    // Method called when the observed data changes
    override fun onChanged(value: List<Category>) {
        // Update the data in the CategoryListAdapter
        val cat0 = Category(isCreateButton = true)

        // Create a new list with cat0 as the first element
        val updatedCategories = listOf(cat0) + value

        adapter?.updateCategories(updatedCategories)

        selectionAdapter?.updateCategories(value)

        // Update the categories in the associated ActivityFragment

        Log.d("Category", "Category retrieved: ${value.size}\n $value")
    }
}