package nomore.delay.gifparser.model.ext

/**
 * 89a 版本加入
 *
 * 图形控制扩展模块
 * 在Gif图片每一帧之前出现，最多出现一次
 *
 * 图形控制扩展块标识符，0xF9
 */
class GraphicControlExtension : ExtensionBlock() {

    var blockSize: Byte = 4 // 块大小，固定值4 8 bit

    var reservedFlag: Int = 0 // 保留位，3 bit
    /**
     * 0 – 未指定，解码器不需要做任何动作，这个选项可以将一个全尺寸，非透明框架替换为另一个。
     * 1 – 不要处置，在此选项中，未被下一帧覆盖的任何像素继续显示。
     * 2 – 还原为背景图像，被图像使用的区域必须还原为背景颜色。
     * 3 – 还原为上一个，解码器必须还原被图像覆盖的区域为之前渲染的图像。
     * 4-7 – 未定义。
     */
    var disposalMethod: Int = 0 // 展示图像之后的处理，3 bit
    /**
     * 0 – 不需要用户输入。
     * 1 – 需要用户输入。
     * 当Delay Time被使用并且用户输入设置为需要用户输入的时候，处理将会在得到用户输入或者延迟时间到期任一满足之后继续。
     */
    var userInputFlag: Boolean = false // 在继续下一帧之前，是否需要等待用户输入, 1bit
    var transparentFlag: Boolean = false // 是否在Transparent Index字段给出一个透明度索引, 1 bit

    var delayTime: Int = 0 // 距离下一帧的延迟，单位 1/100 秒, 16 bit
    var transparentColorIndex: Int = 0 // 透明度索引值, 8 bit

    init {
        label = 0xF9
    }

    override fun toString(): String {
        return "\nparse graphic control extension\n" +
                "label : $label\n" +
                "reservedFlag : $reservedFlag\n" +
                "disposalMethod : $disposalMethod\n" +
                "userInputFlag : $userInputFlag\n" +
                "transparentFlag : $transparentFlag\n" +
                "delayTime : $delayTime\n" +
                "transparentColorIndex : $transparentColorIndex\n"
    }

}