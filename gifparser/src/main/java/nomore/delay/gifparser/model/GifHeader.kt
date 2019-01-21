package nomore.delay.gifparser.model

import nomore.delay.gifparser.toStrings

class GifHeader {
    var signature: ByteArray = ByteArray(3) // magic number
    var version: ByteArray = ByteArray(3) // '87a' or '89a'

    override fun toString(): String {
        return "\n parse header\n" +
                "signature: ${signature.toStrings()}\n" +
                "version: ${version.toStrings()}\n"
    }
}