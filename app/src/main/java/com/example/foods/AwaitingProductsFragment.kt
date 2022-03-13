package com.example.foods

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
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

        NotificationHandler.channel_name= "Do sypania"
        NotificationHandler.createNotificationChannel(requireContext())

        lifecycleScope.launch(IO) {
            awaitingProductsViewModel.loginAnon((requireActivity().application as FoodsApp).foodsApp)
            withContext(Main) {
                awaitingProductsViewModel.createRealm((requireActivity().application as FoodsApp).foodsApp)
                awaitingProductsViewModel.getListCount()
                setupRecyclerView()
                listener = RealmChangeListener {

                    if (requireContext().getSharedPreferences("profilePref", Context.MODE_PRIVATE).getString("profileType", null) == "sypiacy") {
                        if (awaitingProductsViewModel.isNewEntry()) {
                            awaitingProductsViewModel.itemCount =
                                awaitingProductsAdapter.products.size
                            if (!awaitingProductsViewModel.isSameUser(
                                    (requireActivity().application as FoodsApp).foodsApp.currentUser()
                                        .toString()
                                )
                            ) {
                                val notification =
                                    NotificationCompat.Builder(
                                        requireContext(),
                                        NotificationHandler.channel_id
                                    )
                                        .setContentTitle(awaitingProductsViewModel.notifyObjectName())
                                        .setContentText(awaitingProductsViewModel.notifyObjectGrammage())
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
                            awaitingProductsViewModel.itemCount =
                                awaitingProductsViewModel.productList!!.size
                        }
                    }
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
        val hiddenLayout = view.findViewById<ConstraintLayout>(R.id.hidden_layout)
        if (hiddenLayout != null) {
            if (hiddenLayout.visibility == View.VISIBLE) {
                hiddenLayout.visibility = View.GONE
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            awaitingProductsViewModel.addToCompleted(id)
        }
    }
}