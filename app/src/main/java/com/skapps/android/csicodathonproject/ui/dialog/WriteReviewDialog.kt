package com.skapps.android.csicodathonproject.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.data.models.Product
import com.skapps.android.csicodathonproject.data.models.Review
import com.skapps.android.csicodathonproject.util.KEY_COLLECTION_PRODUCTS
import com.skapps.android.csicodathonproject.util.KEY_SUB_COLLECTION_REVIEWS

/**
 * Created by Syed Umair on 18/12/2020.
 */
class WriteReviewDialog(private val product: Product) : DialogFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_write_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val submitBtn = view.findViewById<Button>(R.id.submit_button)
        val cancelBtn = view.findViewById<Button>(R.id.cancel_btn)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_review_submit)
        val heading = view.findViewById<EditText>(R.id.heading)
        val description = view.findViewById<EditText>(R.id.description)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)

        val db = FirebaseFirestore.getInstance()
        val reviewsCollection = db.collection(KEY_COLLECTION_PRODUCTS).document(product.id)
            .collection(KEY_SUB_COLLECTION_REVIEWS)
        val user = FirebaseAuth.getInstance().currentUser

        submitBtn.setOnClickListener {
            if(validateForm(heading, description)){
                progressBar.visibility = View.VISIBLE
                submitBtn.text = ""
                val head = heading.text.toString()
                val des = description.text.toString()
                val rating = ratingBar.rating
                val review = user?.uid?.let { it1 -> Review("",it1, user.displayName ?:"", product.id, head, des, rating.toDouble()) }
                if (review != null) {
                    reviewsCollection.add(review)
                        .addOnSuccessListener {
                            // need to to atomically, not done yet
                            val newRating = ((product.rating * product.ratingCount) + rating) / (product.ratingCount+1)
                            db.collection(KEY_COLLECTION_PRODUCTS).document(product.id)
                                .update(mapOf(
                                    "rating" to newRating,
                                    "ratingCount" to product.ratingCount + 1
                                )).addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Review submitted", Toast.LENGTH_SHORT).show()
                                    progressBar.visibility = View.GONE
                                    submitBtn.text = "ADD"
                                    dismiss()
                                }.addOnFailureListener {
                                    Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
                                    progressBar.visibility = View.GONE
                                    submitBtn.text = "ADD"
                                }

                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                            submitBtn.text = "ADD"
                        }

                }
            }
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun validateForm(heading: EditText, description: EditText): Boolean {
        if(heading.text.toString().isEmpty()){
            Toast.makeText(requireContext(), "Heading is empty!", Toast.LENGTH_SHORT).show()
            return false
        }

        if(description.text.toString().isEmpty()){
            Toast.makeText(requireContext(), "Write some review", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


}
