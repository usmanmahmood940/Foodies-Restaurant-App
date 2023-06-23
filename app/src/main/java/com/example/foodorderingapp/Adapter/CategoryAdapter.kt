package com.example.foodorderingapp.Adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.CategoryColors
import com.example.foodorderingapp.R
import com.example.foodorderingapp.models.Category

class CategoryAdapter(private var categoryList: List<Category> = emptyList()) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var backgroundColor = CategoryColors.PINK

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
            val drawable = ContextCompat.getDrawable(ivCategoryIcon.context, category.icon)
            ivCategoryIcon.setImageDrawable(drawable)

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

                return@apply
            }

//            backgroundColor = CategoryColors.PURPLE
//            backgroundColor?.apply {
//                val color = Color.parseColor(hexCode)
//                val colorStateList = ColorStateList.valueOf(color)
//                layout_category.backgroundTintList = colorStateList
//
//            }

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