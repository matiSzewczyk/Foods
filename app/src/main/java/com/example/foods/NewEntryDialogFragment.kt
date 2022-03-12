package com.example.foods

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.foods.databinding.DialogNewEntryFragmentBinding
import com.google.android.material.switchmaterial.SwitchMaterial

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

        val productList = resources.getStringArray(R.array.product_array)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_expandable_list_item_1,
            productList
        )
        binding.newEntryName.setAdapter(adapter)

        binding.apply {

            buttonConfirm.setOnClickListener {
                if (ConnectionChecker.isInternetAvailable(requireContext())) {
                    if (isGrammageSelected(checkBox1, checkBox500)) {
                        awaitingProductsViewModel.createNewEntry(
                            newEntryName.text.toString(),
                            getGrammage(),
                            isUrgent(urgentSwitch),
                            (requireActivity().application as FoodsApp).foodsApp.currentUser()
                        )
                        dialog!!.dismiss()
                    } else {
                        Toast.makeText(context, "Należy wybrać gramaturę.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(context, "Brak połączenia.", Toast.LENGTH_SHORT).show()
                }
            }

            buttonCancel.setOnClickListener {
                dialog!!.dismiss()
            }
        }
    }

    private fun isUrgent(urgentSwitch: SwitchMaterial): String {
        return if (urgentSwitch.isChecked)
            "pilne"
        else
            ""
    }


    private fun isGrammageSelected(checkBox1: CheckBox, checkBox500: CheckBox): Boolean {
        return checkBox1.isChecked || checkBox500.isChecked
    }

    private fun getGrammage(): String {
        var text = ""
        binding.apply {
            if (checkBox1.isChecked)
                text += "1kg"
            if (checkBox500.isChecked)
                text += "\t\t500g"
        }
        return text
    }

}