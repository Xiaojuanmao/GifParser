package nomore.delay.gifparser.model

import nomore.delay.gifparser.toStrings
import java.util.*

class GifHeader {
    var signature: ByteArray = ByteArray(3) // magic number
    var version: ByteArray = ByteArray(3) // '87a' or '89a'

    override fun toString(): String {
        return "\nGifHeader(" +
                "signature=${signature.toStrings()}," +
                "version=${version.toStrings()}" +
                ")\n"
    }


}