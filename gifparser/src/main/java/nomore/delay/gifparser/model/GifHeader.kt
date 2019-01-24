package nomore.delay.gifparser.model

import java.util.*

class GifHeader {
    var signature: ByteArray = ByteArray(3) // magic number
    var version: ByteArray = ByteArray(3) // '87a' or '89a'

    override fun toString(): String {
        return "\nGifHeader(" +
                "signature=${Arrays.toString(signature)}," +
                "version=${Arrays.toString(version)}" +
                ")\n"
    }


}