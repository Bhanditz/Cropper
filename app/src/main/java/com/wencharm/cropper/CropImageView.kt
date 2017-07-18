package com.wencharm.cropper

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
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

    //todo
    fun animateToAllowedBound() {

    }

    inner class ScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        //todo
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            return true
        }

        //todo
        fun isValidScale(scale: Float): Boolean {
            return true
        }
    }

    inner class TranslationGestureListener {
        //todo
        fun onDown(event: MotionEvent) {

        }

        //todo
        fun onTouchEvent(event: MotionEvent, inProgress: Boolean) {

        }
    }

    inner class GestureProcessor {

        var scaleDetector = ScaleGestureDetector(context, ScaleGestureListener())
        var translationGestureListener = TranslationGestureListener()

        fun onDown(event: MotionEvent) {
            translationGestureListener.onDown(event)
        }

        fun onTouchEvent(event: MotionEvent) {
            when(event.action) {
                MotionEvent.ACTION_DOWN -> return
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    animateToAllowedBound()
                    return
                }
            }

            scaleDetector.onTouchEvent(event)
            translationGestureListener.onTouchEvent(event, !scaleDetector.isInProgress)
        }

    }
}