package com.beer.android.firebaseallfeatureproject

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.beer.android.firebaseallfeatureproject.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var mAuth: FirebaseAuth? = null
    var mVerificationId: String? = null
    var mResendToken: PhoneAuthProvider.ForceResendingToken? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        binding.authBtn.setOnClickListener {
            authenticationWithPhoneNumber(binding.authNumberEt.text.toString())
        }

        if(getCurrentUserEmail().isNullOrEmpty()){
            openChatActivity()
        }
    }

    private fun authenticationWithPhoneNumber(number: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        signInWithPhoneAuthCredential(credential)
                    }

                    override fun onVerificationFailed(e: FirebaseException) {
                        if (e is FirebaseAuthInvalidCredentialsException) {
                            binding.authNumberTil.setError("Invalid phone number.")
                            // [END_EXCLUDE]
                        } else if (e is FirebaseTooManyRequestsException) {
                            Snackbar.make(findViewById<View>(android.R.id.content), "Quota exceeded.",
                                    Snackbar.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCodeSent(verificationId: String?,
                                            token: PhoneAuthProvider.ForceResendingToken?) {
                        mVerificationId = verificationId
                        mResendToken = token
                    }
                }
        )
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth?.signInWithCredential(credential)!!.addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {
                        val user = task.result.user
                        openChatActivity()
                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            binding.authNumberTil.setError("Invalid code.")
                        }
                    }
                })
    }

    private fun openChatActivity(){
        val intent = Intent(this,ChatActivity::class.java)
        startActivity(intent)
    }

    private fun getCurrentUserEmail(): String? {
        val firebaseUser = mAuth?.currentUser
        return if (firebaseUser != null) {
            firebaseUser.email
        } else ""
    }

}
