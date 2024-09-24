package com.opsc.opsc7312.view.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.databinding.FragmentCategoriesBinding
import com.opsc.opsc7312.model.api.controllers.CategoryController
import com.opsc.opsc7312.model.data.model.Category
import com.opsc.opsc7312.model.data.offline.preferences.TokenManager
import com.opsc.opsc7312.model.data.offline.preferences.UserManager
import com.opsc.opsc7312.view.adapter.CategoryAdapter
import com.opsc.opsc7312.view.custom.TimeOutDialog
import com.opsc.opsc7312.view.observers.CategoriesObserver


class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryList: ArrayList<Category>
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var categoryViewModel: CategoryController

    private lateinit var userManager: UserManager

    private lateinit var tokenManager: TokenManager

    private lateinit var timeOutDialog: TimeOutDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        categoryList = arrayListOf<Category>()

        userManager = UserManager.getInstance(requireContext())

        tokenManager = TokenManager.getInstance(requireContext())

        categoryViewModel = ViewModelProvider(this).get(CategoryController::class.java)

        timeOutDialog = TimeOutDialog()

        categoryAdapter = CategoryAdapter{
                category ->

            if (category.isCreateButton) {
                // Handle the create button click (if necessary)
                redirectToCreate()
            } else {
                redirectToDetails(category)
            }
        }

        setUpRecyclerView()

        setUpCategoriesDetails()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Access the MainActivity and set the toolbar title
        (activity as? MainActivity)?.setToolbarTitle("Categories")
    }

    private fun setUpRecyclerView(){
        binding.categoryRecycleView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.categoryRecycleView.setHasFixedSize(true)
        binding.categoryRecycleView.adapter = categoryAdapter
    }

    private fun setUpCategoriesDetails(){
        val user = userManager.getUser()

        val token = tokenManager.getToken()


        if(token != null){
            observeViewModel(token, user.id)
        } else {

        }
    }
    

    private fun observeViewModel(token: String, id: String) {
        val progressDialog = timeOutDialog.showProgressDialog(requireContext())

        // Observe LiveData
        categoryViewModel.status.observe(viewLifecycleOwner)  { status ->
            // Handle status changes (success or failure)
            if (status) {
                progressDialog.dismiss()


            } else {
               progressDialog.dismiss()

            }
        }

        categoryViewModel.message.observe(viewLifecycleOwner) { message ->
            // Show message to the user, if needed
            if(message == "timeout" || message.contains("Unable to resolve host")){
                timeOutDialog.showTimeoutDialog(requireContext() ){
                    progressDialog.dismiss()
                    timeOutDialog.showProgressDialog(requireContext())
                    timeOutDialog.updateProgressDialog(requireContext(), progressDialog, "fetching categories...", hideProgressBar = false)
                    categoryViewModel.getAllCategories(token, id)
                }
            }
        }

        categoryViewModel.categoryList.observe(viewLifecycleOwner, CategoriesObserver(categoryAdapter, null))


        // Example API calls
        categoryViewModel.getAllCategories(token, id)
    }

    private fun redirectToCreate(){
        // Create a new instance of CategoryDetailsFragment and pass category data
        val createCategoryFragment = CreateCategoryFragment()
//        val bundle = Bundle()
//        bundle.putString("screen", "redirectToCreate")
//        createCategoryFragment.arguments = bundle
//        // Navigate to CategoryDetailsFragment
        changeCurrentFragment(createCategoryFragment)
    }

    private fun redirectToDetails(category: Category){
        // Create a new instance of CategoryDetailsFragment and pass category data
        val categoryDetailsFragment = UpdateCategoryFragment()
        val bundle = Bundle()
        bundle.putParcelable("category", category)
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