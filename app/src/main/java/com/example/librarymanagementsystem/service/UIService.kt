package com.example.librarymanagementsystem.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.librarymanagementsystem.R

object UIService {
    @SuppressLint("UseCompatTextViewDrawableApis")
    fun setButtonColor(
        context: Context,
        selectedBtn: View,
        deselectedBtns: List<View>? = null,
        selectedColorResId: Int = R.color.light_blue,
        deselectedColorResId: Int = R.color.light_gray
    ) {
        val selectedColor = ContextCompat.getColor(context, selectedColorResId)
        val deselectedColor = ContextCompat.getColor(context, deselectedColorResId)

        when (selectedBtn) {
            is Button -> {
                selectedBtn.compoundDrawableTintList = ColorStateList.valueOf(selectedColor)
                selectedBtn.setTextColor(selectedColor)
            }
            is ImageView -> {
                selectedBtn.imageTintList = ColorStateList.valueOf(selectedColor)
            }
        }

        deselectedBtns?.forEach { btn ->
            if (btn == selectedBtn) return@forEach
            when (btn) {
                is Button -> {
                    btn.compoundDrawableTintList = ColorStateList.valueOf(deselectedColor)
                    btn.setTextColor(deselectedColor)
                }
                is ImageView -> {
                    btn.imageTintList = ColorStateList.valueOf(deselectedColor)
                }
            }
        }
    }

    fun setButtonIcon(
        context: Context,
        targetView: View,
        drawableResId: Int,
        position: IconPosition = IconPosition.LEFT
    ) {
        val drawable: Drawable? = ContextCompat.getDrawable(context, drawableResId)

        when (targetView) {
            is Button -> {
                val drawables = arrayOfNulls<Drawable>(4)
                when (position) {
                    IconPosition.LEFT -> drawables[0] = drawable
                    IconPosition.TOP -> drawables[1] = drawable
                    IconPosition.RIGHT -> drawables[2] = drawable
                    IconPosition.BOTTOM -> drawables[3] = drawable
                }
                targetView.setCompoundDrawablesWithIntrinsicBounds(
                    drawables[0], drawables[1], drawables[2], drawables[3]
                )
            }

            is ImageView -> {
                targetView.setImageDrawable(drawable)
            }
        }
    }

    enum class IconPosition {
        LEFT, TOP, RIGHT, BOTTOM
    }
}