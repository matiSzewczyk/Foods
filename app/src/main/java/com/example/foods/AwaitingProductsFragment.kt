package com.example.foods

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AwaitingProductsFragment : Fragment(R.layout.fragment_awaiting_products), RecyclerViewInterface {

    private lateinit var binding : FragmentAwaitingProductsBinding
    private val awaitingProductsViewModel : AwaitingProductsViewModel by activityViewModels()
    private lateinit var awaitingProductsAdapter: AwaitingProductsAdapter
    private lateinit var listener: RealmChangeListener<RealmResults<AwaitingProduct>>

    private val CHANNEL_ID = "channelID"
    private val CHANNEL_NAME = "channelName"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAwaitingProductsBinding.bind(view)

        createNotificationChannel()

        lifecycleScope.launch(IO) {
            awaitingProductsViewModel.loginAnon((requireActivity().application as FoodsApp).foodsApp)
            withContext(Main) {
                awaitingProductsViewModel.createRealm((requireActivity().application as FoodsApp).foodsApp)
                awaitingProductsViewModel.getListCount()
                setupRecyclerView()
                listener = RealmChangeListener {

                    if (awaitingProductsViewModel.isNewEntry()) {
                        if (!awaitingProductsViewModel.isSameUser((requireActivity().application as FoodsApp).foodsApp.currentUser().toString())) {
                            val notification =
                                NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                                    .setContentTitle(awaitingProductsViewModel.notifyObjectName())
                                    .setContentText(awaitingProductsViewModel.notifyObjectGrammage())
                                    .setSmallIcon(R.drawable.placeholder)
                                    .setPriority(NotificationCompat.PRIORITY_MAX).build()

                            val notificationManager =
                                NotificationManagerCompat.from(requireContext())

                            notificationManager.notify(0, notification)
                            awaitingProductsViewModel.itemCount =
                                awaitingProductsAdapter.products.size
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
        CoroutineScope(Main).launch {
            awaitingProductsViewModel.addToCompleted(id)
            awaitingProductsViewModel.deleteFromRealm(id)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }
            val manager: NotificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}