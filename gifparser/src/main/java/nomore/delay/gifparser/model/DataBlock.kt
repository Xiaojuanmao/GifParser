package nomore.delay.gifparser.model

/**
 * Gif图每一帧的数据块
 */
class DataBlock {

    var blockSize: Byte = 0 // 块大小，最大为255字节
    var datas: ByteArray? = null // 数据本身

}