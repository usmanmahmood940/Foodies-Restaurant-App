package com.example.foodorderingapp.Repositories

import android.net.Uri
import com.example.foodorderingapp.Listeners.CustomSuccessFailureListener
import com.example.foodorderingapp.Utils.Constants.ROLE_REFRENCE
import com.example.foodorderingapp.Utils.Constants.ROLE_USER
import com.example.foodorderingapp.Utils.Constants.USER_REFRENCE
import com.example.foodorderingapp.models.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firabaseDbRef: DatabaseReference,
) {

    suspend fun firebaseAuthWithEmailPass(
        email: String,
        password: String,
        listener: CustomSuccessFailureListener,
    ) {
        try {
            val signInAuth = auth.signInWithEmailAndPassword(email, password).await()
            signInAuth.user?.apply {
                if (isEmailVerified || true) {

                    runOnMain({ listener.onSuccess() })

                } else {
                    sendEmailVerification()
                    runOnMain {
                        listener.onFailure("Please Verified Your email")
                    }
                    auth.signOut()
                }
            }
        } catch (exception: Exception) {
            runOnMain {
                listener.onFailure(exception.message)
            }
        }
    }

    suspend fun firebaseAuthWithCredentials(
        credentials: AuthCredential,
        listener: CustomSuccessFailureListener,
    ) {
        try {
            val signInTask = auth.signInWithCredential(credentials).await()
            if (!checkUserExist()) {
                signInTask.user?.uid?.let { uid ->
                    val user = User(
                        id = uid,
                        role = ROLE_USER,
                        mobileNumber = auth.currentUser?.phoneNumber
                    )
                    saveUser(user)
                }
            }
            runOnMain({ listener.onSuccess() })
        } catch (exception: Exception) {
            runOnMain { listener.onFailure(exception.message) }
        }
    }

    private suspend fun checkUserExist(): Boolean {
        auth.currentUser?.uid?.let { uid ->
            val user = firabaseDbRef.child(USER_REFRENCE).child(uid).get().await()
            return user.exists()
        }
        return false
    }

    suspend fun checkRole(uid: String): String? {
        val role = firabaseDbRef.child(USER_REFRENCE).child(uid).child(ROLE_REFRENCE).get().await()
        if (role.exists()) {
            return role.getValue(String::class.java)
        }
        return null
    }

    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        mobileNumber: String,
        imageUri: Uri,
        listener: CustomSuccessFailureListener,
    ) {
        try {
            val task = auth.createUserWithEmailAndPassword(email, password).await()
            val displayNameUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(imageUri)
                .build()

            task.user?.apply {
                sendEmailVerification()
                updateProfile(displayNameUpdate)
                val newUser = User(uid, ROLE_USER, mobileNumber)
                saveUser(newUser)
                auth.signOut()
                runOnMain({ listener.onSuccess() })

            }
        } catch (e: Exception) {
            runOnMain {
                listener.onFailure(e.message)
            }
        }
    }

    private fun saveUser(user: User) {
        firabaseDbRef.child(USER_REFRENCE).child(user.id).setValue(user)
    }

    private suspend fun runOnMain(action: () -> Unit) {
        withContext(Dispatchers.Main) {
            action.invoke()
        }

    }

}