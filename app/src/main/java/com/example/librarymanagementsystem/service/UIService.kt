package com.example.librarymanagementsystem.service

import android.content.Context
import android.content.res.ColorStateList
import android.widget.Button
import androidx.core.content.ContextCompat
import com.example.librarymanagementsystem.R

object UIService {
    suspend fun setButtonColor(
        context: Context,
        selectedBtn: Button,
        deselectedBtns: List<Button>? = null,
        selectedColorResId: Int =  R.color.light_blue,
        deselectedColorResId: Int = R.color.light_gray
    ) {
        val selectedColor = ContextCompat.getColor(context, selectedColorResId)
        val deselectedColor = ContextCompat.getColor(context, deselectedColorResId)

        selectedBtn.compoundDrawableTintList = ColorStateList.valueOf(selectedColor)
        selectedBtn.setTextColor(selectedColor)

        if (deselectedBtns != null) {
            for (btn in deselectedBtns) {
                if (btn == selectedBtn) continue
                btn.compoundDrawableTintList = ColorStateList.valueOf(deselectedColor)
                btn.setTextColor(deselectedColor)
            }
        }
    }
}