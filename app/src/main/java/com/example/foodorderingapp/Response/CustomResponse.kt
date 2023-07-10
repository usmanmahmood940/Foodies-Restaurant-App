package com.example.foodorderingapp.Response

sealed class CustomResponse<T>(val  data: T? = null, val errorMessage:String? =null)
{                                    ;
    class Loading<T> : CustomResponse<T>()
    class Success<T>(data: T? = null) : CustomResponse<T>(data = data)
    class Error<T>(errorMessage: String) : CustomResponse<T>(errorMessage = errorMessage)
}
