package com.wencharm.cropper

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF

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

fun calculateAllowedMatrix(initBounds: RectF, allowedBounds: RectF, initMatrix: Matrix): Matrix {
    val currentBounds = RectF()
    val currentMatrix = Matrix()
    currentMatrix.set(initMatrix)
    currentBounds.set(initBounds)
    currentMatrix.mapRect(currentBounds)

    if (currentBounds.width() < allowedBounds.width()) {
        scaleToMatrix(initBounds, currentMatrix, allowedBounds.width() / currentBounds.width(), currentBounds)
    }

    if (currentBounds.height() < allowedBounds.height()) {
        scaleToMatrix(initBounds, currentMatrix, allowedBounds.height() / currentBounds.height(), currentBounds)
    }

    if (currentBounds.left > allowedBounds.left) {
        translateToMatrix(initBounds = initBounds, transformer = currentMatrix, dx = allowedBounds.left - currentBounds.left, out = currentBounds)
    }

    if (currentBounds.right < allowedBounds.right) {
        translateToMatrix(initBounds = initBounds, transformer = currentMatrix, dx = allowedBounds.right - currentBounds.right, out = currentBounds)
    }

    if (currentBounds.top > allowedBounds.top) {
        translateToMatrix(initBounds = initBounds, transformer = currentMatrix, dy = allowedBounds.top - currentBounds.top, out = currentBounds)
    }

    if (currentBounds.bottom < allowedBounds.bottom) {
        translateToMatrix(initBounds = initBounds, transformer = currentMatrix, dy = allowedBounds.bottom - currentBounds.bottom, out = currentBounds)
    }

    return currentMatrix
}

fun scaleToMatrix(initBounds: RectF, transformer: Matrix, s: Float, out: RectF) {
    transformer.postScale(s, s, out.centerX(), out.centerY())
    out.set(initBounds)
    transformer.mapRect(out)
}

fun translateToMatrix(initBounds: RectF, transformer: Matrix, dx: Float = 0f, dy: Float = 0f, out: RectF) {
    transformer.postTranslate(dx, dy)
    out.set(initBounds)
    transformer.mapRect(out)
}