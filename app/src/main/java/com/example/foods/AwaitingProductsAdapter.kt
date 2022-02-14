package com.example.foods

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults

class AwaitingProductsAdapter(
    var products: RealmResults<AwaitingProduct>
) : RecyclerView.Adapter<AwaitingProductsAdapter.AwaitingProductsHolder>() {

    inner class AwaitingProductsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productUrgency: TextView = itemView.findViewById(R.id.product_urgency)
        val productTimestamp: TextView = itemView.findViewById(R.id.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AwaitingProductsHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.awaiting_products_item, parent, false)
        return AwaitingProductsHolder(view)
    }

    override fun onBindViewHolder(holder: AwaitingProductsHolder, position: Int) {
        holder.apply {
            productName.text = products[position]?.name
            productUrgency.text = products[position]?.urgent
            productTimestamp.text = products[position]?.timestamp
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }
}
