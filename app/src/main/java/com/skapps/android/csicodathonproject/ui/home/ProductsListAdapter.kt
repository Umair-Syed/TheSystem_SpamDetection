package com.skapps.android.csicodathonproject.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.data.models.Product
import com.skapps.android.csicodathonproject.databinding.ListItemProductBinding

/**
 * Created by Syed Umair on 18/12/2020.
 */
class ProductsListAdapter(
    private val context: Context,
    private val productList: ArrayList<Product>,
    private val listener: ItemAdapterListener
) : RecyclerView.Adapter<ProductsListAdapter.ProductsViewHolder>() {


    interface ItemAdapterListener {
        fun onItemClicked(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val binding = ListItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ProductsViewHolder(private val binding: ListItemProductBinding):
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val item = productList[position]
                    listener.onItemClicked(item)
                }
            }
        }


        fun bind(listIndex: Int) {
            val currentProduct = productList[listIndex]
            binding.apply {
                    title.text = currentProduct.name
                    description.text = currentProduct.description
                    title.text = currentProduct.name
                    ratingBar.rating = currentProduct.rating.toFloat()
                    Glide.with(context).load(currentProduct.imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .into(imageHolder)
            }
        }
    }
}