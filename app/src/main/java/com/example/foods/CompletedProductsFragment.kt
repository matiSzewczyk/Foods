package com.example.foods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.foods.databinding.FragmentCompletedProductsBinding

class CompletedProductsFragment : Fragment(R.layout.fragment_completed_products) {

    private lateinit var binding: FragmentCompletedProductsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCompletedProductsBinding.bind(view)
    }
}