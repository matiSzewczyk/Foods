package com.example.foods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foods.databinding.FragmentAwaitingProductsBinding
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AwaitingProductsFragment : Fragment(R.layout.fragment_awaiting_products) {

    private lateinit var binding : FragmentAwaitingProductsBinding
    private val awaitingProductsViewModel : AwaitingProductsViewModel by viewModels()
    private lateinit var awaitingProductsAdapter: AwaitingProductsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAwaitingProductsBinding.bind(view)

        lifecycleScope.launch(IO) {
            initRealm()
            println(awaitingProductsViewModel.productList()[0]!!.name)
        }
//        setupRecyclerView()

        binding.button.setOnClickListener {
            // This is just a test button


            // If pressed, send a fake request to the cloud and see if it updates on other devices
        }

    }

    private fun initRealm() {
        // TODO: This might be bad practice, let's try passing the application instance as an argument to the ViewModel. 
        awaitingProductsViewModel.loginAnon((requireActivity().application as FoodsApp).foodsApp)
    }

    private fun setupRecyclerView() = binding.awaitingProductsRecyclerView.apply {
        awaitingProductsAdapter = AwaitingProductsAdapter(
            awaitingProductsViewModel.productList()
        )
        adapter = awaitingProductsAdapter
        layoutManager = LinearLayoutManager(context)
    }
}