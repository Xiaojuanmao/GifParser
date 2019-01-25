package nomore.delay.gifparser.model.ext

import nomore.delay.gifparser.model.DataBlock
import nomore.delay.gifparser.nullOr

/**
 * 89a 版本加入
 *
 * Plain Text Extension包含文本数据和需要的能够将这些数据渲染为图像的信息的简单格式
 * 文本数据应当编码为7字节能够打印出来的ASCII字符
 * 文本数据使 用由块字段中的参数定义的字符单元网格来呈现
 * 每个字符用一个单独的单元来渲染
 * 快中的文本字符被渲染为单间隔字符，每个单元格一个字符，有最适合的字体 和大小
 * 从块的数据部分顺序地去除数据自负并在单元内呈现，从网格中的坐上单元开始并且从左到右并从上到下地进行
 * 呈现文本数据，直到到达数据的末尾或者 字符网格被填充完
 * 在单元尺寸不允许非整数的时候，必须要丢弃分数单元
 * 编码器必须要小心精确地指定网格单元，以避免这样的情况发生
 *
 *
 * 这个块需要Global Color Table存在，这个块使用的颜色来自流中的Global Color Table
 * 如果其存在的话。这个块是一个图像渲染块，因此能够被一个Graphic Control Extension修改
 * 这个块是可选的，任意数量的Plain Text Extension能够出现在数据流中。
 *
 * 标识符为 0x01
 */
class PlainTextExtension : ExtensionBlock() {

    var blockSize: Int = 0x0c // 在扩展中的字节数，从Block Size字段后开始知道但是不包括data部分的开始。这个域值为12, 8 bit
    var textLeftPosition: Int = 0 // 文字距离屏幕左侧边距, 单位为像素 16 bit
    var textTopPosition: Int = 0 // 文字距离屏幕顶部边距, 单位为像素, 16 bit
    var textWidth: Int = 0 // 文本框宽度, 16 bit
    var textHeight: Int = 0 // 文本框高度, 16 bit
    var characterWidth: Int = 0 // 字符宽度, 8 bit
    var characterHeight: Int = 0 // 字符高度, 8 bit
    var textForegroundColorIndex: Int = 0 // 文字前景色在全局颜色表的索引，8 bit
    var textBackgroundColorIndex: Int = 0 // 文字背景色在全局颜色表的索引，8 bit

    var textBlocks: List<DataBlock>? = null // 文字内容, 最多为255，最少为1，在每个子块之前有一个字节标记块的大小

    init {
        label = 0x01
    }

    override fun toString(): String {
        return "\nPlainTextExtension(" +
                "blockSize=$blockSize," +
                "textLeftPosition=$textLeftPosition," +
                "textTopPosition=$textTopPosition," +
                "textWidth=$textWidth," +
                "textHeight=$textHeight," +
                "characterWidth=$characterWidth," +
                "characterHeight=$characterHeight," +
                "textForegroundColorIndex=$textForegroundColorIndex," +
                "textBackgroundColorIndex=$textBackgroundColorIndex," +
                "textBlocksSize=${textBlocks?.size.nullOr(0)}," +
                ")\n"
    }


}