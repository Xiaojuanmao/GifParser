package nomore.delay.gifparser

import java.nio.charset.Charset

fun ByteArray?.toStrings(): String? {
    this ?: return null
    return this.toString(Charset.defaultCharset())
}

fun ByteArray?.toHexString(): String? {
    val stringBuilder = StringBuilder("")
    if (this == null || this.isEmpty()) {
        return null
    }
    for (i in 0 until this.size) {
        val v = this[i].toInt() and 0xFF
        val hv = Integer.toHexString(v)
        if (hv.length < 2) {
            stringBuilder.append(0)
        }
        stringBuilder.append(hv).append(' ')
    }
    return stringBuilder.toString()
}

fun Byte?.toHexString(): String? {
    this ?: return null
    val value = this.toInt() and 0xFF
    val hexValue = Integer.toHexString(value)
    val stringBuilder = StringBuilder("")
    if (hexValue.length < 2) {
        stringBuilder.append(0)
    }
    stringBuilder.append(hexValue)
    return stringBuilder.toString()
}

fun <T> T?.nullOr(default: T): T {
    return this ?: default
}