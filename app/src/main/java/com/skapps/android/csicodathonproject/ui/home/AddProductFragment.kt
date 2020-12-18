package com.skapps.android.csicodathonproject.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.data.Product
import com.skapps.android.csicodathonproject.databinding.FragmentAddProductBinding
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_PRODUCTS
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_USERS


class AddProductFragment : Fragment(R.layout.fragment_add_product) {
    private lateinit var binding : FragmentAddProductBinding

    private val db = FirebaseFirestore.getInstance()
    private val productsCollectionRef = db.collection(KEY_COLLECTION_PRODUCTS)
    private var user = FirebaseAuth.getInstance().currentUser

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddProductBinding.bind(view)

        if (activity != null) {
            (activity as MainActivity).supportActionBar?.title = "Add Product"
        }

        binding.addBtn.setOnClickListener {
            if(validateForm()){
                binding.progressBarAdd.visibility = View.VISIBLE
                binding.addBtn.text = ""

                val name = binding.title.editText?.text.toString()
                val description = binding.description.editText?.text.toString()
                val product = user?.uid?.let { it1 -> Product(it1, name, description, "#", 0.0) }
                if (product != null) {
                    productsCollectionRef.document().set(product)
                        .addOnSuccessListener {
                            Snackbar.make(requireView(), "Product Added", Snackbar.LENGTH_SHORT).show()
                            binding.progressBarAdd.visibility = View.GONE
                            binding.addBtn.text = "ADD"
                        }
                        .addOnFailureListener {
                            Snackbar.make(requireView(), it.localizedMessage ?: "Something went wrong!", Snackbar.LENGTH_SHORT).show()
                            binding.progressBarAdd.visibility = View.GONE
                            binding.addBtn.text = "ADD"
                        }

                }
            }
        }
    }

    private fun validateForm(): Boolean {
        if(binding.title.editText?.text.toString().isBlank()){
            binding.title.isErrorEnabled = true
            binding.title.error = "Please fill this field"
            return false
        }else {
            binding.title.isErrorEnabled = false
            binding.title.error = null
        }

        if(binding.description.editText?.text?.isEmpty()!!){
            Snackbar.make(requireView(), "Please write some description.", Snackbar.LENGTH_SHORT).show()
            return false
        }

        return true
    }

}