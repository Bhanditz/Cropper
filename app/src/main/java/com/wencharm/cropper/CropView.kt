package com.wencharm.cropper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * Created by Wencharm on 13/07/2017.
 */

class CropView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var imageView: CropImageView = CropImageView(context, attrs)
    var overlayView: CropOverlayView = CropOverlayView(context, attrs)
    var uri: Uri? = null

    private var loadListener: BitmapLoadListener? = null

    init {
        imageView.setBackgroundColor(Color.BLACK)
        addView(imageView)
        addView(overlayView)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        imageView.measure(widthMeasureSpec, heightMeasureSpec)
        overlayView.measure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(imageView.measuredWidthAndState, imageView.measuredHeightAndState)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (uri != null && (w != oldw || h != oldh)) BitmapManager.load(context, uri!!, w, h, loadListener)
    }

    override fun invalidate() {
        imageView.invalidate()
        overlayView.invalidate()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev != null) imageView.gestureProcessor.onDown(ev)
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) imageView.gestureProcessor.onTouchEvent(event)
        return true
    }

    fun setImageUri(uri: Uri) {
        this.uri = uri
        BitmapManager.load(context, uri, width, height, loadListener ?: object : BitmapLoadListener {
            override fun onStart() {
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete(uri: Uri, bitmap: Bitmap?) {
                setImageBitmap(bitmap)
                invalidate()
            }

        })
    }

    fun setImageBitmap(bitmap: Bitmap?) {
        imageView.setImageBitmap(bitmap)
    }

    fun setLoadListener(listener: BitmapLoadListener) {
        loadListener = listener
    }

}