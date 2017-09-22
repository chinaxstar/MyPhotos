package top.xstar.photolibrary;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("top.xstar.photolibrary.test", appContext.getPackageName());
        assertEquals(0, HelloC.grayAlogrithm(0));
        assertEquals(255, HelloC.grayAlogrithm(0xffffffff));
        //17 299 34 587 51 114
        assertEquals(30, HelloC.grayAlogrithm(0xff112233));

        assertEquals(100, HelloC.abs(-100));
        int[] array = {
                0xffFFff, 0xffFFff, 0xffFFff, 0xffFFff,
                0xffFFff, 0xffFFff, 0xffFFff, 0xffFFff,
        };
        int[] array2 = {
                0x884422, 0x884422, 0x884422, 0x884422,
                0x884422, 0x884422, 0x884422, 0x884422,
        };
        assertEquals(false, HelloC.checkDiff(array, 0, 100));
        assertEquals(false, HelloC.checkDiff(array, 0xffffffff, 100));
        assertEquals(true, HelloC.checkDiff(array2, 0, 30));
        assertEquals(false, HelloC.checkDiff(array2, 0xFF111111, 100));
        assertEquals(true, HelloC.checkDiff(array2, 0xFF111111, 20));
    }
}
