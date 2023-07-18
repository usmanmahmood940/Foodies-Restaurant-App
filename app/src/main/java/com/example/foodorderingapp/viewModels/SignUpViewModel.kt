package com.example.foodorderingapp.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.foodorderingapp.Listeners.CustomSuccessFailureListener
import com.example.foodorderingapp.Repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userRepository: UserRepository
):ViewModel(){

    fun signUp(
        email: String,
        password: String,
        name: String,
        mobileNumner: String,
        imageUri: Uri,
        listener: CustomSuccessFailureListener
    ){
        userRepository.signUp(email,password,name,mobileNumner,imageUri,listener)
    }

}
