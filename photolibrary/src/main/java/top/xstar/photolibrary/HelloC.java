package top.xstar.photolibrary;

/**
 * @author: xstar
 * @since: 2017-09-19.
 */

public class HelloC {

    static {
        System.loadLibrary("hello");
    }

    public static native String hello();

    /**
     * 铅笔画
     *
     * @param pixels
     * @param w
     * @param h
     * @param threshold
     * @return
     */
    public static native int[] pencil(int[] pixels, int w, int h, int threshold);

    /**
     * 素描
     *
     * @param pixels
     * @param w
     * @param h
     * @param threshold
     * @return
     */
    public static native int[] sketch(int[] pixels, int w, int h, int threshold);

    /**
     * 灰度图片
     *
     * @param pixels
     * @param w
     * @param h
     * @return
     */
    public static native int[] grey(int[] pixels, int w, int h);

    public static native int grayAlogrithm(int src);

    public static native int abs(int src);

//    /**
//     * 检查周围八个点的色值分量
//     *
//     * @param pixels
//     * @param src
//     * @param threshold
//     * @return
//     */
//    public static native boolean checkDiff(int[] pixels, int src, int threshold);


}
