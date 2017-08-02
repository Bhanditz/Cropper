package com.wencharm.cropper

import android.graphics.Bitmap
import android.graphics.RectF

/**
 * Created by Wencharm on 02/08/2017.
 */
class CropArea(cropRectF: RectF, imageBounds: RectF) {
    val imageRectF = imageBounds
    val cropRect = RectF(cropRectF.left - imageBounds.left, cropRectF.top - imageBounds.top, cropRectF.right - imageBounds.left, cropRectF.bottom - imageBounds.top)

    fun apply(bitmap: Bitmap): Bitmap {
        val immutable = Bitmap.createBitmap(bitmap,
                realCoordinate(bitmap.width, cropRect.left, imageRectF.width()),
                realCoordinate(bitmap.height, cropRect.top, imageRectF.height()),
                realCoordinate(bitmap.width, cropRect.width(), imageRectF.width()),
                realCoordinate(bitmap.height, cropRect.height(), imageRectF.height()))
        return immutable.copy(bitmap.config, true)
    }

    fun realCoordinate(realSize: Int, d: Float, relativeSize: Float): Int {
        return Math.round(realSize * d / relativeSize)
    }
}