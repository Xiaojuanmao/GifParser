package nomore.delay.gifparser.model

class GifHeader {
    var signature: ByteArray = ByteArray(3) // magic number
    var version: ByteArray = ByteArray(3) // '87a' or '89a'
}