package com.skapps.android.csicodathonproject.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.data.models.Product
import com.skapps.android.csicodathonproject.data.models.Review
import com.skapps.android.csicodathonproject.databinding.ListItemProductBinding
import com.skapps.android.csicodathonproject.databinding.ListItemReviewBinding

/**
 * Created by Syed Umair on 18/12/2020.
 */
class ReviewsListAdapter (
    private val context: Context,
    private val reviewList: ArrayList<Review>,
    private val listener: ItemAdapterListener
) : RecyclerView.Adapter<ReviewsListAdapter.ReviewsViewHolder>() {

    interface ItemAdapterListener {
        fun onItemClicked(review: Review)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {
        val binding = ListItemReviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class ReviewsViewHolder(private val binding: ListItemReviewBinding):
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val item = reviewList[position]
                    listener.onItemClicked(item)
                }
            }
        }


        fun bind(listIndex: Int) {
            val currentReview = reviewList[listIndex]
            binding.apply {
                name.text = currentReview.name
                title.text = currentReview.heading
                descriptionTextView.text = currentReview.description
                ratingBar.rating = currentReview.rating.toFloat()
            }
        }
    }
}