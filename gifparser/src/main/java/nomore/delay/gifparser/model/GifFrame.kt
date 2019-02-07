package nomore.delay.gifparser.model

import nomore.delay.gifparser.nullOr

class GifFrame {

    var frameSeparator: Int = 0x2C // 图像标识符以',' 值为0x2C 开头 8 bit
    var translationX: Int = 0 // X方向偏移量 16 bit
    var translationY: Int = 0 // Y方向偏移量 16 bit
    var frameWidth: Int = 0 // 帧宽 16 bit
    var frameHeight: Int = 0 // 帧高 16 bit

    var hasLocalColorTable: Boolean = false // 是否有局部颜色表 1 bit
    var interfaceFlag: Boolean = false // 图像是否为隔行扫描，图像以四遍交错模式交织 1 bit
    var sortFlag: Boolean = false // 标识Local Color Table是否被排序 1 bit
    var preserved: Boolean = false // 保留位 1 bit
    var localColorTableSize: Int = 0 // 局部颜色表大小 4 bit

    var localColorTable: ByteArray? = null // 局部颜色表，大小为3的整数倍，RGB排列

    var lzwInitSize: Int = 0 // 图像数据中的LZW码的初始字节数
    var dataBlocks: List<DataBlock>? = null // 图像数据

    var blockTerminator: Byte = 0 // block结束符

    override fun toString(): String {
        return "\nGifFrame(" +
                "frameSeparator=$frameSeparator," +
                "translationX=$translationX," +
                "translationY=$translationY," +
                "frameWidth=$frameWidth," +
                "frameHeight=$frameHeight," +
                "hasLocalColorTable=$hasLocalColorTable," +
                "interfaceFlag=$interfaceFlag," +
                "sortFlag=$sortFlag," +
                "preserved=$preserved," +
                "localColorTableSize=$localColorTableSize," +
                "lzwInitSize=$lzwInitSize," +
                "dataBlocksSize=${dataBlocks?.size.nullOr(0)}," +
                "blockTerminator=$blockTerminator" +
                ")\n"
    }


}