package com.example.foodorderingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.Adapter.CategoryAdapter
import com.example.foodorderingapp.Adapter.FoodDomainAdapter
import com.example.foodorderingapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

  lateinit var binding: ActivityHomeBinding
  lateinit var homeViewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val categoryAdapter = CategoryAdapter()
        val foodDomainAdapter = FoodDomainAdapter()
        binding.apply {
            rvCategories.layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.HORIZONTAL,false)
            rvCategories.adapter = categoryAdapter

            rvPopular.layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.HORIZONTAL,false)
            rvPopular.adapter = foodDomainAdapter
        }



        homeViewModel.categoryList.observe(this, Observer {
            it?.let {
                categoryAdapter.setList(it)
            }
        })

        homeViewModel.foodDomainList.observe(this, Observer {
            it?.let {
                foodDomainAdapter.setList(it)
            }
        })




    }
}