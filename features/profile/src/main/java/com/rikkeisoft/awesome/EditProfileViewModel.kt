package com.rikkeisoft.awesome

import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.core.base.BaseViewModel
import com.project.core.model.firebase.User
import com.project.core.utils.SingleLiveEvent
import com.project.core.utils.toDate
import com.rikkeisoft.awesome.profile.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : BaseViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    val updateSuccess = SingleLiveEvent<Boolean>()

    fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users").document(uid).get().await()
                val user = snapshot.toObject(User::class.java)
                _userProfile.value = user
            } catch (e: Exception) {
                handleError(e, null)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateUserProfile(username: String, phoneNumber: String, birthOfDay: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            isLoading.value = true
            val firstName = username.split("\\s+".toRegex()).last()
            try {
                val updates = mutableMapOf<String, Any>(
                    "username" to username,
                    "phoneNumber" to phoneNumber,
                    "firstName" to firstName
                )

                // Parse birthOfDay string to Timestamp
                if (birthOfDay.isNotEmpty()) {
                    val date = birthOfDay.toDate("dd/MM/yyyy")
                    if (date != null) {
                        if (date.after(java.util.Date())) {
                            messageError.value = R.string.error_future_date
                            return@launch
                        }
                        updates["birthOfDay"] = Timestamp(date)
                    } else {
                        messageError.value = R.string.error_invalid_date_format
                        return@launch
                    }
                }

                db.collection("users").document(uid).update(updates).await()
                updateSuccess.value = true
            } catch (e: Exception) {
                handleError(e, null)
            } finally {
                isLoading.value = false
            }
        }
    }
}