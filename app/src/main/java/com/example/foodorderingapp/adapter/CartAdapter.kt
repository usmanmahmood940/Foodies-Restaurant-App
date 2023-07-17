package com.example.foodorderingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.databinding.ItemCartBinding
import com.example.foodorderingapp.models.CartItem


class CartAdapter(
    private var cartItemList: List<CartItem> = mutableListOf(),
    private val listener: OnCartListener
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    fun setList(list: List<CartItem>) {
        cartItemList = list
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        listener.onItemRemove(position)
        notifyItemRemoved(position)
    }

    interface OnCartListener {
        fun onCartUpdate()
        fun onItemRemove(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCartBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartItemList[position])
    }

    override fun getItemCount(): Int {
        return cartItemList.size
    }

    inner class ViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.cartItem = cartItem

            binding.ivIncreaseCart.setOnClickListener {
                increaseQuantity(cartItem)
            }

            binding.ivDecreaseCart.setOnClickListener {
                decreaseQuantity(cartItem)
            }

            binding.executePendingBindings()
        }

        private fun increaseQuantity(cartItem: CartItem) {
            cartItem.quantity++
            cartItem.updateTotalAmount()
            listener.onCartUpdate()
            binding.invalidateAll()
        }

        private fun decreaseQuantity(cartItem: CartItem) {
            if (cartItem.quantity > 1) {
                cartItem.quantity--
                cartItem.updateTotalAmount()
                listener.onCartUpdate()
                binding.invalidateAll()
            }
        }
    }
}
