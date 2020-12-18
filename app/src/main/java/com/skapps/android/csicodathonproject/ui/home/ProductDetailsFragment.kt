package com.skapps.android.csicodathonproject.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.databinding.FragmentLoginThirdBinding
import com.skapps.android.csicodathonproject.databinding.FragmentProductDetailsBinding
import com.skapps.android.csicodathonproject.ui.login.LoginThirdFragmentArgs


class ProductDetailsFragment : Fragment(R.layout.fragment_product_details) {
    private lateinit var binding: FragmentProductDetailsBinding
    private val args by navArgs<ProductDetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductDetailsBinding.bind(view)

        if (activity != null) {
            (activity as MainActivity).supportActionBar?.title = args.product.name
        }

        binding.apply {
            title.text = args.product.name
            description.text = args.product.description
            ratingBar.rating = args.product.rating.toFloat()
            Glide.with(requireContext()).load(args.product.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(image)
        }

        binding.checkReviewsBtn.setOnClickListener {
            val action = ProductDetailsFragmentDirections.actionProductDetailsFragmentToReviewsFragment(args.product)
            findNavController().navigate(action)
        }

    }

}