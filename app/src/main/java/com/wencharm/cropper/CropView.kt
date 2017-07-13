package com.wencharm.cropper

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Created by Wencharm on 13/07/2017.
 */

class CropView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    lateinit private var imageView: CropImageView
    lateinit private var overlayView: CropOverlayView

    init {
        init(attrs)
    }

    fun init(attrs: AttributeSet?) {
        initImageView(attrs)
        initOverlayView(attrs)
    }

    fun initImageView(attrs: AttributeSet?) {
        imageView = CropImageView(context, attrs)
        imageView.setBackgroundColor(Color.BLACK)
        addView(imageView)
    }

    fun initOverlayView(attrs: AttributeSet?) {
        overlayView = CropOverlayView(context, attrs)
        addView(overlayView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        imageView.measure(widthMeasureSpec, heightMeasureSpec)
        overlayView.measure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(imageView.measuredWidthAndState, imageView.measuredHeightAndState)
    }

    override fun invalidate() {
        imageView.invalidate()
        overlayView.invalidate()
    }

    fun setImageUri(uri: Uri) {
        imageView.setImageURI(uri)
    }
}