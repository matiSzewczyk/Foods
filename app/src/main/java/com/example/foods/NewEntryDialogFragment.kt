package com.example.foods

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.foods.databinding.DialogNewEntryFragmentBinding

class NewEntryDialogFragment : DialogFragment(){

    private val awaitingProductsViewModel: AwaitingProductsViewModel by activityViewModels()
    private lateinit var binding: DialogNewEntryFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, com.google.android.material.R.style.Theme_MaterialComponents_Light_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNewEntryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonConfirm.setOnClickListener {
                if (isGrammageSelected(radioButton1, radioButton500)) {
                    awaitingProductsViewModel.createNewEntry(
                        newEntryName.text.toString(),
                        getGrammage(radioButton1),
                        isUrgent(urgentRadioButton)
                    )
                dialog!!.dismiss()
                }
                else {
                    Toast.makeText(context, "Należy wybrać gramaturę.", Toast.LENGTH_SHORT).show()
                }
            }
            buttonCancel.setOnClickListener {
                dialog!!.dismiss()
            }
        }
    }

    private fun isUrgent(urgentRadioButton: RadioButton): String {
        return if (urgentRadioButton.isChecked)
            "pilne"
        else
            ""
    }

    private fun isGrammageSelected(radioButton1: RadioButton, radioButton500: RadioButton): Boolean{
        return radioButton1.isChecked || radioButton500.isChecked
    }

    private fun getGrammage(radioButton1: RadioButton): String{
        return if (radioButton1.isChecked)
            "1kg"
        else
            "500g"
    }

}