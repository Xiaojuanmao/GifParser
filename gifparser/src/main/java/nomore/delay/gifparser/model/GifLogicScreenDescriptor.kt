package nomore.delay.gifparser.model

class GifLogicScreenDescriptor {

    var widthInPx: Short = 0 // 逻辑屏幕宽 16 bit
    var heightInPx: Short = 0 // 逻辑屏幕高 16 bit
    var hasGlobalColorTable: Boolean = false // 是否有全局颜色表 1 bit
    var colorResolution: Int = 0 // 色彩解析度 3 bit
    var sortFlag: Boolean = false // 全局颜色表是否按序排列 1 bit
    var globalColorTableSizeInByte : Int = 0 // 全局颜色表的大小 3 bit
    var backgroundColorIndex: Byte = 0 // 背景色在全局颜色表的索引 8 bit
    var pixelAspectRatio: Byte = 0 // 用于计算原始图像中像素的宽高比的近似值 8 bit

    var globalColorTable: ByteArray? = null // 整个全局颜色表，大小为3的整数倍，RGB排列
}