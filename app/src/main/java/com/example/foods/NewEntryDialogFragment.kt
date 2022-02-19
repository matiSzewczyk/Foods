package com.example.foods

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels

class NewEntryDialogFragment : DialogFragment(){

    private val awaitingProductsViewModel: AwaitingProductsViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val alertDialog = AlertDialog.Builder(it)
            alertDialog.setView(requireActivity().layoutInflater.inflate(R.layout.dialog_new_entry_fragment, null))
            alertDialog.setPositiveButton("Zatwierdź") { _, _ ->
                Toast.makeText(context, "works", Toast.LENGTH_SHORT).show()
                awaitingProductsViewModel.test()
            }

            alertDialog.create()
        }?:throw IllegalStateException("nope")
    }
}