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

    public static native int grayAlogrithm(int src);

    public static native int abs(int src);

    public static native byte[] ARGB(int src);
}
