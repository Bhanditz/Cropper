package com.wencharm.cropper

import android.content.Context

/**
 * Created by Wencharm on 14/07/2017.
 */
fun dp(context: Context, px: Float): Int {
    return (context.resources.displayMetrics.density * px).toInt()
}