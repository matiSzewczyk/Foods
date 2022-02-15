package com.example.foods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foods.databinding.FragmentAwaitingProductsBinding
import io.realm.RealmChangeListener
import io.realm.RealmResults

class AwaitingProductsFragment : Fragment(R.layout.fragment_awaiting_products) {

    private lateinit var binding : FragmentAwaitingProductsBinding
    private val awaitingProductsViewModel : AwaitingProductsViewModel by viewModels()
    private lateinit var awaitingProductsAdapter: AwaitingProductsAdapter
    private lateinit var listener: RealmChangeListener<RealmResults<AwaitingProduct>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAwaitingProductsBinding.bind(view)

        initRealm()
        setupRecyclerView()

        listener = RealmChangeListener {
            awaitingProductsAdapter.notifyDataSetChanged()
        }
        awaitingProductsAdapter.products.addChangeListener(listener)

        binding.button.setOnClickListener {
            awaitingProductsViewModel.test()
        }

    }

    private fun initRealm() {
        // TODO: This might be bad practice, let's try passing the application instance as an argument to the ViewModel. 
        awaitingProductsViewModel.loginAnon((requireActivity().application as FoodsApp).foodsApp)
    }

    private fun setupRecyclerView() = binding.awaitingProductsRecyclerView.apply {
        awaitingProductsAdapter = AwaitingProductsAdapter(
            awaitingProductsViewModel.productList!!
        )
        adapter = awaitingProductsAdapter
        layoutManager = LinearLayoutManager(context)
    }
}