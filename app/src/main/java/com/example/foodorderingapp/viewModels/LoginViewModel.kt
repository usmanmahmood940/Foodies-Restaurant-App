package com.example.foodorderingapp.viewModels

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.example.foodorderingapp.Listeners.CustomSuccessFailureListener
import com.example.foodorderingapp.Repositories.UserRepository
import com.example.foodorderingapp.Utils.Constants.ROLE_REFRENCE
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel  @Inject constructor(
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferences
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

    fun saveRoleInPref(role:String?) {
        sharedPreferences.edit{
            putString(ROLE_REFRENCE,role)
            apply()
        }
    }

}