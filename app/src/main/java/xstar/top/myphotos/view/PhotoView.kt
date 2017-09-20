package xstar.top.myphotos.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.Nullable
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import xstar.top.myphotos.R

/**
 * @author: xstar
 * @since: 2017-09-11.
 */
class PhotoView : View {
    val photoMatrix = Matrix()
    var photo: BitmapDrawable? = null
    val paint = Paint()
    var scaleNum = 1f
    val photoRect = Rect()
    val movedLenth = PointF()

    constructor(context: Context) : super(context)

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr, 0) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PhotoView, defStyleAttr, 0)
        photo = a.getDrawable(R.styleable.PhotoView_photo) as BitmapDrawable
        photo?.let {
            val bm = it.bitmap
            it.bounds = Rect(0, 0, bm.width, bm.height)
            photoRect.right = bm.width
            photoRect.bottom = bm.height
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        photo = BitmapDrawable(resources, bitmap)
        photo?.bounds = Rect(0, 0, bitmap.width, bitmap.height)
        photoRect.right = bitmap.width
        photoRect.bottom = bitmap.height
        recover()
    }

    constructor(context: Context, @Nullable attrs: AttributeSet) : this(context, attrs, 0)

    override fun onDraw(canvas: Canvas?) {
        canvas!!
        photo?.let {
            val bitmap = it.bitmap
            val left = (canvas.width - photoRect.width()) shr 1
            val top = (canvas.height - photoRect.height()) shr 1
            photoMatrix.reset()
            photoMatrix.preScale(scaleNum, scaleNum)
            photoMatrix.postTranslate(left.plus(movedLenth.x), top.plus(movedLenth.y))
            canvas.drawBitmap(bitmap, photoMatrix, paint)
        }
    }

    var doScale = false
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        doScale = false
        scaleGesture.onTouchEvent(event)
        gesture.onTouchEvent(event)
        return true
    }


    fun Matrix.values(): FloatArray {
        val arr = FloatArray(9)
        getValues(arr)
        return arr
    }

    val scaleGesture = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            //缩放的中心点
            val scaleCenter = PointF(detector!!.focusX, detector.focusY)
            val delta = detector.currentSpan - detector.previousSpan
            scaleNum += delta.div(100)
            if (scaleNum < 0.2) scaleNum = 0.2f
            if (scaleNum > 100) scaleNum = 100f
            photoScaleChange(scaleNum)
            invalidate()
            doScale = true
            return true
        }
    })

    fun photoScaleChange(scale: Float) {
        photo?.let {
            photoRect.right = it.bitmap.width.times(scale).toInt()
            photoRect.bottom = it.bitmap.height.times(scale).toInt()
        }
    }

    val gesture = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            //滑动
            if (!doScale and (Math.abs(distanceX) > 3f) and (Math.abs(distanceY) > 3)) {
                Log.e("touch", String.format("x:%s,y:%s", distanceX.toString(), distanceY.toString()))
                movedLenth.x -= distanceX
                movedLenth.y -= distanceY
                invalidate()
                return true
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            //双击恢复原状
            recover()
            return true
        }
    })

    /**
     * 恢复默认状态
     */
    fun recover() {
        movedLenth.set(0f, 0f)
        scaleNum = 1f
        photoScaleChange(scaleNum)
        invalidate()
    }

    fun setColorFilter(colorFilter: ColorFilter) {
        paint.colorFilter=colorFilter
        paint.isAntiAlias=true
        invalidate()
    }
}