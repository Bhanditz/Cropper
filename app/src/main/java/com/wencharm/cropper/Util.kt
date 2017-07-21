package com.wencharm.cropper

import android.content.Context
import android.graphics.Matrix

/**
 * Created by Wencharm on 14/07/2017.
 */
fun dp(context: Context, px: Float): Int {
    return (context.resources.displayMetrics.density * px).toInt()
}

fun scaleXFromMatrix(matrix: Matrix): Float {
    val values = FloatArray(9)
    matrix.getValues(values)
    return values[Matrix.MSCALE_X]
}