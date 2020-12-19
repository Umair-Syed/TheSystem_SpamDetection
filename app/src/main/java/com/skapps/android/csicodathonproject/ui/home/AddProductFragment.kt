package com.skapps.android.csicodathonproject.ui.home

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.data.models.Product
import com.skapps.android.csicodathonproject.databinding.FragmentAddProductBinding
import com.skapps.android.csicodathonproject.util.FIREBASE_STORAGE_FOLDER_NAME_PRODUCTS
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_PRODUCTS

private const val TAG = "AddProductFragment"
const val REQUEST_CODE_IMAGE = 102
class AddProductFragment : Fragment(R.layout.fragment_add_product) {
    private lateinit var binding : FragmentAddProductBinding

    private val db = FirebaseFirestore.getInstance()
    private val productsCollectionRef = db.collection(KEY_COLLECTION_PRODUCTS)
    private var user = FirebaseAuth.getInstance().currentUser
    private var imageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddProductBinding.bind(view)

        if (activity != null) {
            (activity as MainActivity).supportActionBar?.title = "Add Product"
        }

        binding.selectImageBtn.setOnClickListener {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                startActivityForResult(
                    this,
                    REQUEST_CODE_IMAGE
                )
            }
        }

        binding.addBtn.setOnClickListener {
            if(validateForm()){
                binding.progressBarAdd.visibility = View.VISIBLE
                binding.addBtn.text = ""

                val name = binding.title.editText?.text.toString()
                val description = binding.description.editText?.text.toString()
                val price = binding.price.editText?.text.toString()

                if(imageUri != null){
                    val storageRef = FirebaseStorage.getInstance().reference
                        .child(FIREBASE_STORAGE_FOLDER_NAME_PRODUCTS)
                        .child("${name}_${Timestamp.now()}")

                    val uploadTask = storageRef.putFile(imageUri!!)
                    uploadTask.continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        storageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        addToFirestore(name, description, price, task.result.toString())
                    }.addOnFailureListener {
                        Log.d(TAG, "uploadNoteToStorage: ${it.message}")
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }else{
                    addToFirestore(name, description, price, "#")
                }
            }
        }
    }

    private fun addToFirestore(name: String, description: String, price: String, imageUrl: String) {
        val product =
            user?.uid?.let { it1 -> Product("", it1, name, description, imageUrl, price, 0.0, 0) }
        if (product != null) {
            productsCollectionRef.document().set(product)
                .addOnSuccessListener {
                    Snackbar.make(requireView(), "Product Added", Snackbar.LENGTH_SHORT).show()
                    binding.progressBarAdd.visibility = View.GONE
                    binding.addBtn.text = "ADD"
                }
                .addOnFailureListener {
                    Snackbar.make(
                        requireView(),
                        it.localizedMessage ?: "Something went wrong!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    binding.progressBarAdd.visibility = View.GONE
                    binding.addBtn.text = "ADD"
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

        if(binding.price.editText?.text.toString().isBlank()){
            binding.price.isErrorEnabled = true
            binding.price.error = "Please fill this field"
            return false
        }else {
            binding.price.isErrorEnabled = false
            binding.price.error = null
        }

        if(binding.description.editText?.text?.isEmpty()!!){
            Snackbar.make(requireView(), "Please write some description.", Snackbar.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_CODE_IMAGE){
            if(resultCode == Activity.RESULT_OK && data != null) {
                imageUri = data.data
                if(imageUri != null){
                    Glide.with(requireContext())
                        .load(imageUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_baseline_image_24)
                        .into(binding.image)
                }
            }
        }
    }

}