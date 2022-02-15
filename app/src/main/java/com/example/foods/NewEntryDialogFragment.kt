package com.example.foods

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.example.foods.databinding.DialogNewEntryFragmentBinding

class NewEntryDialogFragment : DialogFragment(R.layout.dialog_new_entry_fragment){

    private lateinit var binding: DialogNewEntryFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DialogNewEntryFragmentBinding.bind(view)
    }
}