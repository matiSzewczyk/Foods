package com.example.foods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foods.databinding.FragmentAwaitingProductsBinding
import io.realm.RealmChangeListener
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AwaitingProductsFragment : Fragment(R.layout.fragment_awaiting_products) {

    private lateinit var binding : FragmentAwaitingProductsBinding
    private val awaitingProductsViewModel : AwaitingProductsViewModel by activityViewModels()
    private lateinit var awaitingProductsAdapter: AwaitingProductsAdapter
    private lateinit var listener: RealmChangeListener<RealmResults<AwaitingProduct>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAwaitingProductsBinding.bind(view)

        lifecycleScope.launch(IO) {
            awaitingProductsViewModel.loginAnon((requireActivity().application as FoodsApp).foodsApp)
            withContext(Main) {
                awaitingProductsViewModel.createRealm((requireActivity().application as FoodsApp).foodsApp)
                setupRecyclerView()
                listener = RealmChangeListener {
                    awaitingProductsAdapter.notifyDataSetChanged()
                }
                awaitingProductsAdapter.products.addChangeListener(listener)
            }
        }


        binding.apply {
            button.setOnClickListener {
//            awaitingProductsViewModel.test()
                val dialog = NewEntryDialogFragment()
                dialog.show(childFragmentManager, "dialogFragment.show()")
            }
            button2.setOnClickListener {
                awaitingProductsViewModel.test()
            }
        }

    }

    private fun setupRecyclerView() = binding.awaitingProductsRecyclerView.apply {
        awaitingProductsAdapter = AwaitingProductsAdapter(
            awaitingProductsViewModel.productList()
        )
        adapter = awaitingProductsAdapter
        layoutManager = LinearLayoutManager(context)
    }
}