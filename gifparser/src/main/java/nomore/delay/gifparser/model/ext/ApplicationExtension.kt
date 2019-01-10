package nomore.delay.gifparser.model.ext

class ApplicationExtension : ExtensionBlock() {

    var blockSize: Byte = 11 // 在扩展中的字节数，从Block Size字段后开始知道但是不包括data部分的开始。这个域值为12, 8 bit
    var identifier: ByteArray? = null // 8个可以打印的ASCII字符用来标识拥有这个Application Extension的应用, 8 byte
    var authenticationCode: ByteArray? = null // 3字节用来认证
    var identifierCode: ByteArray? = null // 一个应用程序可以使用一个算法来计算一个二进制码唯一标识其为拥有这个Application

}