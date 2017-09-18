package xstar.top.myphotos

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * @author: xstar
 * @since: 2017-09-18.
 */
object Const {
    open fun init(context: Context) {
        val win = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        win.defaultDisplay.getMetrics(dm)
        SCREEN_W = dm.widthPixels
        SCREEN_H = dm.heightPixels
        DENSITY = dm.density
        SCALE_DENSITY = dm.scaledDensity

    }

    open var SCREEN_W = 0
    open var SCREEN_H = 0
    open var DENSITY = 0f
    open var SCALE_DENSITY = 0f

    open val PHOTO_TRANS_NONE = 0//无变换
    open val PHOTO_TRANS_GRAY = 1//灰度
    open val PHOTO_TRANS_SKETCH = 2//素描
    open val PHOTO_TRANS_PENCIL = 3//铅笔画
}