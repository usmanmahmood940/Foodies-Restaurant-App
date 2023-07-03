package com.example.foodorderingapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.foodorderingapp.databinding.FragmentAddToCartBottomSheetBinding
import com.example.foodorderingapp.models.FoodItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddToCartBottomSheet : RoundedBottomSheetDialogFragment() {
    private lateinit var binding: FragmentAddToCartBottomSheetBinding

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
        binding = FragmentAddToCartBottomSheetBinding.inflate(inflater,container,false)
       return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the FoodItem from the arguments
        val foodItem = arguments?.getParcelable<FoodItem>(ARG_FOOD_ITEM)
        Glide.with(binding.ivFooditemImage.context).load(foodItem?.image).into(binding.ivFooditemImage)

        binding.tvName.text = foodItem?.title
        binding.tvDescription.text = foodItem?.description
        binding.tvPrice.text = foodItem?.price.toString()
        binding.tvTotalAmount.text = foodItem?.price.toString()
        val price = foodItem?.price!!
        var quantity = 1
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

        // Example logic for adding the item to the cart
        binding.tvAddToCart.setOnClickListener {
            // Perform the action

            dismiss()
        }
    }


}