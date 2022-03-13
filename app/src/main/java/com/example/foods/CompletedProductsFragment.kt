package com.example.foods

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
                    if (requireActivity().getSharedPreferences("profilePref", Context.MODE_PRIVATE).getString("profileType", null) == "pakujacy") {
                        if (completedProductsViewModel.isNewEntry()) {
                            completedProductsViewModel.itemCount =
                                completedProductsAdapter.products.size
                            if (!completedProductsViewModel.isSameUser(
                                    (requireActivity().application as FoodsApp).foodsApp.currentUser()
                                        .toString()
                                )
                            ) {
                                val notification =
                                    NotificationCompat.Builder(
                                        requireContext(),
                                        NotificationHandler.channel_id
                                    )
                                        .setContentTitle(completedProductsViewModel.notifyObjectName())
                                        .setContentText(completedProductsViewModel.notifyObjectGrammage())
                                        .setSmallIcon(R.drawable.foods_icon)
                                        .setLargeIcon(
                                            BitmapFactory.decodeResource(
                                                requireContext().resources,
                                                R.mipmap.logo_round
                                            )
                                        )
                                        .setColor(resources.getColor(R.color.green_900))
                                        .setPriority(NotificationCompat.PRIORITY_MAX).build()

                                val notificationManager =
                                    NotificationManagerCompat.from(requireContext())

                                notificationManager.notify(0, notification)
                            }
                        } else {
                            completedProductsViewModel.itemCount =
                                completedProductsViewModel.productList!!.size
                        }
                    }
                    completedProductsAdapter.notifyDataSetChanged()
                }
                completedProductsAdapter.products.addChangeListener(listener)
            }
        }
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

    override fun onDestroy() {
        completedProductsAdapter.products.removeAllChangeListeners()
        super.onDestroy()
    }
}