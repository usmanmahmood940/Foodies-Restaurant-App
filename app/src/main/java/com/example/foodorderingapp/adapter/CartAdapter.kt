package com.example.foodorderingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.databinding.ItemCartBinding
import com.example.foodorderingapp.models.CartItem


class CartAdapter(private var cartItemList: List<CartItem> = mutableListOf(), private val listener: onCartListener) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {


    fun setList(list: List<CartItem>) {
        cartItemList = list
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        listener.onItemRemove(position)
        notifyItemRemoved(position)
    }

    interface onCartListener{
        fun onCartUpdate()
        fun onItemRemove(position: Int)
    }

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCartBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cartItemList[position]
        holder.bind(cartItem)

    }


    override fun getItemCount(): Int {
        return cartItemList.size
    }

    inner class ViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            binding.cartItem = cartItem
            binding.ivIncreaseCart.setOnClickListener {
                // Increase the quantity of the CartItem
                cartItem.quantity++
                 cartItem.updateTotalAmount()
                 listener.onCartUpdate()
                 binding.invalidateAll()
                // Notify the data binding that the property has changed
            }
            binding.ivDecreaseCart.setOnClickListener {
                // Increase the quantity of the CartItem

                if (cartItem.quantity > 1) {
                    cartItem.quantity--
                    cartItem.updateTotalAmount()
                    listener.onCartUpdate()
                    binding.invalidateAll()
                }

                // Notify the data binding that the property has changed
            }
            binding.executePendingBindings()
        }

    }
}