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

class CategoryAdapter(
    private var categoryList: List<Category> = emptyList(),
    private val listener: CategoryClickListener
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var backgroundColor = CategoryColors.PINK
    private var currentItem: CardView? = null

    fun setList(list: List<Category>) {
        categoryList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class ViewHolder(private val categoryView: View) : RecyclerView.ViewHolder(categoryView) {
        private val layoutCategory: CardView = categoryView.findViewById(R.id.card_item)
        private val tvCategoryName: TextView = categoryView.findViewById(R.id.tv_category_name)
        private val ivCategoryIcon: ImageView = categoryView.findViewById(R.id.iv_category)

        init {
            layoutCategory.setOnClickListener {
                onCategoryClick()
            }
        }

        fun bind(category: Category) {
            tvCategoryName.text = category.name
            Glide.with(ivCategoryIcon.context).load(category.icon).into(ivCategoryIcon)
            setBackgrounds(layoutCategory)
        }

        private fun onCategoryClick() {
            if (currentItem == layoutCategory) {
                currentItem?.cardElevation = 0F
                currentItem = null
                listener.onItemDeselect()
            } else {
                currentItem?.cardElevation = 0F
                currentItem = layoutCategory
                currentItem?.cardElevation = 12F
                listener.onItemClick(categoryList[adapterPosition])
            }
        }
    }

    private fun setBackgrounds(layoutCategory: CardView) {
        when (backgroundColor) {
            CategoryColors.PINK -> {
                layoutCategory.backgroundTintList = getColorStateList(CategoryColors.PINK.hexCode)
                backgroundColor = CategoryColors.PURPLE
            }
            CategoryColors.PURPLE -> {
                layoutCategory.backgroundTintList = getColorStateList(CategoryColors.PURPLE.hexCode)
                backgroundColor = CategoryColors.BLUE
            }
            CategoryColors.BLUE -> {
                layoutCategory.backgroundTintList = getColorStateList(CategoryColors.BLUE.hexCode)
                backgroundColor = CategoryColors.GREEN
            }
            CategoryColors.GREEN -> {
                layoutCategory.backgroundTintList = getColorStateList(CategoryColors.GREEN.hexCode)
                backgroundColor = CategoryColors.PINK
            }
            else -> {}
        }
    }

    private fun getColorStateList(hexCode: String): ColorStateList {
        val color = Color.parseColor(hexCode)
        return ColorStateList.valueOf(color)
    }
}
