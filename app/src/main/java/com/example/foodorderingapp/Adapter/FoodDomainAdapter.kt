package com.example.foodorderingapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.CategoryColors
import com.example.foodorderingapp.R
import com.example.foodorderingapp.models.FoodDomain

class FoodDomainAdapter(private var foodDomainList: List<FoodDomain> = emptyList()) :
    RecyclerView.Adapter<FoodDomainAdapter.ViewHolder>() {


    fun setList(list: List<FoodDomain>) {
        foodDomainList = list
        notifyDataSetChanged()
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food_domain, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.apply {
            val foodDomain = foodDomainList[position]

            tvTitle.text = foodDomain.title
            tvPrice.text = foodDomain.price.toString()

            val drawable = ContextCompat.getDrawable(ivFoodImage.context, foodDomain.image)
            ivFoodImage.setImageDrawable(drawable)


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

    }
}