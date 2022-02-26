package com.example.foods

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foods.databinding.FragmentAwaitingProductsBinding
import io.realm.RealmChangeListener
import io.realm.RealmResults
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AwaitingProductsFragment : Fragment(R.layout.fragment_awaiting_products), RecyclerViewInterface {

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
            newEntryButton.setOnClickListener {
                val dialog = NewEntryDialogFragment()
                dialog.show(childFragmentManager, "dialogFragment.show()")
            }
            button2.setOnClickListener {
                awaitingProductsViewModel.deleteAll()
                val hiddenLayout = view.findViewById<ConstraintLayout>(R.id.hidden_layout)
                hiddenLayout.visibility = View.GONE
            }
        }

    }

    private fun setupRecyclerView() = binding.awaitingProductsRecyclerView.apply {
        awaitingProductsAdapter = AwaitingProductsAdapter(
            awaitingProductsViewModel.productList(),
            this@AwaitingProductsFragment
        )
        adapter = awaitingProductsAdapter
        layoutManager = LinearLayoutManager(context)
    }

    override fun layoutClickListener(position: Int, view: View?) {
        val hiddenLayout = view?.findViewById<ConstraintLayout>(R.id.hidden_layout)
        if (hiddenLayout != null) {
            if (hiddenLayout.visibility == View.GONE) {
                hiddenLayout.visibility = View.VISIBLE
            } else {
                hiddenLayout.visibility = View.GONE
            }
        }
    }

    override fun deleteButtonClickListener(position: Int, view: View?) {
        val hiddenLayout = view?.findViewById<ConstraintLayout>(R.id.hidden_layout)
        if (hiddenLayout != null) {
            if (hiddenLayout.visibility == View.VISIBLE) {
                hiddenLayout.visibility = View.GONE
            }
            awaitingProductsViewModel.deleteFromRealm(awaitingProductsAdapter.products[position]!!.id)
        }
    }

    override fun toggleUrgencyClickListener(position: Int, view: View?) {
        awaitingProductsViewModel.toggleUrgency(
            awaitingProductsAdapter.products[position]!!.id
        )
    }

    override fun completedButtonClickListener(position: Int, view: View) {
        val id = awaitingProductsAdapter.products[position]!!.id
        awaitingProductsViewModel.addToCompleted(id)
        awaitingProductsViewModel.deleteFromRealm(id)
    }
}