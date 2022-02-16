package com.example.foods

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class NewEntryDialogFragment : DialogFragment(){


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val alertDialog = AlertDialog.Builder(it)
            alertDialog.setView(requireActivity().layoutInflater.inflate(R.layout.dialog_new_entry_fragment, null))
            alertDialog.setPositiveButton("Zatwierdź") { _, _ ->
                Toast.makeText(context, "works", Toast.LENGTH_SHORT).show()
            }

            alertDialog.create()
        }?:throw IllegalStateException("nope")
    }
}