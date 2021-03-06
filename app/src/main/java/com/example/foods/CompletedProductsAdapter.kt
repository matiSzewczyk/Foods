package com.example.foods

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults

class CompletedProductsAdapter(
    var products: RealmResults<CompletedProduct>,
    private val customInterface: RecyclerViewInterface
) : RecyclerView.Adapter<CompletedProductsAdapter.CompletedProductsHolder>() {

    inner class CompletedProductsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView           = itemView.findViewById(R.id.completed_product_name)
        val productTimestamp: TextView      = itemView.findViewById(R.id.completed_timestamp)
        val productGrammage: TextView       = itemView.findViewById(R.id.completed_product_grammage)
        private val completedButton: Button = itemView.findViewById(R.id.completed_button)

        init {
            apply {
                completedButton.setOnClickListener {
                    customInterface.completedButtonClickListener(adapterPosition, itemView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedProductsHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.completed_products_item, parent, false)
        return CompletedProductsHolder(view)
    }

    override fun onBindViewHolder(holder: CompletedProductsHolder, position: Int) {
        holder.apply {
            productName.text = products[position]?.name
            productGrammage.text = products[position]?.grammage
            productTimestamp.text = products[position]?.timestamp
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }
}