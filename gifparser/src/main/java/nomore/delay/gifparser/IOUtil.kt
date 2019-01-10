package nomore.delay.gifparser

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer

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