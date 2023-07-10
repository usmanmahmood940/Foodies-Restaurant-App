package com.example.foodorderingapp.Fragments

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
import com.example.foodorderingapp.Adapter.FoodItemAdapter
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
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater)
        initListeners()


        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)



        foodItemAdapter = FoodItemAdapter(listener = object : FoodItemClickListener {
            override fun onAddClicked(foodItem: FoodItem) {
                val addToCartBottomSheet = AddToCartBottomSheet.newInstance(foodItem)
                addToCartBottomSheet.show(requireActivity().supportFragmentManager, "addToCartBottomSheet")
            }

        })

        binding.apply {
            rvSearchFood.layoutManager = GridLayoutManager(requireContext(), 2)
            rvSearchFood.adapter = foodItemAdapter

        }

        searchViewModel.foodItemList.observe(viewLifecycleOwner){
            when(it){
                is CustomResponse.Loading -> {

                }
                is CustomResponse.Success -> {
                    if(it.data != null){
                        if(searchViewModel.query == null) {
                            foodItemAdapter.setList(it.data)
                        }
                        else{
                           val list = searchViewModel.getFoodItemsByQuery(searchViewModel.query!!)
                            foodItemAdapter.setList(list)
                        }
                    }
                }
                is CustomResponse.Error -> {

                }
                else -> {}
            }

        }

        return binding.root
    }

    private fun initListeners() {
        binding.etSearch.requestFocus()
        searchBarSetup()
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun searchBarSetup() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {

                val query = editable.toString()
                searchViewModel.query = query
                val list = searchViewModel.getFoodItemsByQuery(query)
                foodItemAdapter.setList(list)
            }

        })
    }


}