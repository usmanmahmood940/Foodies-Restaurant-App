package com.example.foodorderingapp.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.foodorderingapp.R
import com.example.foodorderingapp.Utils.Constants
import com.example.foodorderingapp.Utils.Constants.LATITUDE
import com.example.foodorderingapp.Utils.Constants.LOCATION_DATA
import com.example.foodorderingapp.Utils.Constants.LONGITUDE
import com.example.foodorderingapp.Utils.Constants.MY_LATITUDE
import com.example.foodorderingapp.Utils.Constants.MY_LONGITUDE
import com.example.foodorderingapp.Utils.Constants.ZERO
import com.example.foodorderingapp.Utils.Helper.isValidEmail
import com.example.foodorderingapp.Utils.NetworkUtils.Companion.checkForInternet
import com.example.foodorderingapp.databinding.FragmentCheckoutBinding
import com.example.foodorderingapp.models.*
import com.example.foodorderingapp.viewModels.CheckoutViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CheckoutFragment : Fragment() {

    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var navController: NavController
    private lateinit var checkoutViewModel: CheckoutViewModel

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        navController = findNavController()
        checkoutViewModel = ViewModelProvider(this)[CheckoutViewModel::class.java]

        initListeners()

        val navBackStackEntry = navController.getBackStackEntry(R.id.checkoutFragment)
        val locationData = navBackStackEntry.savedStateHandle.getLiveData<Bundle>(LOCATION_DATA)
        locationData.observe(viewLifecycleOwner) { data ->
            val latitude = data.getDouble(LATITUDE)
            val longitude = data.getDouble(LONGITUDE)
            checkoutViewModel.latitude = latitude
            checkoutViewModel.longitude = longitude

            binding.etAddress.setText(getAddressFromLocation(requireContext(), latitude, longitude))
            val chosenLatLng = LatLng(latitude, longitude)
            val restaurantLatLng = LatLng(MY_LATITUDE, MY_LONGITUDE)

            checkoutViewModel.distance =
                checkoutViewModel.calculateDistanceInKm(chosenLatLng, restaurantLatLng)
            if (!checkoutViewModel.validDistance()) {
                showDialogBox("Area out of Reach",getString(R.string.delivery_area_error))
            }
        }

        return binding.root
    }

    private fun showDialogBox(title:String, message:String) {
        val alertDialog = AlertDialog.Builder(requireActivity())
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.show()
    }

    private fun initListeners() {
        binding.apply {
            etName.setText(auth.currentUser?.displayName)
            etEmail.setText(auth.currentUser?.email)
            etMobileNum.setText(auth.currentUser?.phoneNumber)
            etAddress.isFocusableInTouchMode = false

            etAddress.setOnClickListener {
                navController.navigate(R.id.action_checkoutFragment_to_mapsFragment)
            }

            btnPlaceOrder.setOnClickListener {
                placeOrder()
            }

        }
    }

    private fun placeOrder() {
        binding.apply {
            when {
                etName.text.toString().isNullOrBlank() -> {
                    etName.error = getString(R.string.name_required_error)
                    etName.requestFocus()
                }
                etEmail.text.toString().isNullOrBlank() -> {
                    etEmail.error = getString(R.string.email_required_error)
                    etEmail.requestFocus()
                }
                !etEmail.text.toString().isValidEmail() -> {
                    etEmail.error = getString(R.string.email_valid_error)
                    etEmail.requestFocus()
                }
                etMobileNum.text.toString().isNullOrBlank() -> {
                    etMobileNum.error = getString(R.string.mobile_required_error)
                    etMobileNum.requestFocus()
                }
                etAddress.text.isNullOrBlank() -> {
                    etAddress.error = getString(R.string.address_required_error)
                    etAddress.isFocusableInTouchMode = true
                    etAddress.requestFocus()
                    etAddress.isFocusableInTouchMode = false

                }
                !checkoutViewModel.validDistance() -> {
                    showDialogBox("Area out of Reach",getString(R.string.delivery_area_error))
                }
                else -> {

                    val customerInfo = CustomerInfo(
                        name = etName.text.toString().trim(),
                        email = etEmail.text.toString().trim(),
                        phoneNumner = etMobileNum.text.toString().trim()
                    )
                    val deliveryInfo = DeliveryInfo(
                        locationLatitude = checkoutViewModel.latitude,
                        locationLongitude = checkoutViewModel.latitude
                    )
                    val paymentMethod = PaymentMethod.CashOnDelivery("CashOnDelivery")
                    var cartItemList = emptyList<CartItem>()
                    val cartJson = sharedPreferences.getString(Constants.CART, null)
                    if (!cartJson.isNullOrEmpty()) {
                        val type = object : TypeToken<MutableList<CartItem>?>() {}.type
                        cartItemList = Gson().fromJson<MutableList<CartItem>>(cartJson, type)
                    }
                    val totalAmount = cartItemList.sumOf { it.totalAmount }
                    val args: CheckoutFragmentArgs by navArgs()
                    val amounts = args.amounts
                    amounts.updateTotalItemAmount(totalAmount)
                    val order = Order(
                        customerInfo = customerInfo,
                        deliveryInfo = deliveryInfo,
                        paymentMethod = paymentMethod,
                        cartItemList = cartItemList,
                        amounts = amounts
                    )
                    if(checkForInternet(requireActivity().applicationContext)) {
                        checkoutViewModel.placeOrder(order){ success, exception ->
                            if(success){
                                showDialogBox("Order Confirmation","Your Order is Confirmed")
                            }
                            else{
                                showDialogBox("Error",exception?.message.toString())
                            }
                        }
                        findNavController().popBackStack(R.id.cartFragment,true)
                    }
                    else{
                        showDialogBox("Internet Error","Internet connection error")
                    }

                }
            }
        }
    }

    private fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addressText = ""
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[ZERO]
                val sb = StringBuilder()

                for (i in 0 until address.maxAddressLineIndex + 1) {
                    sb.append(address.getAddressLine(i))
                    if (i < address.maxAddressLineIndex) {
                        sb.append(", ")
                    }
                }

                addressText = sb.toString()
            }
        } catch (e: IOException) {
            showDialogBox("Address Exception",e.message?:"")
        }

        return addressText
    }


}