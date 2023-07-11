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
    lateinit var auth:FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)


        auth.currentUser?.apply {
            binding.tvName.text =  "Hi $displayName"
            Glide.with(this@HomeFragment).load(photoUrl).into(binding.ivProfile)
//            binding.ivProfile.setImageURI(photoUrl!!)
            println(photoUrl)
        }



        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        val foodDomainAdapter = FoodItemAdapter(listener = object : FoodItemClickListener{
            override fun onAddClicked(foodItem: FoodItem) {
                val addToCartBottomSheet = AddToCartBottomSheet.newInstance(foodItem)
                addToCartBottomSheet.show(requireActivity().supportFragmentManager, "addToCartBottomSheet")

                addToCartBottomSheet.dialog?.setOnShowListener {
                    val dialog = addToCartBottomSheet.dialog as BottomSheetDialog
                    val bottomSheetBehavior = dialog.behavior
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

        })


        val categoryAdapter = CategoryAdapter(listener = object : CategoryClickListener {
            override fun onItemClick(category: Category) {
                val filteredList = homeViewModel.getFoodItemsByCategory(category)
                foodDomainAdapter.setList(filteredList)
                binding.tvLabelCategory.text = category.name
            }

            override fun onItemDeselect() {
                val list = homeViewModel.foodItemList.value?.data
                foodDomainAdapter.setList(list ?: emptyList())
                binding.tvLabelCategory.text = Constants.POPULAR
            }

        })


        binding.apply {
            rvCategories.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL, false
            )
            rvCategories.adapter = categoryAdapter

            rvPopular.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL, false
            )
            rvPopular.adapter = foodDomainAdapter
        }

     
        homeViewModel.categoryList.observe(viewLifecycleOwner){
            when(it){
                is CustomResponse.Loading -> {
                    binding.rvCategories.visibility = View.INVISIBLE
                    binding.progressBarCategory.visibility = View.VISIBLE

                }
                is CustomResponse.Success -> {
                    if(it.data != null){
                        binding.rvCategories.visibility = View.VISIBLE
                        binding.progressBarCategory.visibility = View.GONE
                        categoryAdapter.setList(it.data)
                    }
                }
                is CustomResponse.Error -> {
                    binding.progressBarCategory.visibility = View.GONE
                    val alertDialog = AlertDialog.Builder(requireActivity())
                    alertDialog.setTitle("Error Message")
                    alertDialog.setMessage(it.errorMessage)
                    alertDialog.show()

//                    binding.tvError.visibility = View.VISIBLE
//                    binding.tvError.text = it.errorMessage
                }
                else -> {}
            }

        }

        homeViewModel.foodItemList.observe(viewLifecycleOwner){
            when(it){
                is CustomResponse.Loading -> {
                    binding.rvPopular.visibility = View.INVISIBLE
                    binding.progressBarFooditem.visibility = View.VISIBLE

                }
                is CustomResponse.Success -> {
                    binding.rvPopular.visibility = View.VISIBLE
                    binding.progressBarFooditem.visibility = View.GONE
                    if(it.data != null){
                        if(homeViewModel.category == null){
                            foodDomainAdapter.setList(it.data)
                        }
                        else{
                            val list = homeViewModel.getFoodItemsByCategory(homeViewModel.category!!)
                            foodDomainAdapter.setList(list)
                        }


                    }
                }
                is CustomResponse.Error -> {
                    

                }
                else -> {}
            }

        }
     
        binding.apply {
            etSearch.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }
            tvSeeAll.setOnClickListener{
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
            }
        }

        binding.ivProfile.setOnClickListener{
            auth.signOut()
            startActivity(Intent(requireContext(),LoginActivity::class.java))
            requireActivity().finish()
        }

        return binding.root
    }


    






}