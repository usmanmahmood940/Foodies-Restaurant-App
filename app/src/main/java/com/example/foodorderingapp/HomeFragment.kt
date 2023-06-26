package com.example.foodorderingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.Adapter.CategoryAdapter
import com.example.foodorderingapp.Adapter.FoodDomainAdapter
import com.example.foodorderingapp.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var homeViewModel:HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding =  FragmentHomeBinding.inflate(inflater)
       val activity =  requireActivity() as HomeActivity

        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val categoryAdapter = CategoryAdapter()
        val foodDomainAdapter = FoodDomainAdapter()

        binding.apply {
            rvCategories.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,false)
            rvCategories.adapter = categoryAdapter

            rvPopular.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL,false)
            rvPopular.adapter = foodDomainAdapter
        }

        homeViewModel.categoryList.observe(viewLifecycleOwner){
            it?.let {
                categoryAdapter.setList(it)
            }
        }

        homeViewModel.foodDomainList.observe(viewLifecycleOwner){
            it?.let {
                foodDomainAdapter.setList(it)
            }
        }

        return binding.root
    }






}