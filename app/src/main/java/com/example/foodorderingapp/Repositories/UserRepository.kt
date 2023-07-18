package com.example.foodorderingapp.Repositories

import com.example.foodorderingapp.Listeners.CustomSuccessFailureListener
import com.example.foodorderingapp.Utils.Constants.MOBILE_NUMBER_REF
import com.example.foodorderingapp.Utils.Constants.ROLE_REFRENCE
import com.example.foodorderingapp.Utils.Constants.ROLE_USER
import com.example.foodorderingapp.Utils.Constants.USER_REFRENCE
import com.example.foodorderingapp.models.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firabaseDbRef: DatabaseReference,
) {

    fun firebaseAuthWithEmailPass(email:String, password:String, listener: CustomSuccessFailureListener){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    listener.onSuccess()
                } else {
                    listener.onFailure(task.exception?.message)

                }
            }
    }
    fun firebaseAuthWithCredentials(
        credentials: AuthCredential,
        listener: CustomSuccessFailureListener,
    ) {
        auth.signInWithCredential(credentials)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    checkUserExist(){
                        if(!it){
                            saveUserInRealtimeDatabase(listener)
                        }
                        else{
                            listener.onSuccess()
                        }
                    }

                } else {
                    listener.onFailure(task.exception?.message)
                }
            }

    }

    private fun checkUserExist(callback :(Boolean) -> Unit) {
        auth.currentUser?.uid?.let { uid ->
            firabaseDbRef.child(USER_REFRENCE).child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()){
                            callback(true)
                        }
                        else{
                            callback(false)
                        }

                    }
                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        }
    }

    private fun saveUserInRealtimeDatabase(listener:CustomSuccessFailureListener) {
        val uid = auth.currentUser?.uid
        val user = User(
            id = uid!!,
            role = ROLE_USER,
            mobileNumber = auth.currentUser?.phoneNumber
        )
        firabaseDbRef.child(USER_REFRENCE).child(uid)
            .setValue(user)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    listener.onSuccess()
                }
                else{
                    listener.onFailure(task.exception?.message)
                }

            }
    }

    fun checkRole(uid:String,callbackRole: (String?) -> Unit){
        firabaseDbRef.child(USER_REFRENCE).child(uid).child(ROLE_REFRENCE)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        callbackRole(snapshot.getValue(String::class.java))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }



    fun updatePhoneNumber(mobileNumber: String,uid:String){
        firabaseDbRef.child(USER_REFRENCE).child(uid).child(MOBILE_NUMBER_REF)
            .setValue(mobileNumber)
    }
    fun getPhoneNumber(callbackMobileNum: (String?) -> Unit){
        auth.currentUser?.uid?.let{
            firabaseDbRef.child(USER_REFRENCE).child(it).child(MOBILE_NUMBER_REF)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            callbackMobileNum(snapshot.getValue(String::class.java))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })  
        }
      
    }
}