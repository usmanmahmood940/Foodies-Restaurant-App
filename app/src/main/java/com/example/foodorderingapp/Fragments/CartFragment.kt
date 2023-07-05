package com.example.foodorderingapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodorderingapp.Adapter.CartAdapter
import com.example.foodorderingapp.CartItemTouchHelperCallback
import com.example.foodorderingapp.R
import com.example.foodorderingapp.viewModels.CartViewModel
import com.example.foodorderingapp.databinding.FragmentCartBinding
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

        binding.cartViewModel = cartViewModel
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
                if(it.size> 0) {
                    binding.tvCartEmpty.visibility = View.GONE
                    binding.svCart.visibility = View.VISIBLE
                    cartAdapter.setList(it)
                    cartViewModel.updateAmounts()
                    binding.invalidateAll()
                }
                else{
                    binding.svCart.visibility = View.GONE
                    binding.tvCartEmpty.visibility = View.VISIBLE
                }
            
            }
        }


        return binding.root
    }


}