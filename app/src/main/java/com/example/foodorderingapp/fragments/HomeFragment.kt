package com.example.foodorderingapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodorderingapp.activities.LoginActivity
import com.example.foodorderingapp.adapter.CategoryAdapter
import com.example.foodorderingapp.adapter.FoodItemAdapter
import com.example.foodorderingapp.BottomSheet.AddToCartBottomSheet
import com.example.foodorderingapp.Listeners.CategoryClickListener
import com.example.foodorderingapp.Listeners.FoodItemClickListener
import com.example.foodorderingapp.viewModels.HomeViewModel
import com.example.foodorderingapp.R
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.databinding.FragmentHomeBinding
import com.example.foodorderingapp.models.Category
import com.example.foodorderingapp.models.FoodItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        setupUserProfile()
        setupViewModels()
        setupAdapters()

        setListeners()

        observeCategoryList()
        observeFoodItemList()

        return binding.root
    }

    private fun setupUserProfile() {
        auth.currentUser?.apply {
            binding.tvName.text = "Hi $displayName"
            Glide.with(this@HomeFragment).load(photoUrl).into(binding.ivProfile)
            println(photoUrl)
        }
    }

    private fun setupViewModels() {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private fun setupAdapters() {
        val foodDomainAdapter = createFoodDomainAdapter()
        val categoryAdapter = createCategoryAdapter()

        binding.rvCategories.apply {
            layoutManager = createHorizontalLayoutManager()
            adapter = categoryAdapter
        }

        binding.rvPopular.apply {
            layoutManager = createHorizontalLayoutManager()
            adapter = foodDomainAdapter
        }
    }

    private fun createFoodDomainAdapter(): FoodItemAdapter {
        return FoodItemAdapter(listener = object : FoodItemClickListener {
            override fun onAddClicked(foodItem: FoodItem) {
                showAddToCartBottomSheet(foodItem)
            }
        })
    }

    private fun createCategoryAdapter(): CategoryAdapter {
        return CategoryAdapter(listener = object : CategoryClickListener {
            override fun onItemClick(category: Category) {
                val filteredList = homeViewModel.getFoodItemsByCategory(category)
                binding.rvPopular.adapter?.let { adapter ->
                    if (adapter is FoodItemAdapter) {
                        adapter.setList(filteredList)
                    }
                }
                binding.tvLabelCategory.text = category.name
            }

            override fun onItemDeselect() {
                binding.rvPopular.adapter?.let { adapter ->
                    if (adapter is FoodItemAdapter) {
                        val list = homeViewModel.foodItemList.value?.data
                        adapter.setList(list ?: emptyList())
                    }
                }
                binding.tvLabelCategory.text = Constants.POPULAR
            }
        })
    }

    private fun createHorizontalLayoutManager(): LinearLayoutManager {
        return LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setListeners() {
        binding.etSearch.setOnClickListener {
            navigateToSearchFragment()
        }

        binding.tvSeeAll.setOnClickListener {
            navigateToSearchFragment()
        }

        binding.ivProfile.setOnClickListener {
            signOutAndNavigateToLogin()
        }
    }

    private fun navigateToSearchFragment() {
        findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
    }

    private fun signOutAndNavigateToLogin() {
        auth.signOut()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    private fun observeCategoryList() {
        homeViewModel.categoryList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is CustomResponse.Loading -> {
                    binding.rvCategories.visibility = View.INVISIBLE
                    binding.progressBarCategory.visibility = View.VISIBLE
                }
                is CustomResponse.Success -> {
                    binding.rvCategories.visibility = View.VISIBLE
                    binding.progressBarCategory.visibility = View.GONE
                    response.data?.let { categoryList ->
                        (binding.rvCategories.adapter as? CategoryAdapter)?.setList(categoryList)
                    }
                }
                is CustomResponse.Error -> {
                    binding.progressBarCategory.visibility = View.GONE
                    showDialogBox("Error Message", response.errorMessage.toString())
                }
            }
        }
    }

    private fun observeFoodItemList() {
        homeViewModel.foodItemList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is CustomResponse.Loading -> {
                    binding.rvPopular.visibility = View.INVISIBLE
                    binding.progressBarFooditem.visibility = View.VISIBLE
                }
                is CustomResponse.Success -> {
                    binding.rvPopular.visibility = View.VISIBLE
                    binding.progressBarFooditem.visibility = View.GONE
                    response.data?.let { foodItemList ->
                        (binding.rvPopular.adapter as? FoodItemAdapter)?.apply {
                            val list = homeViewModel.category?.let {
                                homeViewModel.getFoodItemsByCategory(it)
                            } ?: foodItemList
                            setList(list)
                        }
                    }
                }
                is CustomResponse.Error -> {
                    binding.progressBarFooditem.visibility = View.GONE
                    showDialogBox("Error Message", response.errorMessage.toString())
                }
            }
        }
    }

    private fun showAddToCartBottomSheet(foodItem: FoodItem) {
        val addToCartBottomSheet = AddToCartBottomSheet.newInstance(foodItem)
        addToCartBottomSheet.show(
            requireActivity().supportFragmentManager,
            "addToCartBottomSheet"
        )

        addToCartBottomSheet.dialog?.setOnShowListener {
            val dialog = addToCartBottomSheet.dialog as BottomSheetDialog
            val bottomSheetBehavior = dialog.behavior
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun showDialogBox(title: String, message: String) {
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.show()
    }
}
