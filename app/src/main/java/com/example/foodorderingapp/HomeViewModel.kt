package com.example.foodorderingapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.foodorderingapp.models.Category
import com.example.foodorderingapp.models.FoodDomain

class HomeViewModel : ViewModel() {

    val categoryList: MutableLiveData<List<Category>> = MutableLiveData()
    val foodDomainList: MutableLiveData<List<FoodDomain>> = MutableLiveData()

    init {
        categoryList.value = getCategoryList()
        foodDomainList.value = getFoodDomainList()
    }

    fun getCategoryList(): List<Category> {
        return mutableListOf(
            Category("Pizza", R.drawable.cat_1),
            Category("Burger", R.drawable.cat_2),
            Category("Hotdog", R.drawable.cat_3),
            Category("Drink", R.drawable.cat_4),
            Category("Donut", R.drawable.cat_5)
        )
    }

    fun getFoodDomainList(): List<FoodDomain> {
        return mutableListOf(
            FoodDomain("Pepperoni Pizza", R.drawable.pop_1,"slices pepperoni, mozerella cheese, fresh organo,",9.76),
            FoodDomain("Cheese Burger", R.drawable.pop_2,"Beef, Special Sauce, Lettuce ,",5.76),
            FoodDomain("Vegetable Pizza", R.drawable.pop_3,"olives, Vegetable oil, Cherry Tomatoes ,",7.76),
        )
    }


}