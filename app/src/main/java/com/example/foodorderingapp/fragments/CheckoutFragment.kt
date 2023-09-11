package com.example.foodorderingapp.fragments


import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.foodorderingapp.Utils.Constants.CASH_ON_DELIVERY
import com.example.foodorderingapp.Utils.Constants.LATITUDE
import com.example.foodorderingapp.Utils.Constants.LOCATION_DATA
import com.example.foodorderingapp.Utils.Constants.LONGITUDE
import com.example.foodorderingapp.Utils.Constants.MY_LATITUDE
import com.example.foodorderingapp.Utils.Constants.MY_LONGITUDE
import com.example.foodorderingapp.Utils.Helper.getAddressFromLocation
import com.example.foodorderingapp.Utils.Extensions.isValidEmail
import com.example.foodorderingapp.Utils.Extensions.showError
import com.example.foodorderingapp.Utils.Helper.showAlertDialog
import com.example.foodorderingapp.Utils.NetworkUtils.Companion.checkForInternet
import com.example.foodorderingapp.activities.OrderTrackingActivity
import com.example.foodorderingapp.databinding.FragmentCheckoutBinding
import com.example.foodorderingapp.models.*
import com.example.foodorderingapp.viewModels.CheckoutViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
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
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        navController = findNavController()
        checkoutViewModel = ViewModelProvider(this)[CheckoutViewModel::class.java]

        setupViews()
        handleLocationResult()

        return binding.root
    }

    private fun setupViews() {
        binding.apply {
            etName.setText(auth.currentUser?.displayName)
            etEmail.setText(auth.currentUser?.email)
            etMobileNum.setText(auth.currentUser?.phoneNumber)

            etAddress.isFocusableInTouchMode = false
            etAddress.setOnClickListener {
                navigateToMapsFragment()
            }

            btnPlaceOrder.setOnClickListener {
                placeOrder()
            }
        }
    }

    private fun handleLocationResult() {
        val navBackStackEntry = navController.getBackStackEntry(R.id.checkoutFragment)
        val locationData = navBackStackEntry.savedStateHandle.getLiveData<Bundle>(LOCATION_DATA)
        locationData.observe(viewLifecycleOwner) { data ->
            val latitude = data.getDouble(LATITUDE)
            val longitude = data.getDouble(LONGITUDE)

            setAddressFromLocation(latitude, longitude)
            updateDistance(latitude, longitude)
        }
    }

    private fun setAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        binding.etAddress.setText(getAddressFromLocation(geocoder, latitude, longitude))
    }

    private fun updateDistance(latitude: Double, longitude: Double) {
        checkoutViewModel.latitude = latitude
        checkoutViewModel.longitude = longitude

        val chosenLatLng = LatLng(latitude, longitude)
        val restaurantLatLng = LatLng(MY_LATITUDE, MY_LONGITUDE)
        checkoutViewModel.distance = checkoutViewModel.calculateDistanceInKm(restaurantLatLng, chosenLatLng)

        if (!checkoutViewModel.validDistance()) {
            showAlertDialog(
                context = WeakReference(requireContext()),
                title = getString(R.string.area_error),
                message = getString(R.string.delivery_area_error)
            )
        }
    }

    private fun navigateToMapsFragment() {
        navController.navigate(R.id.action_checkoutFragment_to_mapsFragment)
    }

    private fun placeOrder() {
        binding.apply {
            when {
                etName.text.isNullOrBlank() -> {
                    etName.showError(getString(R.string.name_required_error))
                }
                etEmail.text.isNullOrBlank() -> {
                    etEmail.showError(getString(R.string.email_required_error))
                }
                !etEmail.text.toString().isValidEmail() -> {
                    etEmail.showError(getString(R.string.email_valid_error))
                }
                etMobileNum.text.isNullOrBlank() -> {
                    etMobileNum.showError(getString(R.string.mobile_required_error))
                }
                etAddress.text.isNullOrBlank() -> {
                    etAddress.isFocusableInTouchMode = true
                    etAddress.showError(getString(R.string.address_required_error))
                    etAddress.isFocusableInTouchMode = false
                }
//                !checkoutViewModel.validDistance() -> {
//                    showAlertDialog(WeakReference(requireContext()), getString(R.string.area_error), getString(R.string.delivery_area_error))
//                }
                else -> {
                    val customerInfo = createCustomerInfo()
                    val deliveryInfo = createDeliveryInfo()
                    val cartItemList = getCartItemList()
                    val totalAmount = cartItemList.sumOf { it.totalAmount }
                    val amounts = retrieveAmounts()
                    amounts.updateTotalItemAmount(totalAmount)

                    val order = createOrder(customerInfo, deliveryInfo, cartItemList, amounts)
                    if (checkForInternet(requireActivity().applicationContext)) {
                        checkoutViewModel.placeOrder(order) { success, exception ->
                            if (success) {

                                showAlertDialog(WeakReference(requireContext()),
                                    getString(R.string.information),
                                    getString(R.string.order_confirmed),
                                    positiveListener = object : DialogInterface.OnClickListener{
                                        override fun onClick(dialog: DialogInterface?, which: Int) {
                                            navigateToOrderTracking()
                                        }

                                    },
                                    onDismissListener = object : DialogInterface.OnDismissListener{
                                        override fun onDismiss(dialog: DialogInterface?) {
                                            navigateToOrderTracking()
                                        }

                                    }
                                )

                            } else {
                                showAlertDialog(WeakReference(requireContext()),getString(R.string.error), exception?.message.toString())
                            }
                        }
                    } else {
                        showAlertDialog(WeakReference(requireContext()),getString(R.string.information), getString(R.string.internet_error_msg))
                    }
                }
            }
        }
    }

    private fun navigateToOrderTracking() {
        requireActivity().startActivity(
            Intent(requireActivity(), OrderTrackingActivity::class.java)
        )
        findNavController().popBackStack(R.id.cartFragment, true)
    }

    private fun createCustomerInfo(): CustomerInfo {
        return CustomerInfo(
            name = binding.etName.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            phoneNumner = binding.etMobileNum.text.toString().trim(),
            id= auth.currentUser?.uid
        )
    }

    private fun createDeliveryInfo(): DeliveryInfo {
        return DeliveryInfo(
            locationLatitude = checkoutViewModel.latitude,
            locationLongitude = checkoutViewModel.longitude
        )
    }

    private fun getCartItemList(): List<CartItem> {
        val cartJson = sharedPreferences.getString(Constants.CART, null)
        return if (!cartJson.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<CartItem>?>() {}.type
            Gson().fromJson<MutableList<CartItem>>(cartJson, type)
        } else {
            emptyList()
        }
    }

    private fun retrieveAmounts(): Amounts {
        val args: CheckoutFragmentArgs by navArgs()
        return args.amounts
    }

    private fun createOrder(
        customerInfo: CustomerInfo,
        deliveryInfo: DeliveryInfo,
        cartItemList: List<CartItem>,
        amounts: Amounts
    ): Order {
        return Order(
            customerInfo = customerInfo,
            customerDeliveryInfo = deliveryInfo,
            paymentMethod = CASH_ON_DELIVERY,
            cartItemList = cartItemList,
            amounts = amounts,
            orderTracking = OrderTracking().apply {
                proceedOrder()
            }
        )
    }

}
