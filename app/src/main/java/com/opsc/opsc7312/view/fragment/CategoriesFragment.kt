package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentCategoriesBinding
import com.opsc.opsc7312.model.data.Category
import com.opsc.opsc7312.view.adapter.CategoryAdapter


class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryList: ArrayList<Category>
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        categoryList = arrayListOf<Category>()

        categoryAdapter = CategoryAdapter{
                category ->

            if (category.isCreateButton) {
                // Handle the create button click (if necessary)
                redirectToCreate()
            } else {
                redirectToDetails(category)
            }
        }

        categoryList()

        setUpRecyclerView()


        return binding.root
    }

    private fun setUpRecyclerView(){
        binding.categoryRecycleView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.categoryRecycleView.setHasFixedSize(true)
        binding.categoryRecycleView.adapter = categoryAdapter
    }

    private fun categoryList(){

        val cat0 = Category(isCreateButton = true)

        val cat1 = Category(id = "id", name = "blue", color = "Blue", icon = "yellow", transactiontype = AppConstants.TRANSACTION_TYPE.INCOME,
        userid = "userid")

        val cat2 = Category(id = "id", name = "red", color = "Red", icon = "green", transactiontype = AppConstants.TRANSACTION_TYPE.INCOME,
            userid = "userid")

        val cat3 = Category(id = "id", name = "yellow", color = "Yellow", icon = "red", transactiontype = AppConstants.TRANSACTION_TYPE.INCOME,
            userid = "userid")

        val cat4 = Category(id = "id", name = "green", color = "Green", icon = "blue", transactiontype = AppConstants.TRANSACTION_TYPE.INCOME,
            userid = "userid")

        categoryList.add(cat0)
        categoryList.add(cat1)
        categoryList.add(cat2)
        categoryList.add(cat3)
        categoryList.add(cat4)

        categoryAdapter.updateCategories(categoryList)
    }

    private fun redirectToCreate(){
        // Create a new instance of CategoryDetailsFragment and pass category data
        val createCategoryFragment = PlaceholderFragment()
        val bundle = Bundle()
        bundle.putString("screen", "redirectToCreate")
        createCategoryFragment.arguments = bundle
        // Navigate to CategoryDetailsFragment
        changeCurrentFragment(createCategoryFragment)
    }

    private fun redirectToDetails(category: Category){
        // Create a new instance of CategoryDetailsFragment and pass category data
        val categoryDetailsFragment = PlaceholderFragment()
        val bundle = Bundle()
        bundle.putParcelable("category", category)
        bundle.putString("screen", "redirectToDetails")
        categoryDetailsFragment.arguments = bundle

        // Navigate to CategoryDetailsFragment
        changeCurrentFragment(categoryDetailsFragment)
    }

    private fun changeCurrentFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}