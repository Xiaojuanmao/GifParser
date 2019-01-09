package nomore.delay.gifparser.model.ext

open class ExtensionBlock {

    var extensionSeparator: Byte = 0 // 扩展块标识符，值为0x21 8 bit
    var label: Byte = 0 // 扩展块标识符 8 bit


    var blockTerminator: Byte = 0 // 标记图形控制块的结束，固定值'0', 8 bit

}