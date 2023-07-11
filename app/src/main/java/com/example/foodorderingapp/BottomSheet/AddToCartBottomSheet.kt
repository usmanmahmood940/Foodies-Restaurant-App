package com.example.foodorderingapp.BottomSheet

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.foodorderingapp.CartManager
import com.example.foodorderingapp.R
import com.example.foodorderingapp.Utils.Constants.ONE
import com.example.foodorderingapp.databinding.FragmentAddToCartBottomSheetBinding
import com.example.foodorderingapp.models.CartItem
import com.example.foodorderingapp.models.FoodItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddToCartBottomSheet : RoundedBottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddToCartBottomSheetBinding

    @Inject
    lateinit var cartManager: CartManager

    companion object {
        private const val ARG_FOOD_ITEM = "foodItem"

        fun newInstance(foodItem: FoodItem): AddToCartBottomSheet {
            val args = Bundle()
            args.putParcelable(ARG_FOOD_ITEM, foodItem)

            val fragment = AddToCartBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddToCartBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the FoodItem from the arguments
        val foodItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable<FoodItem>(ARG_FOOD_ITEM, FoodItem::class.java)
        } else {
            arguments?.getParcelable<FoodItem>(ARG_FOOD_ITEM)
        }
        Glide.with(binding.ivFooditemImage.context).load(foodItem?.image)
            .into(binding.ivFooditemImage)

        binding.tvName.text = foodItem?.title
        binding.tvDescription.text = foodItem?.description
        binding.tvPrice.text = foodItem?.price.toString()
        binding.tvTotalAmount.text = foodItem?.price.toString()
        val price = foodItem?.price!!
        var quantity = ONE
        var totalAmount = quantity * price
        binding.tvQuantity.text = quantity.toString()

        binding.ivIncrease.setOnClickListener {
            quantity++
            binding.tvQuantity.text = quantity.toString()
            totalAmount = quantity * price
            binding.tvTotalAmount.text = totalAmount.toString()
        }
        binding.ivDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
                totalAmount = quantity * price
                binding.tvTotalAmount.text = totalAmount.toString()
            }
        }

        binding.tvAddToCart.setOnClickListener {
            // Perform the action
            val cartItem = CartItem(foodItem, quantity, totalAmount)
            cartManager.addToCart(cartItem)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val myDialog = dialog as BottomSheetDialog
        myDialog?.let { dialog ->
            val bottomSheetBehavior = dialog.behavior
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

}