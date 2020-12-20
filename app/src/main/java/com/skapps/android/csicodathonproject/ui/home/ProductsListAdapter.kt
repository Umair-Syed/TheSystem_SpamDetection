package com.skapps.android.csicodathonproject.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.skapps.android.csicodathonproject.R
import com.skapps.android.csicodathonproject.data.models.Product
import com.skapps.android.csicodathonproject.databinding.ListItemProductBinding
import com.skapps.android.csicodathonproject.ui.login.DATA_STORE_KEY
import com.skapps.android.csicodathonproject.ui.login.PREF_KEY_IS_ADMIN
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

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
        fun onRemoveClicked(product: Product)
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

            // get isAdmin from data store
            val dataStore: DataStore<Preferences> = context.createDataStore(
                name = DATA_STORE_KEY
            )
            val isAdminKey = preferencesKey<Boolean>(PREF_KEY_IS_ADMIN)
            val isAdminFlow: Flow<Boolean> = dataStore.data.map {
                it[isAdminKey] ?: false
            }

            GlobalScope.launch {
                isAdminFlow.collect {isAdmin ->
                    try {
                        if (isAdmin){
                            binding.close.visibility = View.VISIBLE
                        }else{
                            binding.close.visibility = View.INVISIBLE
                        }
                    }catch (e: NullPointerException){
                        Log.e("ProductsListAdapter", e.message ?: "close button is null")
                    }

                }
            }

            binding.close.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val item = productList[position]
                    listener.onRemoveClicked(item)
                }
            }
        }


        fun bind(listIndex: Int) {
            val currentProduct = productList[listIndex]
            binding.apply {
                    title.text = currentProduct.name
                    description.text = currentProduct.description
                    title.text = currentProduct.name
                    price.text = "\u20b9 ${currentProduct.price}"
                    ratingBar.rating = currentProduct.rating.toFloat()
                    Glide.with(context).load(currentProduct.imageUrl)
                        .placeholder(R.drawable.ic_baseline_image_black)
                        .transition(DrawableTransitionOptions.withCrossFade(200))
                        .into(imageHolder)
            }
        }
    }
}