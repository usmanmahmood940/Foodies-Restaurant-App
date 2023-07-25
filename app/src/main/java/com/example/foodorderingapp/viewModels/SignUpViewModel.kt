package com.example.foodorderingapp.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.Listeners.CustomSuccessFailureListener
import com.example.foodorderingapp.Repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
        viewModelScope.launch {
            userRepository.signUp(email, password, name, mobileNumner, imageUri, listener)
        }
    }

}
