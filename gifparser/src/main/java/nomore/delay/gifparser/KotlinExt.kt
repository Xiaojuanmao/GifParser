package nomore.delay.gifparser

import java.nio.charset.Charset

fun ByteArray?.toStrings(): String? {
    this ?: return null
    return this.toString(Charset.defaultCharset())
}

fun <T> T?.nullOr(default: T): T {
    return this ?: default
}