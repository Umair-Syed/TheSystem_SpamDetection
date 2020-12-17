package com.skapps.android.csicodathonproject.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.databinding.FragmentLoginThirdBinding
import java.util.concurrent.TimeUnit

private const val TAG = "LoginThirdFragment"
private const val KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress"
class LoginThirdFragment : Fragment(R.layout.fragment_login_third) {
    private lateinit var binding: FragmentLoginThirdBinding
    private val args by navArgs<LoginThirdFragmentArgs>()

    private var verificationId = ""
    private var resendTime = ""
    private var resendTimeout = false
    private var mVerificationInProgress = false
    private var codeReceived: String? = null
    private var timer: CountDownTimer? = null
    private var mResendToken: ForceResendingToken? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let { onViewStateRestored(it) }

        binding = FragmentLoginThirdBinding.bind(view)

        binding.phoneMsg.text = String.format("Please enter the verification code sent to +%s-%s", args.ccode, args.phone)

        binding.pinView.setOnClickListener {
            it.requestFocus()
        }

        //If user manually enter OTP and click verify.
        binding.verifyBtn.setOnClickListener {
            binding.progressBarOtp.visibility = View.VISIBLE
            val code = binding.pinView.text.toString()
            if(code.isNotEmpty() && code.length == 6){
                getCredentialAndSignIn(code)
            }
        }

        //If resentOTP is clicked after 60 seconds timeout.
        binding.resendOtp.setOnClickListener {
            if (resendTimeout) {
                mResendToken?.let { it1 -> resendVerificationCode(args.phone, it1) }
                resendTimeout = false
                setCountDownTimer()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!mVerificationInProgress)
            sendVerificationCodeToUser(args.phone)
        setCountDownTimer()
    }


    private fun sendVerificationCodeToUser(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber("+${args.ccode}${phoneNumber}")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(mCallbacks)
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        mVerificationInProgress = true
    }

    private fun resendVerificationCode(phoneNumber: String,
                                       token: ForceResendingToken) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber("+${args.ccode}${phoneNumber}")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(requireActivity())
                .setCallbacks(mCallbacks)
                .setForceResendingToken(token)
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val mCallbacks: OnVerificationStateChangedCallbacks = object : OnVerificationStateChangedCallbacks() {
        //automatic verification
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            binding.progressBarOtp.visibility = View.VISIBLE
            codeReceived = phoneAuthCredential.smsCode
            if (codeReceived != null) {
                binding.verifyBtn.isClickable = false
                binding.pinView.setText(codeReceived)
                mVerificationInProgress = false
                getCredentialAndSignIn(codeReceived!!)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(TAG, "onVerificationFailed: " + e.message)
            mVerificationInProgress = false
            if (e is FirebaseAuthInvalidCredentialsException) {
                Snackbar.make(requireView(), "Verification Failed. Make Sure Phone Number is Valid.",
                        Snackbar.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                Snackbar.make(requireView(), "OTP Quota exceeded. Try Again Later.",
                        Snackbar.LENGTH_SHORT).show()
            }
            binding.progressBarOtp.visibility = View.INVISIBLE
        }

        override fun onCodeSent(veriId: String, forceResendingToken: ForceResendingToken) {
            super.onCodeSent(veriId, forceResendingToken)
            Log.d(TAG, "onCodeSent: Verification Id $veriId")
            Snackbar.make(requireView(), "OTP has been sent to +${args.ccode} ${args.phone}",
                    Snackbar.LENGTH_SHORT).show()
            verificationId = veriId
            mResendToken = forceResendingToken
        }
    }


    private fun getCredentialAndSignIn(code: String) {
        Log.d(TAG, "getCredentialAndSignIn: Verification Id $verificationId  COde $code")
        if (verificationId.isNotEmpty()) {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInTheUser(credential)
        } else {
            Toast.makeText(requireActivity(), "OTP Not Sent. Try Again Later.", Toast.LENGTH_LONG).show()
            binding.progressBarOtp.visibility = View.INVISIBLE
        }
    }

    private fun signInTheUser(credential: PhoneAuthCredential) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val action = LoginThirdFragmentDirections.actionLoginThirdFragmentToLoginFourthFragment()
                        findNavController().navigate(action)
                    } else {
                        Toast.makeText(requireActivity(), "Something went wrong! try again", Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun setCountDownTimer() {
        binding.resendOtp.setTextColor(ContextCompat.getColor(requireContext(), R.color.lightGray))
        timer = object : CountDownTimer(59000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                resendTime = "Resend OTP in 00:" + millisUntilFinished / 1000
                binding.resendOtp.text = resendTime
            }

            override fun onFinish() {
                binding.resendOtp.text = "Resend OTP"
                binding.resendOtp.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                resendTimeout = true
            }
        }
        timer?.start()
    }

    override fun onPause() {
        super.onPause()
        timer!!.cancel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) try {
            mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS)
        } catch (e: NullPointerException) {
            Log.d(TAG, "onViewStateRestored: " + e.message)
        }
    }
}