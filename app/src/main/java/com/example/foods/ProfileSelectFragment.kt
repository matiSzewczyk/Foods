package com.example.foods

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.foods.databinding.FragmentProfileSelectBinding

class ProfileSelectFragment : Fragment(R.layout.fragment_profile_select) {

    private lateinit var binding: FragmentProfileSelectBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileSelectBinding.bind(view)

        val sharedPref = requireContext().getSharedPreferences("profilePref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        binding.apply {
            button1.setOnClickListener {
                editor.apply {
                    putString("profileType", "sypiacy")
                    apply()
                }
                goToAwaitingFragment()
            }
            button2.setOnClickListener {
                editor.apply {
                    putString("profileType", "pakujacy")
                    apply()
                }
                goToAwaitingFragment()
            }
        }
    }

    private fun goToAwaitingFragment() {
        val action = NavGraphDirections.actionGlobalAwaitingProductsFragment()
        findNavController().navigate(action)
    }
}