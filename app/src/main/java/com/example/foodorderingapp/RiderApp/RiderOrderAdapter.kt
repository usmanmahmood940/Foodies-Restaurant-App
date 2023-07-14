package com.example.foodorderingapp.RiderApp

import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.Utils.Helper.getAddressFromLocation
import com.example.foodorderingapp.models.Order
import java.util.*

class RiderOrderAdapter(
    private var orderList: List<Order> = emptyList(),
    private val listener:RiderOrderConfirmClickListener,
) : RecyclerView.Adapter<RiderOrderAdapter.ViewHolder>() {

    fun setList(list: List<Order>) {
        orderList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rider_order, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.apply {
            val order = orderList[position]

            tvName.text = order.customerInfo.name
            tvMobileNumber.text = order.customerInfo.phoneNumner
            val geocoder = Geocoder(tvOrderLocation.context, Locale.getDefault())
            val address = getAddressFromLocation(
                geocoder,
                order.customerDeliveryInfo.locationLatitude,
                order.customerDeliveryInfo.locationLongitude
            )
            tvOrderLocation.text = address


            btnConfirm.setOnClickListener {
                listener.onConfirm(order)
            }


        }

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val tvName: TextView = ItemView.findViewById(R.id.tv_person_name)
        val tvMobileNumber: TextView = ItemView.findViewById(R.id.tv_mobile_number)
        val tvOrderLocation: TextView = ItemView.findViewById(R.id.tv_order_location)
        val btnConfirm: Button = ItemView.findViewById(R.id.btn_confirm)


    }
}