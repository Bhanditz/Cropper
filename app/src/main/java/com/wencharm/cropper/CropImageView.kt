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

    var allowedBounds = RectF()
    private var imageBounds = RectF()
    private var realImageBounds = RectF()
    private var iMatrix = Matrix()
    var gestureProcessor = GestureProcessor()

    init {
        scaleType = ScaleType.MATRIX
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun reset() {
        iMatrix = Matrix()
    }

    //todo
    fun animateToAllowedBound() {
        updateBounds()
        val endMatrix = calculateAllowedMatrix(realImageBounds, allowedBounds, iMatrix)
        iMatrix.set(endMatrix)
        imageMatrix = iMatrix
    }

    fun scaleImage(factor: Float, pivotX: Float, pivotY: Float) {
        iMatrix.postScale(factor, factor, pivotX, pivotY)
        imageMatrix = iMatrix
        updateBounds()
    }

    fun translateImage(dx: Float, dy: Float) {
        iMatrix.postTranslate(dx, dy)
        imageMatrix = iMatrix
        if (dx > 0.01f || dy > 0.01f) updateBounds()
    }

    fun updateBounds() {
        realImageBounds.set(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
        imageBounds.set(realImageBounds)
        iMatrix.mapRect(imageBounds)
    }

    inner class ScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        //todo
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val factor = detector.scaleFactor
            if (isValidScale(scaleXFromMatrix(iMatrix) * factor)) {
                scaleImage(factor, detector.focusX, detector.focusY)
            }
            return true
        }

        //todo
        fun isValidScale(scale: Float): Boolean {
            return true
        }
    }

    inner class TranslationGestureListener {
        var prex = 0f
        var prey = 0f
        var id = -1

        fun onDown(event: MotionEvent) {
            prex = event.x
            prey = event.y
            id = event.getPointerId(0)
        }

        fun onTouchEvent(event: MotionEvent, inProgress: Boolean) {
            if (event.actionMasked == MotionEvent.ACTION_POINTER_UP && event.getPointerId(event.actionIndex) == id) {
                var index = 0
                if (event.findPointerIndex(id) == 0) index = 1
                id = event.getPointerId(index)
                prex = event.getX(index)
                prey = event.getY(index)
            } else {
                val index = event.findPointerIndex(id)
                if (!inProgress) translateImage(event.getX(index) - prex, event.getY(index) - prey)
                prex = event.getX(index)
                prey = event.getY(index)
            }
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
            translationGestureListener.onTouchEvent(event, scaleDetector.isInProgress)
        }

    }
}