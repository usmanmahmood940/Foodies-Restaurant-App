package com.example.foodorderingapp.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ui.IconGenerator

class MapsFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var binding: FragmentMapsBinding
    private lateinit var mapViewModel: MapViewModel

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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val previousFragment = findNavController().previousBackStackEntry
                if (previousFragment != null) {
                    val data = Bundle().apply {
                        putDouble(LATITUDE, mapViewModel.pinLocation?.latitude ?: 0.0)
                        putDouble(LONGITUDE, mapViewModel.pinLocation?.longitude ?: 0.0)
                    }
                    previousFragment.savedStateHandle.set(LOCATION_DATA, data)
                    findNavController().popBackStack()
                    val activity = requireActivity() as MainActivity
                    activity.binding.coordinatorLayout.visibility = View.VISIBLE
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        binding.btnUseLocation.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        val activity = requireActivity() as MainActivity
        activity.binding.coordinatorLayout.visibility = View.GONE
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        val iconGenerator = IconGenerator(context)
        iconGenerator.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_pin))
        val customIconBitmap = iconGenerator.makeIcon()
        val customIcon = BitmapDescriptorFactory.fromBitmap(customIconBitmap)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        mapViewModel.pinLocation = LatLng(location.latitude, location.longitude)
                        mapViewModel.pinLocation.apply {
                            googleMap.addMarker(MarkerOptions().position(this).icon(customIcon))

                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    this,
                                    15f
                                )
                            )
                        }
                    }
                }
        } else {
            getPermissions()
        }


        googleMap.setOnMapClickListener { point ->
            // Handle map click event and update pin marker
            googleMap.clear() // Remove previous markers
            mapViewModel.pinLocation = point
            googleMap.addMarker(MarkerOptions().position(point).icon(customIcon))
            // Do something with the new location (point.latitude, point.longitude)
        }
    }

    private fun getPermissions() {
        requestMultiplePermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    val requestMultiplePermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions.all { it.value }
            if (isGranted) {

            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            }

        }

    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }
}