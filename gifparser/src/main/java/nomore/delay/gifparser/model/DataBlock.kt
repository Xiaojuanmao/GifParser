package nomore.delay.gifparser.model


/**
 * 数据块
 */
class DataBlock {

    var blockSize: Int = 0 // 块大小, 1 byte
    var datas: ByteArray? = null // 数据本身，最大为255字节

}