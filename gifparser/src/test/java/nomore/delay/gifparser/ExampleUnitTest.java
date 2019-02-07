package nomore.delay.gifparser;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        Byte b = Byte.valueOf("0xec", 16);
        System.out.println(Byte.toUnsignedInt(b));
    }

    @Test
    public void testGifHeader() {
        GifParser gifParser = new GifParser();
        gifParser.init("src/test/java/nomore/delay/gifparser/able_1.gif");
        gifParser.parse();

    }
}