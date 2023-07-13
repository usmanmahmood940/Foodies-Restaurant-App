package com.example.foodorderingapp.models

sealed class OrderStatus(
    val status: String = "",
    val driverInfo: DriverInfo? = null,
    val driverLocation: DriverLocation? = null
) {
    class OrderPlaced(status: String) : OrderStatus(status = status)
    class OrderProceed(status: String) : OrderStatus(status = status)
    class OrderInDelivery(status: String, driverInfo: DriverInfo?, driverLocation: DriverLocation) :
        OrderStatus(status = status, driverInfo = driverInfo, driverLocation = driverLocation)
    class OrderCompleted(status: String) : OrderStatus(status = status)
}
