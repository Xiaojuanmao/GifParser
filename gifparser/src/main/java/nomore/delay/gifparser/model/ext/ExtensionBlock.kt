package nomore.delay.gifparser.model.ext

open class ExtensionBlock {

    var extensionSeparator: Int = 0x21 // 扩展块标识符，值为0x21 8 bit
    var label: Int = 0 // 扩展块标识符 8 bit


    var blockTerminator: Int = 0 // 标记图形控制块的结束，固定值'0', 8 bit

}