package com.wencharm.cropper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.AsyncTask
import android.support.media.ExifInterface
import android.util.Log
import java.io.IOException
import java.io.OutputStream


/**
 * Created by Wencharm on 14/07/2017.
 */
object BitmapManager {
    private var loadListener: BitmapLoadListener? = null
    private var cropListener: BitmapLoadListener? = null
    fun load(context: Context, uri: Uri, w: Int, h: Int, listener: BitmapLoadListener?) {
        this.loadListener = listener
        if (w != 0 || h != 0) {
            listener?.onStart()
            LoadTask(context, uri, w, h).execute()
        }
    }

    fun crop(context: Context, uri: Uri, w: Int, h: Int, cropArea: CropArea, listener: BitmapLoadListener?) {
        this.cropListener = listener
        listener?.onStart()
        CropTask(context, uri, w, h, cropArea).execute()
    }

    fun notifyLoadListener(uri: Uri, result: Bitmap?, e: Throwable?) {
        if (e != null) loadListener?.onError(e)
        else loadListener?.onComplete(uri, result)
    }

    fun notifyCropListenr(uri: Uri, e: Throwable?) {
        if (e != null) cropListener?.onError(e)
        else cropListener?.onComplete(uri, null)
    }

    fun loadToMemory(context: Context, uri: Uri, w: Int, h: Int): Bitmap? {
        var bitmap: Bitmap
        var options = getOptions(context, uri, w, h)
        while (true) {
            val input = context.contentResolver.openInputStream(uri)
            try {
                bitmap = BitmapFactory.decodeStream(input, null, options)
                return ensureCorrectRotation(context, uri, bitmap)
            } catch (e: OutOfMemoryError) {
                if (options.inSampleSize < 64) {
                    options.inSampleSize *= 2
                    continue
                } else {
                    return null
                }
            }
        }
    }

    fun getOptions(context: Context, uri: Uri, w: Int, h: Int): BitmapFactory.Options {
        val input = context.contentResolver.openInputStream(uri)
        val result = BitmapFactory.Options()
        result.inSampleSize = 1
        result.inJustDecodeBounds = true
        BitmapFactory.decodeStream(input, null, result)
        result.inJustDecodeBounds = false
        val degree = getExifOrientation(context, uri)
        val rotated = degree == 90 || degree == 270
        var outw = if (rotated) result.outHeight else result.outWidth
        var outh = if (rotated) result.outWidth else result.outHeight
        while (outw > 1.5 * w || outh > 1.5 * h) {
            result.inSampleSize *= 2
            outw /= 2
            outh /= 2
        }
        return result
    }

    fun getExifOrientation(context: Context, uri: Uri): Int {
        val input = context.contentResolver.openInputStream(uri)
        val exif = ExifInterface(input)
        var rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        when (rotation) {
            ExifInterface.ORIENTATION_ROTATE_90, ExifInterface.ORIENTATION_TRANSPOSE -> return 90
            ExifInterface.ORIENTATION_ROTATE_180, ExifInterface.ORIENTATION_FLIP_VERTICAL -> return 180
            ExifInterface.ORIENTATION_ROTATE_270, ExifInterface.ORIENTATION_TRANSVERSE -> return 270
            else -> return 0
        }
    }

    fun ensureCorrectRotation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        var degree = getExifOrientation(context, uri)
        if (degree != 0) {
            var matrix = Matrix()
            matrix.preRotate(degree.toFloat())
            try {
                val converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                return if (!converted.sameAs(bitmap)) {
                    bitmap.recycle()
                    converted
                } else {
                    bitmap
                }
            } catch (e: OutOfMemoryError) {
                Log.d("Load preview error: ", e.toString())
            }
        }
        return bitmap
    }
}

class LoadTask(var context: Context, var uri: Uri, var w: Int, var h: Int) : AsyncTask<Any, Any, Throwable>() {

    var result: Bitmap? = null

    override fun doInBackground(vararg p0: Any?): Throwable? {
        try {
            result = BitmapManager.loadToMemory(context, uri, w, h)
            if (result == null) {
                return IOException("Failed to load image")
            }
        } catch (e: Exception) {
            return e
        }
        return null
    }

    override fun onPostExecute(e: Throwable?) {
        BitmapManager.notifyLoadListener(uri, result, e)
    }

}

class CropTask(val context: Context, val uri: Uri, var w: Int, var h: Int, val area: CropArea) : AsyncTask<Any, Any, Throwable>() {

    lateinit var path: Uri
    override fun doInBackground(vararg p0: Any?): Throwable? {
        try {
            var result: Bitmap
            path = tempPath(context, "JPG")
            val b = BitmapManager.loadToMemory(context, uri, w, h)
            if (b != null) result = area.apply(b)
            else return Throwable(NullPointerException())
            val os: OutputStream = context.contentResolver.openOutputStream(path)
            result.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.close()
            b.recycle()
            result.recycle()
        } catch (e: Exception) {
            return e
        }
        return null
    }

    override fun onPostExecute(result: Throwable?) {
        BitmapManager.notifyCropListenr(path, result)
    }
}

interface BitmapLoadListener {
    fun onStart()
    fun onError(e: Throwable)
    fun onComplete(uri: Uri, bitmap: Bitmap?)
}