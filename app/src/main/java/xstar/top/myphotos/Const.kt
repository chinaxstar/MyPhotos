package xstar.top.myphotos

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * @author: xstar
 * @since: 2017-09-18.
 */
object Const {
    fun init(context: Context) {
        val win = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        win.defaultDisplay.getMetrics(dm)
        SCREEN_W = dm.widthPixels
        SCREEN_H = dm.heightPixels
        DENSITY = dm.density
        SCALE_DENSITY = dm.scaledDensity

    }

    var SCREEN_W = 0
    var SCREEN_H = 0
    var DENSITY = 0f
    var SCALE_DENSITY = 0f

    val PHOTO_TRANS_NONE = 0//无变换
    val PHOTO_TRANS_GRAY = 1//灰度
    val PHOTO_TRANS_SKETCH = 2//素描
    val PHOTO_TRANS_PENCIL = 3//铅笔画
}