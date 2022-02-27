package com.example.foods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foods.databinding.FragmentCompletedProductsBinding
import io.realm.RealmChangeListener
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CompletedProductsFragment : Fragment(R.layout.fragment_completed_products), RecyclerViewInterface{

    private lateinit var binding: FragmentCompletedProductsBinding
    private val completedProductsViewModel : CompletedProductsViewModel by activityViewModels()
    private lateinit var completedProductsAdapter: CompletedProductsAdapter
    private lateinit var listener: RealmChangeListener<RealmResults<CompletedProduct>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCompletedProductsBinding.bind(view)

        lifecycleScope.launch(Dispatchers.IO) {
            completedProductsViewModel.loginAnon((requireActivity().application as FoodsApp).foodsApp)
            withContext(Dispatchers.Main) {
                completedProductsViewModel.createRealm((requireActivity().application as FoodsApp).foodsApp)
                setupRecyclerView()
                listener = RealmChangeListener {
                    completedProductsAdapter.notifyDataSetChanged()
                }
                completedProductsAdapter.products.addChangeListener(listener)
            }
        }

//        binding.button2.setOnClickListener {
//            println("\n${completedProductsAdapter.products.size}")
//            println("\n${completedProductsAdapter.products}")
//        }
    }

    private fun setupRecyclerView() = binding.completedProductsRecyclerView.apply {
        completedProductsAdapter = CompletedProductsAdapter(
            completedProductsViewModel.productList(),
            this@CompletedProductsFragment
        )
        adapter = completedProductsAdapter
        layoutManager = LinearLayoutManager(context)
    }

    override fun layoutClickListener(position: Int, view: View?) {
        TODO("Not yet implemented")
    }

    override fun deleteButtonClickListener(position: Int, view: View?) {
        TODO("Not yet implemented")
    }

    override fun toggleUrgencyClickListener(position: Int, view: View?) {
        TODO("Not yet implemented")
    }

    override fun completedButtonClickListener(position: Int, view: View) {
        val id = completedProductsAdapter.products[position]!!.id
        completedProductsViewModel.deleteFromRealm(id)
    }

}