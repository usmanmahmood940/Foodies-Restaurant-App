package com.example.foodorderingapp.fragments

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.foodorderingapp.activities.MainActivity
import com.example.foodorderingapp.R
import com.example.foodorderingapp.Utils.Constants.LATITUDE
import com.example.foodorderingapp.Utils.Constants.LOCATION_DATA
import com.example.foodorderingapp.Utils.Constants.LONGITUDE
import com.example.foodorderingapp.databinding.FragmentMapsBinding
import com.example.foodorderingapp.viewModels.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator

class MapsFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var binding: FragmentMapsBinding
    private lateinit var mapViewModel: MapViewModel
    private var myGoogleMap: GoogleMap? = null

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions.all { it.value }
            if (isGranted) {
                updateMap()
            } else {
                Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
        setupBackButton()
        setupUseLocationButton()
        (requireActivity() as MainActivity).hideShowBottomNav()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap ->
            myGoogleMap = googleMap
            updateMap()
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private fun setupBackButton() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val previousFragment = findNavController().previousBackStackEntry
                if (previousFragment != null) {
                    val data = Bundle().apply {
                        mapViewModel.pinLocation.apply {
                            putDouble(LATITUDE, latitude)
                            putDouble(LONGITUDE, longitude)
                        }
                    }
                    previousFragment.savedStateHandle.set(LOCATION_DATA, data)
                    findNavController().popBackStack()
                    (requireActivity() as MainActivity).hideShowBottomNav()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }

    private fun setupUseLocationButton() {
        binding.btnUseLocation.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    private fun updateMap() {
        val customIcon = getCustomMapIcon()
        myGoogleMap?.apply {
            getCurrentLocation { locationLatLng ->
                mapViewModel.pinLocation = locationLatLng
                mapViewModel.pinLocation.apply {
                    addMarker(MarkerOptions().position(this).icon(customIcon))
                    moveCamera(CameraUpdateFactory.newLatLngZoom(this, 15f))
                }
            }

            setOnMapClickListener { point ->
                clear()
                mapViewModel.pinLocation = point
                addMarker(MarkerOptions().position(point).icon(customIcon))
            }
        }
    }

    private fun getCurrentLocation(callback: (locationLatLng: LatLng) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        callback(
                            LatLng(location.latitude, location.longitude)
                        )

                    }
                }
        }
    }

    private fun requestLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestLocationPermissionLauncher.launch(permissions)
    }

    private fun getCustomMapIcon(): BitmapDescriptor {
        val iconGenerator = IconGenerator(context)
        iconGenerator.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_pin))
        return BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
    }

    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }

}
