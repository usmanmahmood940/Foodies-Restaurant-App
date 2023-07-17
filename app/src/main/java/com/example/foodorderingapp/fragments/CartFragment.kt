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
import com.example.foodorderingapp.models.CartItem
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var cartViewModel: CartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        setupViews()
        observeCartItems()

        return binding.root
    }

    private fun setupViews() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            amounts = Amounts()

            val cartAdapter = createCartAdapter()
            setupRecyclerView(cartAdapter)
            setupItemTouchHelper(cartAdapter)

            btnCheckout.setOnClickListener {
                navigateToCheckout()
            }
        }
    }

    private fun createCartAdapter(): CartAdapter {
        return CartAdapter(listener = object : CartAdapter.OnCartListener {
            override fun onCartUpdate() {
                cartViewModel.updateCart()
            }

            override fun onItemRemove(position: Int) {
                cartViewModel.removeItem(position)
            }
        })
    }

    private fun setupRecyclerView(cartAdapter: CartAdapter) {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
        }
    }

    private fun setupItemTouchHelper(cartAdapter: CartAdapter) {
        val itemTouchHelperCallback = CartItemTouchHelperCallback(cartAdapter)
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvCart)
    }

    private fun observeCartItems() {
        cartViewModel.cartItemList.observe(viewLifecycleOwner) { cartItems ->
            if (cartItems.isNotEmpty()) {
                showCartItems(cartItems)
            } else {
                showEmptyCart()
            }
        }
    }

    private fun showCartItems(cartItems: List<CartItem>) {
        binding.apply {
            tvCartEmpty.visibility = View.GONE
            svCart.visibility = View.VISIBLE

            val amounts = amounts ?: return
            val totalAmount = cartItems.sumOf { it.totalAmount }
            amounts.updateTotalItemAmount(totalAmount)

            val cartAdapter = binding.rvCart.adapter as? CartAdapter
            cartAdapter?.setList(cartItems)

            invalidateAll()
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            svCart.visibility = View.GONE
            tvCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun navigateToCheckout() {
        val amounts = binding.amounts ?: return
        val action = CartFragmentDirections.actionCartFragmentToCheckoutFragment(amounts = amounts)
        findNavController().navigate(action)
    }
}
