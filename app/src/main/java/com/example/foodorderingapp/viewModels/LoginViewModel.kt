package com.example.foodorderingapp.viewModels

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodorderingapp.Listeners.CustomSuccessFailureListener
import com.example.foodorderingapp.Repositories.UserRepository
import com.example.foodorderingapp.Utils.Constants.ROLE_REFRENCE
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel  @Inject constructor(
    private val userRepository: UserRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    fun firebaseAuthWithCredentials(credentials: AuthCredential, listener: CustomSuccessFailureListener){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.firebaseAuthWithCredentials(credentials, listener)
        }
    }

    fun firebaseAuthWithEmailPass(email: String,password:String,listener: CustomSuccessFailureListener){
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.firebaseAuthWithEmailPass(email, password, listener)
        }
    }

    fun checkRole(uid:String,callback:(String?) -> Unit){
        viewModelScope.launch {
            callback(userRepository.checkRole(uid))
        }
    }

    fun saveRoleInPref(role:String?) {
        sharedPreferences.edit{
            putString(ROLE_REFRENCE,role)
            apply()
        }
    }

}