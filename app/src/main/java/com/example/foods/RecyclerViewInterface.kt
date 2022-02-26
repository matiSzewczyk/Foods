package com.example.foods

import android.view.View

interface RecyclerViewInterface {
    fun layoutClickListener(position: Int, view: View?)
    fun deleteButtonClickListener(position: Int, view: View?)
    fun toggleUrgencyClickListener(position: Int, view: View?)
    fun completedButtonClickListener(position: Int, view: View)
}