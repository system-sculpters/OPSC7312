package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.opsc.opsc7312.AppConstants
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentCategoriesBinding
import com.opsc.opsc7312.model.api.retrofitclients.CategoryRetrofitClient
import com.opsc.opsc7312.model.data.Category
import com.opsc.opsc7312.view.adapter.CategoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryList: ArrayList<Category>
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        categoryList("id1")

        setUpRecyclerView()


        return binding.root
    }

    private fun setUpRecyclerView(){
        binding.categoryRecycleView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.categoryRecycleView.setHasFixedSize(true)
        binding.categoryRecycleView.adapter = categoryAdapter
    }
    

    private fun categoryList(id: String){
        val call = CategoryRetrofitClient.apiService.getCategories(id)

        // Log the URL
        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")
        CategoryRetrofitClient.apiService.getCategories(id).enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    val categories = response.body()
                    categories?.let {
                        categoryAdapter.updateCategories(it)
                        Log.d("MainActivity", "Categories: $it")
                    }
                } else {
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
            }
        })
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