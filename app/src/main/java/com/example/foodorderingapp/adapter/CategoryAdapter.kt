package com.example.foodorderingapp.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.Listeners.CategoryClickListener
import com.example.foodorderingapp.CategoryColors
import com.example.foodorderingapp.R
import com.example.foodorderingapp.models.Category

class CategoryAdapter(private var categoryList: List<Category> = emptyList(),private val listener: CategoryClickListener) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var backgroundColor = CategoryColors.PINK
    private var currentItem :  CardView? = null
    fun setList(list: List<Category>) {
        categoryList = list
        notifyDataSetChanged()
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.apply {
            val category = categoryList[position]
            tvCategoryName.text = category.name
            Glide.with(ivCategoryIcon.context).load(category.icon).into(ivCategoryIcon)

            setBackgrounds(layout_category)

            layout_category.setOnClickListener{
                currentItem?.let{
                     if(currentItem == layout_category){
                         currentItem?.cardElevation = 0F
                         currentItem = null
                         listener.onItemDeselect()
                         return@setOnClickListener
                     }
                     currentItem?.cardElevation = 0F
                     currentItem = layout_category
                     currentItem?.cardElevation = 12F
                     listener.onItemClick(category)
                     return@setOnClickListener
                }
                currentItem = layout_category
                currentItem?.cardElevation = 12F
                listener.onItemClick(category)
            }
        }
    }

    private fun setBackgrounds(layout_category: CardView) {
        backgroundColor?.let {
            when(backgroundColor){
                CategoryColors.PINK ->{
                    layout_category.backgroundTintList = getColor(hexCode = backgroundColor.hexCode)
                    backgroundColor = CategoryColors.PURPLE
                }
                CategoryColors.PURPLE ->{
                    layout_category.backgroundTintList = getColor(hexCode = backgroundColor.hexCode)

                    backgroundColor = CategoryColors.BLUE
                }
                CategoryColors.BLUE ->{
                    layout_category.backgroundTintList = getColor(hexCode = backgroundColor.hexCode)
                    backgroundColor = CategoryColors.GREEN
                }
                CategoryColors.GREEN -> {
                    layout_category.backgroundTintList = getColor(hexCode = backgroundColor.hexCode)
                    backgroundColor = CategoryColors.PINK
                }
                else -> {}
            }
            
        }
    }

    fun getColor(hexCode:String):  ColorStateList{
        val color = Color.parseColor(hexCode)
        val colorStateList = ColorStateList.valueOf(color)
        return colorStateList

    }
    override fun getItemCount(): Int {
        return categoryList.size
    }

    class ViewHolder(CategoryView: View) : RecyclerView.ViewHolder(CategoryView) {
        val layout_category: CardView = CategoryView.findViewById(R.id.card_item)
        val tvCategoryName: TextView = CategoryView.findViewById(R.id.tv_category_name)
        val ivCategoryIcon: ImageView = CategoryView.findViewById(R.id.iv_category)
    }
}