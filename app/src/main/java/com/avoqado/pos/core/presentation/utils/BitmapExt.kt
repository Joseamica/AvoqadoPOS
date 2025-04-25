package com.avoqado.pos.core.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.content.res.AppCompatResources

fun Context.getBitmap(drawableRes: Int): Bitmap {
    val drawable = AppCompatResources.getDrawable(this, drawableRes)
    val canvas = Canvas()
    val bitmap =
        Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888,
        )
    canvas.setBitmap(bitmap)
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    drawable.draw(canvas)
    return bitmap
}
