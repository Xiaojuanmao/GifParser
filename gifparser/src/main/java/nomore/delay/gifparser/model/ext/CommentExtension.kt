package nomore.delay.gifparser.model.ext

/**
 * 89a 版本加入
 *
 * Comment Extension包含的文本信息并不是GIF数据流的实际图像中的一部分
 * 它适合于包括图像，信用，描述或者其它任意类型的非控制，非图像信息的评论
 * Comment Extension可能被解码器忽略，或者被保存等待后续处理
 * 任何情况下，Comment Extension不会打断或者干扰数据流的处理
 *
 * 标识这个块为一个Comment Extension，值为0xFE
 */

class CommentExtension : ExtensionBlock() {

    companion object {
        const val LABEL = 0xFE
    }

    var data: ByteArray? = null // 最多255字节，最少1字节，在数据前面有1个字节的大小信息

}