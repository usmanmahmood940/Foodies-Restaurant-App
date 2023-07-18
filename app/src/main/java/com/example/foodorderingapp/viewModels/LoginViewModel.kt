package com.example.foodorderingapp.viewModels

import androidx.lifecycle.ViewModel
import com.example.foodorderingapp.Listeners.CustomSuccessFailureListener
import com.example.foodorderingapp.Repositories.UserRepository
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel  @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun firebaseAuthWithCredentials(credentials: AuthCredential, listener: CustomSuccessFailureListener,){
        userRepository.firebaseAuthWithCredentials(credentials,listener)
    }

    fun firebaseAuthWithEmailPass(email: String,password:String,listener: CustomSuccessFailureListener){
        userRepository.firebaseAuthWithEmailPass(email,password,listener)
    }

    fun checkRole(uid:String,callback:(String?) -> Unit){
        userRepository.checkRole(uid,callback)
    }

}