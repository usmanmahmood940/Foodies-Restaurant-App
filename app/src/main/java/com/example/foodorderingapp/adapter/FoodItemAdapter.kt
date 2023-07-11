package com.example.foodorderingapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodorderingapp.Listeners.FoodItemClickListener
import com.example.foodorderingapp.R
import com.example.foodorderingapp.models.FoodItem

class FoodItemAdapter(
    private var foodDomainList: List<FoodItem> = emptyList(),
    private val listener: FoodItemClickListener,
    ) : RecyclerView.Adapter<FoodItemAdapter.ViewHolder>() {

    fun setList(list: List<FoodItem>) {
        foodDomainList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.apply {
            val foodItem = foodDomainList[position]

            tvTitle.text = foodItem.title
            tvPrice.text = foodItem.price.toString()
            Glide.with(ivFoodImage.context).load(foodItem.image).into(ivFoodImage)

            tvAdd.setOnClickListener{
                listener.onAddClicked(foodItem)
            }

            foodItemLayout.setOnClickListener{
                listener.onAddClicked(foodItem)
            }
            
        }

    }

    override fun getItemCount(): Int {
        return foodDomainList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val tvTitle: TextView = ItemView.findViewById(R.id.tv_title)
        val tvPrice: TextView = ItemView.findViewById(R.id.tv_price)
        val tvAdd: TextView = ItemView.findViewById(R.id.tv_add)
        val ivFoodImage: ImageView = ItemView.findViewById(R.id.iv_foodImage)
        val foodItemLayout:ConstraintLayout = ItemView.findViewById(R.id.layout_food_item)

    }
}