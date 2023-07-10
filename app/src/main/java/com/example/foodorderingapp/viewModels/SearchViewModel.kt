package com.example.foodorderingapp.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.Repositories.CategoryRepository
import com.example.foodorderingapp.Repositories.FoodItemRepository
import com.example.foodorderingapp.Response.CustomResponse
import com.example.foodorderingapp.models.Category
import com.example.foodorderingapp.models.FoodItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel  @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val foodItemRepository: FoodItemRepository
) : ViewModel() {

    val categoryList: LiveData<CustomResponse<List<Category>>>
        get() = categoryRepository.categoriesList

    val foodItemList: LiveData<CustomResponse<List<FoodItem>>>
        get() = foodItemRepository.foodItemList

    var query:String? = null

    init {
        viewModelScope.launch {
            categoryRepository.getCategoriesList()
            foodItemRepository.getFoodItemList()
        }
    }

    fun getFoodItemsByQuery(query:String): List<FoodItem> {
        val filteredCategory = mutableListOf<Category>()
        filteredCategory.addAll(categoryList.value?.data?.filter { it.name.contains(query) }?: emptyList())
        val filteredItems = foodItemList.value?.data?.filter { foodItem ->
            filteredCategory.any { category ->
                category.id == foodItem.categoryId
            } || foodItem.title.contains(query)
        } ?: emptyList()
        return filteredItems

    }

    override fun onCleared() {
        super.onCleared()
        categoryRepository.stopObservingData()
        foodItemRepository.stopObservingData()
    }


}