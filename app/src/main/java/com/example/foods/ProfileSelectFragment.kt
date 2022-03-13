package com.example.foods

import android.content.Context
import android.os.Bundle
import android.view.View
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
                moveToAwaitingFragment()
            }
            button2.setOnClickListener {
                editor.apply {
                    putString("profileType", "pakujacy")
                    apply()
                }
                moveToAwaitingFragment()
            }
        }
    }

    private fun moveToAwaitingFragment() {
        val action =
            ProfileSelectFragmentDirections.actionProfileSelectFragmentToAwaitingProductsFragment()
        findNavController().navigate(action)
    }
}