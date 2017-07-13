package com.wencharm.cropper

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by Wencharm on 13/07/2017.
 */

class CropImageView(ctx: Context, attrs: AttributeSet?) : ImageView(ctx) {

    lateinit private var allowedBounds: RectF
    lateinit private var imageBounds: RectF
    lateinit private var realImageBounds: RectF
    lateinit private var mImageMatrix: Matrix

    init {
        initWidth(attrs)
    }

    fun initWidth(attrs: AttributeSet?) {
        allowedBounds = RectF()
        imageBounds = RectF()
        realImageBounds = RectF()
        mImageMatrix = Matrix()
        scaleType = ScaleType.MATRIX
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}