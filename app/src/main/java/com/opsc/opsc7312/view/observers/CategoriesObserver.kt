package com.opsc.opsc7312.view.observers

import android.util.Log
import androidx.lifecycle.Observer
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.view.adapter.CategoryAdapter
import com.opsc.opsc7312.view.adapter.SelectCategoryAdapter

// This class was adapted from stackoverflow
// https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
// Kevin Robatel
// https://stackoverflow.com/users/244702/kevin-robatel

// This class observes changes to a list of Categories and updates the UI accordingly.
class CategoriesObserver(private val adapter: CategoryAdapter?,
    private val selectionAdapter: SelectCategoryAdapter?
): Observer<List<Category>> {


    // Method called when the observed data changes
    override fun onChanged(value: List<Category>) {
        // initialize first element as the create button
        val cat0 = Category(isCreateButton = true)

        // Create a new list with cat0 as the first element
        val updatedCategories = listOf(cat0) + value

        // Update the data in the CategoryAdapter
        adapter?.updateCategories(updatedCategories)

        // Update the data in the SelectCategoryAdapter
        selectionAdapter?.updateCategories(value)

        // Update the categories in the associated ActivityFragment

        Log.d("Category", "Category retrieved: ${value.size}\n $value")
    }
}