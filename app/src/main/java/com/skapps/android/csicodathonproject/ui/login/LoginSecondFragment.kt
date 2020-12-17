package com.skapps.android.csicodathonproject.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.databinding.FragmentLoginFirstBinding
import com.skapps.android.csicodathonproject.databinding.FragmentLoginSecondBinding


class LoginSecondFragment : Fragment(R.layout.fragment_login_second) {

    private lateinit var binding: FragmentLoginSecondBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginSecondBinding.bind(view)


        binding.submitBtn.setOnClickListener {
            val ccode = binding.countryCodePicker.selectedCountryCode
            val phone = binding.phoneNumber.text.toString()

            if(ccode.isNotEmpty() && phone.isNotEmpty()){
                binding.phoneNumber.error = null
                val action =
                        LoginSecondFragmentDirections.actionLoginSecondFragmentToLoginThirdFragment(phone, ccode)
                findNavController().navigate(action)

            }else if(phone.isEmpty()){
                binding.phoneNumber.error = "Please enter your phone number."
            }
        }
    }
}