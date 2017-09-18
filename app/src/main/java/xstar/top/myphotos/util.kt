package xstar.top.myphotos

import android.graphics.Bitmap
import android.graphics.Color

/**
 * @author: xstar
 * @since: 2017-09-18.
 */

fun Bitmap.pixels(): IntArray {
    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)
    return pixels
}

fun Bitmap.gray(alogrithm: String = GrayAlogrithmType.Weight): Bitmap {
    val pixels = pixels()
    val newPixels = convert(pixels, alogrithm)
    return Bitmap.createBitmap(newPixels, width, height, config)
}

/**
 * 在对图像进行灰度化处理后，我们首先定义一个阈值（threshold）。我们知道素描主要强调的是明暗度的变化，
 * 绘制时是斜向方向，通过经验，我们将每个像素点的灰度值与其右下角的灰度值进行比较，当大于这个阈值时，就判断其是轮廓并绘制。
 */
fun Bitmap.sketch(threshold: Int = 8): Bitmap {
    var _threshold = threshold
    if (threshold < 0) _threshold = 0
    else if (threshold > 100) _threshold = 100
    val newPixels = pixels()//灰度数据
    var src: Int
    var dst: Int
    var diff: Int
    val white = Color.WHITE
    val black = Color.BLACK
    var w: Int
    var h: Int
    for (index in newPixels.indices) {
        w = index % width
        h = index / width
        if (w == (width - 1) || h == height - 1)
            continue
        assert(w * h + w == index, { "下标不相等！" + (w * h + w) })
        src = choiceAlogrithm(newPixels[width * h + w])//当前点灰度
        dst = choiceAlogrithm(newPixels[width * (h + 1) + (w + 1)])//右下点灰度
        diff = Math.abs(src - dst)
//        Log.e("index", index.toString().plus("==").plus((w * h + w)))
//        if (0 < diff) Log.e("diff", diff.toString())
        if (diff >= _threshold)
            newPixels[index] = black
        else
            newPixels[index] = white
    }
    return Bitmap.createBitmap(newPixels, width, height, config)
}

/**
 * 对于铅笔画来说，原理和素描十分相似，但是大家学过画画的就知道，素描强调的是阴影的效果，
 * 是斜向作画，而铅笔画主要是勾勒轮廓。因此在对每个像素点的处理上，就和素描产生变化。
 * 对于任意一个像素点，求出这个像素点的R、G、B三个分量与周围8个点的相应分量的平均值的差，
 * 如果这三个差都大于或者等于某个阈值，就画出线条。
 * @param threshold 阈值
 * 简笔画
 */
fun Bitmap.pencil(threshold: Int = 8): Bitmap {
    var _threshold = threshold
    if (threshold < 0) _threshold = 0
    else if (threshold > 100) _threshold = 100
    val newPixels = pixels()//灰度数据
    val white = Color.WHITE
    val black = Color.BLACK
    var w: Int
    var h: Int
    var area = IntArray(8)
    for (index in newPixels.indices) {
        w = index % width
        h = index / width
        if (w == 0 || h == 0 || w == (width - 1) || h == height - 1)
            continue
        assert(w * h + w == index, { "下标不相等！" + (w * h + w) })
        area[0] = newPixels[width * (h - 1) + w - 1]
        area[1] = newPixels[width * (h - 1) + w]
        area[2] = newPixels[width * (h - 1) + w + 1]
        area[3] = newPixels[width * h + w - 1]
        area[4] = newPixels[width * h + w + 1]
        area[5] = newPixels[width * (h + 1) + w - 1]
        area[6] = newPixels[width * (h + 1) + w]
        area[7] = newPixels[width * (h + 1) + w + 1]
        if (checkDiff(sumARGB(area), ARGB(newPixels[index]), _threshold))
            newPixels[index] = white
        else
            newPixels[index] = black
    }
    return Bitmap.createBitmap(newPixels, width, height, config)
}

/**
 * argb分别累加
 */
fun sumARGB(colors: IntArray): IntArray {
    val argb = IntArray(4)
    for (i in colors) {
        argb[0] += i.shr(24)//A
        argb[1] += i.shr(16).and(0xff)//R
        argb[2] += i.shr(8).and(0xff)//R
        argb[3] += i.and(0xff)//R
    }
    //除以8
    argb[0] = argb[0].shr(3)
    argb[1] = argb[1].shr(3)
    argb[2] = argb[2].shr(3)
    argb[3] = argb[3].shr(3)
    return argb
}

fun ARGB(color: Int): IntArray {
    val argb = IntArray(4)
    argb[0] = color.shr(24)//A
    argb[1] = color.shr(16).and(0xff)//R
    argb[2] = color.shr(8).and(0xff)//R
    argb[3] = color.and(0xff)//R
    return argb
}

fun checkDiff(dist: IntArray, src: IntArray, threshold: Int): Boolean {
    for (i in 1..3) {
        if (Math.abs(src[i] - dist[i]) < threshold) return false
    }
    return true
}

fun convert(src: IntArray, mode: String = GrayAlogrithmType.Weight): IntArray {
    val newPixels = IntArray(src.size)
    for (index in src.indices) {
        val i = src[index]
        newPixels[index] = grayAlogrithm(i, mode)
    }
    return newPixels
}

/**
 *
 *
 *  最大值法(Maximum):使R、G、B的值等于三个色彩分量中的最大的一个分量值，即：R=G=B=Max(R,G,B)。
平均值法(Average)：使R、G、B的值等于三个色彩分量的三个色彩分量的平均值，即：R=G=B= (R+G+B)/3。
加权平均值法(Weight Average)：在这里我给R、G、B三分量分别附上不同的权值，表示为：R=G=B=WR*R+WG*G+WB*B ,其中WR，WG，WB分别是R、G、B的权值。在这里考虑由于人眼对绿色的敏感度最高，红色次之，对蓝色的敏感度最低，因此，当权值 WG > WR > WB时，所产生的灰度图像更符合人眼的视觉感受。PIL库使用ITU-R 601-2 luma transform：
L = R * 299/1000 + G * 587/1000 + B * 114/1000
即 WR=29.9%，WG=58.7%，WB=11.4%。
 *
 */
fun grayAlogrithm(i: Int, mode: String = "WEIGHT"): Int {
    val alpha = i.shr(24)
    val gray = choiceAlogrithm(i, mode)
    return grayToColor(alpha, gray)
}

fun grayToColor(alpha: Int = 0xff, gray: Int): Int {
    return alpha.shl(8).or(gray).shl(8).or(gray).shl(8).or(gray)
}

fun choiceAlogrithm(i: Int, mode: String = "WEIGHT"): Int {
    val gray: Int
    when (mode.toUpperCase()) {
        GrayAlogrithmType.Weight ->
            gray = i.shr(16).and(0xff).times(299).div(1000).plus(i.shr(8).and(0xff).times(587).div(1000)).plus(i.and(0xff).times(114).div(1000))
        GrayAlogrithmType.Average ->
            gray = i.shr(16).and(0xff).plus(i.shr(8).and(0xff)).plus(i.and(0xff)).div(3)
        GrayAlogrithmType.Maximum -> {
            val temp = Math.max(i.shr(16).and(0xff), i.shr(8).and(0xff))
            gray = Math.max(i.and(0xff), temp)
        }
        else -> {
            gray = i.shr(16).and(0xff).times(299).div(1000).plus(i.shr(8).and(0xff).times(587).div(1000)).plus(i.and(0xff).times(114).div(1000))
        }
    }
    return gray
}


object GrayAlogrithmType {
    var Weight = "WEIGHT"
    var Average = "AVERAGE"
    var Maximum = "MAXIMUM"
}