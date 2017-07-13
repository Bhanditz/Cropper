package com.wencharm.cropper

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * Created by Wencharm on 13/07/2017.
 */
class CropOverlayView(ctx: Context, attributeSet: AttributeSet?) : View(ctx) {

    lateinit private var cropRect: RectF
    lateinit private var overlayPaint: Paint
    var overlayColor = resources.getColor(R.color.overlayColor)
    var borderColor = resources.getColor(R.color.borderColor)

    init {
        initWidth(attributeSet)
    }

    fun initWidth(attributeSet: AttributeSet?) {
        cropRect = RectF()
        overlayPaint = Paint()
        overlayPaint.style = Paint.Style.FILL
        overlayPaint.color = overlayColor
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)
        drawBorder(canvas)
    }

    fun drawBorder(canvas: Canvas?) {
        var clearPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        clearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        var borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeCap = Paint.Cap.SQUARE
        borderPaint.color = borderColor
        borderPaint.strokeWidth = resources.getDimension(R.dimen.border_width)
    }

}