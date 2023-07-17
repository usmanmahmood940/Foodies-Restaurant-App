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
        holder.bind(foodDomainList[position])
    }

    override fun getItemCount(): Int {
        return foodDomainList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvPrice: TextView = itemView.findViewById(R.id.tv_price)
        private val tvAdd: TextView = itemView.findViewById(R.id.tv_add)
        private val ivFoodImage: ImageView = itemView.findViewById(R.id.iv_foodImage)
        private val foodItemLayout: ConstraintLayout = itemView.findViewById(R.id.layout_food_item)

        fun bind(foodItem: FoodItem) {
            tvTitle.text = foodItem.title
            tvPrice.text = foodItem.price.toString()
            Glide.with(ivFoodImage.context).load(foodItem.image).into(ivFoodImage)

            tvAdd.setOnClickListener {
                listener.onAddClicked(foodItem)
            }

            foodItemLayout.setOnClickListener {
                listener.onAddClicked(foodItem)
            }
        }
    }
}
