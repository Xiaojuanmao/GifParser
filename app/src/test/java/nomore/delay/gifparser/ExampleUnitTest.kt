package nomore.delay.gifparser

import com.bumptech.glide.gifdecoder.StandardGifDecoder
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter
import com.bumptech.glide.load.resource.gif.GifBitmapProvider
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.*
import java.nio.ByteBuffer

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun testGlideGif() {
        val decoder = StandardGifDecoder(GifBitmapProvider(BitmapPoolAdapter()))

        val file = File("src/test/java/nomore/delay/gifparser/able_1.gif")
        if (!file.exists()) {
            throw IllegalStateException("The file: $file not exist")
        }
        val inputStream = FileInputStream(file)
        val rawData = inputStreamToByteArray(inputStream)
        rawData?.let {
            val status = decoder.read(it)
            print("frame count : ${decoder.frameCount}, is error : $status\n")
            for (i in 0..decoder.frameCount) {
                decoder.advance()
                val nextFrame = decoder.nextFrame
                print("frame index: ${decoder.currentFrameIndex}, frame width: ${nextFrame?.width}, frame height : ${nextFrame?.height}, state: ${decoder.status}\n")
            }
        }
    }

    fun wrapByteArray(byteArray: ByteArray?): ByteBuffer? {
        byteArray ?: return null
        return ByteBuffer.wrap(byteArray)
    }

    fun inputStreamToByteArray(inputStream: InputStream?): ByteArray? {
        inputStream ?: return null
        val bufferSize = 4 * 1024
        val outputStream = ByteArrayOutputStream(bufferSize)
        try {
            var readByteCount: Int
            val buffer = ByteArray(bufferSize)
            while (true) {
                readByteCount = inputStream.read(buffer)
                if (readByteCount == -1) {
                    break
                }
                outputStream.write(buffer, 0, readByteCount)
            }
            outputStream.flush()
        } catch (e: IOException) {
            return null
        }
        return outputStream.toByteArray()
    }
}
