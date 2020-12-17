package com.skapps.android.csicodathonproject.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.databinding.FragmentHomeBinding


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        if (activity != null) {
            (activity as MainActivity).supportActionBar?.title = "Products"
        }

    }
}