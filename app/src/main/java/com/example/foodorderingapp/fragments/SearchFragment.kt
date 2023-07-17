package com.example.foodorderingapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.foodorderingapp.adapter.FoodItemAdapter
import com.example.foodorderingapp.BottomSheet.AddToCartBottomSheet
import com.example.foodorderingapp.Listeners.FoodItemClickListener
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.viewModels.SearchViewModel
import com.example.foodorderingapp.databinding.FragmentSearchBinding
import com.example.foodorderingapp.models.FoodItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var foodItemAdapter: FoodItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        initListeners()
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        setupFoodItemAdapter()
        observeFoodItemList()
        return binding.root
    }

    private fun initListeners() {
        binding.etSearch.requestFocus()
        setupSearchBar()
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupFoodItemAdapter() {
        foodItemAdapter = FoodItemAdapter(listener = object : FoodItemClickListener {
            override fun onAddClicked(foodItem: FoodItem) {
                val addToCartBottomSheet = AddToCartBottomSheet.newInstance(foodItem)
                addToCartBottomSheet.show(requireActivity().supportFragmentManager, "addToCartBottomSheet")
            }
        })
        binding.rvSearchFood.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvSearchFood.adapter = foodItemAdapter
    }

    private fun observeFoodItemList() {
        searchViewModel.foodItemList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is CustomResponse.Loading -> {
                    binding.progressBarSearch.visibility = View.VISIBLE
                    binding.rvSearchFood.visibility = View.INVISIBLE
                }
                is CustomResponse.Success -> {
                    binding.progressBarSearch.visibility = View.GONE
                    binding.rvSearchFood.visibility = View.VISIBLE
                    if (response.data != null) {
                        val list = searchViewModel.getFoodItemsByQuery(searchViewModel.query?:"")
                        foodItemAdapter.setList(list)
                    }
                }
                is CustomResponse.Error -> {
                    binding.progressBarSearch.visibility = View.GONE
                    showDialogBox("Error", response.errorMessage.toString())
                }
                else -> {}
            }
        }
    }

    private fun setupSearchBar() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(editable: Editable?) {
                val query = editable.toString()
                searchViewModel.query = query
                val list = searchViewModel.getFoodItemsByQuery(query)
                foodItemAdapter.setList(list)
            }
        })
    }

    private fun showDialogBox(title: String, message: String) {
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.show()
    }
}
