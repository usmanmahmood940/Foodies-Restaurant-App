package com.example.foodorderingapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.adapter.CartAdapter
import com.example.foodorderingapp.CartItemTouchHelperCallback
import com.example.foodorderingapp.Utils.Constants.ZERO
import com.example.foodorderingapp.viewModels.CartViewModel
import com.example.foodorderingapp.databinding.FragmentCartBinding
import com.example.foodorderingapp.models.Amounts
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartViewModel: CartViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(inflater, container, false)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        val amounts = Amounts()

        binding.amounts = amounts
        binding.lifecycleOwner = this


        val cartAdapter = CartAdapter(listener = object : CartAdapter.onCartListener {
            override fun onCartUpdate() {
                cartViewModel.updateCart()

            }

            override fun onItemRemove(position: Int) {
                cartViewModel.removeItem(position)
            }

        })
        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = cartAdapter

        val itemTouchHelperCallback = CartItemTouchHelperCallback(cartAdapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvCart)

        cartViewModel.cartItemList.observe(viewLifecycleOwner) {
            it?.let {
                if(it.size > ZERO) {
                    binding.tvCartEmpty.visibility = View.GONE
                    binding.svCart.visibility = View.VISIBLE
                    cartAdapter.setList(it)
                    amounts.updateTotalItemAmount(it.sumOf{ cartItem ->
                        cartItem.totalAmount
                    })
                    binding.invalidateAll()
                }
                else{
                    binding.svCart.visibility = View.GONE
                    binding.tvCartEmpty.visibility = View.VISIBLE
                }
            
            }
        }

        binding.btnCheckout.setOnClickListener {
            // Passin amount to nexr fragment
            val action =  CartFragmentDirections.actionCartFragmentToCheckoutFragment(amounts = amounts)
            findNavController().navigate(action)
        }


        return binding.root
    }


}