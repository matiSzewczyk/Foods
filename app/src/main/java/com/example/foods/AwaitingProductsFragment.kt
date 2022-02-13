package com.example.foods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.foods.databinding.FragmentAwaitingProductsBinding

class AwaitingProductsFragment : Fragment(R.layout.fragment_awaiting_products) {

    private lateinit var binding : FragmentAwaitingProductsBinding
    private val awaitingProductsViewModel : AwaitingProductsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAwaitingProductsBinding.bind(view)

        initRealm()
    }

    private fun initRealm() {
        awaitingProductsViewModel.loginAnon((requireActivity().application as FoodsApp).foodsApp)
    }
}