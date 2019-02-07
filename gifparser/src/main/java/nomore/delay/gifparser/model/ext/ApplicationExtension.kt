package nomore.delay.gifparser.model.ext

import nomore.delay.gifparser.model.DataBlock
import nomore.delay.gifparser.nullOr
import nomore.delay.gifparser.toStrings

class ApplicationExtension : ExtensionBlock() {

    var blockSize: Int = 11 // 在扩展中的字节数，从Block Size字段后开始知道但是不包括data部分的开始。这个域值为12, 8 bit
    var identifier = ByteArray(8) // 8个可以打印的ASCII字符用来标识拥有这个Application Extension的应用, 8 byte
    var authenticationCode = ByteArray(3) // 3字节用来认证

    var applicationBlocks: List<DataBlock>? = null

    init {
        label = 0xFF
    }

    override fun toString(): String {
        return "\nApplicationExtension(" +
                "blockSize=$blockSize," +
                "identifier=${identifier.toStrings()}," +
                "authenticationCode=${authenticationCode.toStrings()}," +
                "applicationBlocksSize=${applicationBlocks?.size.nullOr(0)}" +
                ")\n"
    }


}