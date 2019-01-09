package nomore.delay.gifparser.model

class GifFrame {

    var frameSeparator: Byte = 0 // 图像标识符以',' 值为0x2C 开头 8 bit
    var translationX: Short = 0 // X方向偏移量 16 bit
    var translationY: Short = 0 // Y方向偏移量 16 bit
    var frameWidth: Short = 0 // 帧宽 16 bit
    var frameHeight: Short = 0 // 帧高 16 bit

    var hasLocalColorTable: Boolean = false // 是否有局部颜色表 1 bit
    var interfaceFlag: Boolean = false // 图像是否为隔行扫描，图像以四遍交错模式交织 1 bit
    var sortFlag: Boolean = false // 标识Local Color Table是否被排序 1 bit
    var preserved: Boolean = false // 保留位 1 bit
    var localColorTableSize: Int = 0 // 局部颜色表大小 4 bit

    var localColorTable: ByteArray? = null // 局部颜色表，大小为3的整数倍，RGB排列

    var lzwInitSize: Byte = 0 // 图像数据中的LZW码的初始字节数
    var dataBlocks: List<DataBlock>? = null // 图像数据

}