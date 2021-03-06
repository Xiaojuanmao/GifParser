package nomore.delay.gifparser

import nomore.delay.gifparser.model.DataBlock
import nomore.delay.gifparser.model.GifFrame
import nomore.delay.gifparser.model.GifHeader
import nomore.delay.gifparser.model.GifLogicScreenDescriptor
import nomore.delay.gifparser.model.ext.ApplicationExtension
import nomore.delay.gifparser.model.ext.CommentExtension
import nomore.delay.gifparser.model.ext.GraphicControlExtension
import nomore.delay.gifparser.model.ext.PlainTextExtension
import java.io.*
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

class GifParser {

    companion object {

        private const val MAGIC_NUMBER = "GIF"
        private const val VERSION_87 = "87a"
        private const val VERSION_89 = "89a"

        private const val LABEL_EXT_START = 0x21 // 扩展块标识符
        private const val LABEL_EXT_PLAIN_TEXT = 0x01
        private const val LABEL_EXT_GRAPHIC_CONTROL = 0xF9
        private const val LABEL_EXT_COMMENT = 0xFE
        private const val LABEL_EXT_APPLICATION = 0xFF

        private const val LABEL_GIF_FRAME = 0x2C // 帧数据
        private const val LABEL_TRAILER = 0x3B // 文件结束
        private const val LABEL_EMPTY = 0x00

        private const val MASK_GLOBAL_COLOR_TABLE: Int = 0b10000000 // 全局颜色表
        private const val MASK_COLOR_RESOLUTION: Int = 0b01110000 // 色彩解析度
        private const val MASK_GLOBAL_COLOR_TABLE_SORT: Int = 0b00001000 // 全局颜色表排序
        private const val MASK_GLOBAL_COLOR_TABLE_SIZE: Int = 0b00000111 // 全局颜色表排序

        private const val MASK_GRAPHIC_CONTROL_RESERVED_FLAG: Int = 0b11100000 // 图形控制扩展标识保留位
        private const val MASK_GRAPHIC_CONTROL_DISPOSABLE_METHOD: Int = 0b00011100 // 图形控制扩展标识处理方式
        private const val MASK_GRAPHIC_CONTROL_USER_INPUT: Int = 0b00000010 // 图形控制扩展标识用户输入
        private const val MASK_GRAPHIC_CONTROL_TRANSPARENT: Int = 0b00000001 // 图形控制扩展标识透明度索引

        private const val MASK_GIF_FRAME_HAS_LOCAL_COLOR_TABLE: Int = 0b10000000 // 是否有局部颜色表
        private const val MASK_GIF_FRAME_INTERFACE_FLAG: Int = 0b01000000 // 是否为隔行扫描
        private const val MASK_GIF_FRAME_LOCAL_COLOR_TABLE_SORT_FLAG: Int = 0b00100000 // 局部颜色表是否被排序
        private const val MASK_GIF_FRAME_PERSERVED: Int = 0b00010000 // 保留位
        private const val MASK_GIF_FRAME_LOCAL_COLOR_TABLE_SIZE: Int = 0b00001111 // 局部颜色表大小
    }

    private val gifHeader by lazy { GifHeader() }
    private val lsd by lazy { GifLogicScreenDescriptor() }

    private var rawData: ByteBuffer? = null

    fun init(filePath: String?) {

        if (filePath.isNullOrEmpty()) {
            throw IllegalArgumentException("The file path is null or empty")
        }

        print("init file path : $filePath")

        val file = File(filePath)
        if (!file.exists()) {
            throw IllegalStateException("The file: $file not exist")
        }
        val inputStream = FileInputStream(file)
        rawData = wrapByteArray(inputStreamToByteArray(inputStream))
        rawData?.asReadOnlyBuffer()
        rawData?.position(0)
        rawData = rawData?.order(ByteOrder.LITTLE_ENDIAN)
        inputStream.close()
    }

    fun parse() {
        parseHeader()
        parseLSD()
        parseContents()
    }

    fun parseHeader() {
        checkData()

        rawData?.get(gifHeader.signature)
        rawData?.get(gifHeader.version)

        print(gifHeader.toString())

        checkSignature()
        checkVersion()
    }

    fun parseLSD() {
        checkData()

        lsd.widthInPx = readShort()
        lsd.heightInPx = readShort()

        val multiParams = readByte()
        lsd.hasGlobalColorTable = multiParams.and(MASK_GLOBAL_COLOR_TABLE) != 0
        lsd.colorResolution = multiParams.and(MASK_COLOR_RESOLUTION).nullOr(0)
        lsd.sortFlag = multiParams.and(MASK_GLOBAL_COLOR_TABLE_SORT).nullOr(0) != 0
        val sizeInByte = multiParams.and(MASK_GLOBAL_COLOR_TABLE_SIZE).nullOr(0)
        lsd.globalColorTableSizeInByte = calculateColorSize(sizeInByte)
        lsd.backgroundColorIndex = readByte()
        lsd.pixelAspectRatio = readByte()
        /**
         * 获取全局颜色表
         * 3*2^(Size of Global Color Table+1)
         */
        if (lsd.hasGlobalColorTable) {
            lsd.globalColorTable = readColorTable(lsd.globalColorTableSizeInByte)
        }
        print(lsd.toString())
    }

    private fun parseContents() {
        while (true) {
            if (!rawData?.hasRemaining().nullOr(false)) {
                break
            }
            val next = readByte()
            when (next) {
                LABEL_EXT_START -> {
                    val extLabel: Int? = readByte()
                    when (extLabel) {

                        LABEL_EXT_GRAPHIC_CONTROL -> {
                            parseGraphicControlExt()
                        }

                        LABEL_EXT_APPLICATION -> {
                            parseApplicationExt()
                        }

                        LABEL_EXT_COMMENT -> {
                            parseCommentExt()
                        }

                        LABEL_EXT_PLAIN_TEXT -> {
                            parsePlainTextExt()
                        }

                        else -> {
                            skip()
                        }
                    }
                }

                LABEL_GIF_FRAME -> {
                    parseGifFrames()
                }

                LABEL_TRAILER -> {
                }
                LABEL_EMPTY -> {
                }
                else -> {
                }
            }
        }
    }

    private fun parseGraphicControlExt() {
        val graphicControlExtension = GraphicControlExtension()
        graphicControlExtension.blockSize = readByte()

        val multiParams = readByte()
        graphicControlExtension.reservedFlag = multiParams.and(MASK_GRAPHIC_CONTROL_RESERVED_FLAG).nullOr(0)
        graphicControlExtension.disposalMethod = multiParams.and(MASK_GRAPHIC_CONTROL_DISPOSABLE_METHOD).nullOr(0)
        graphicControlExtension.userInputFlag = multiParams.and(MASK_GRAPHIC_CONTROL_USER_INPUT) != 0
        graphicControlExtension.transparentFlag = multiParams.and(MASK_GRAPHIC_CONTROL_TRANSPARENT) != 0

        graphicControlExtension.delayTime = readShort()
        graphicControlExtension.transparentColorIndex = readByte()

        graphicControlExtension.blockTerminator = readByte()

        print(graphicControlExtension.toString())
    }

    private fun parseApplicationExt() {
        val applicationExt = ApplicationExtension()
        applicationExt.blockSize = readByte()
        rawData?.get(applicationExt.identifier)
        rawData?.get(applicationExt.authenticationCode)
        applicationExt.applicationBlocks = readDataBlocks()

        print(applicationExt.toString())
    }

    private fun parseCommentExt() {
        val commentExtension = CommentExtension()
        commentExtension.commentBlock = readDataBlocks()

        print(commentExtension)
    }

    private fun parseGifFrames() {
        val gifFrame = GifFrame()
        gifFrame.translationX = readShort()
        gifFrame.translationY = readShort()
        gifFrame.frameWidth = readShort()
        gifFrame.frameHeight = readShort()

        val multiParams = readByte()
        gifFrame.hasLocalColorTable = multiParams.and(MASK_GIF_FRAME_HAS_LOCAL_COLOR_TABLE) != 0
        gifFrame.interfaceFlag = multiParams.and(MASK_GIF_FRAME_INTERFACE_FLAG) != 0
        gifFrame.sortFlag = multiParams.and(MASK_GIF_FRAME_LOCAL_COLOR_TABLE_SORT_FLAG) != 0
        gifFrame.preserved = multiParams.and(MASK_GIF_FRAME_PERSERVED) != 0

        val sizeInByte = multiParams.and(MASK_GIF_FRAME_LOCAL_COLOR_TABLE_SIZE).nullOr(0)
        gifFrame.localColorTableSize = calculateColorSize(sizeInByte)
        // local color table
        if (gifFrame.hasLocalColorTable) {
            gifFrame.localColorTable = readColorTable(gifFrame.localColorTableSize)
        } else {
            gifFrame.localColorTable = null
        }

        gifFrame.lzwInitSize = readByte()
        gifFrame.dataBlocks = readDataBlocks()

        print(gifFrame)
    }

    private fun parsePlainTextExt() {
        val plainTextExtension = PlainTextExtension()
        plainTextExtension.blockSize = readByte()
        plainTextExtension.textLeftPosition = readShort()
        plainTextExtension.textTopPosition = readShort()
        plainTextExtension.textWidth = readShort()
        plainTextExtension.textHeight = readShort()
        plainTextExtension.characterWidth = readByte()
        plainTextExtension.characterHeight = readByte()
        plainTextExtension.textForegroundColorIndex = readByte()
        plainTextExtension.textBackgroundColorIndex = readByte()
        plainTextExtension.textBlocks = readDataBlocks()

        print(plainTextExtension)

    }

    private fun calculateColorSize(size: Int): Int {
        return if (size > 0) {
            ((size + 1).toFloat().pow(2)).toInt()
        } else {
            0
        }
    }

    private fun readColorTable(tableSize: Int): ByteArray? {
        val colorTable = ByteArray(3 * tableSize)
        print("color table size : $tableSize")
        rawData?.get(colorTable)
        return colorTable
    }

    private fun readDataBlocks(): List<DataBlock>? {
        val frameBlockList = mutableListOf<DataBlock>()
        while (true) {
            val blockSize = readByte()
            if (blockSize <= 0) {
                // ext结束
                break
            } else {
                // read one frame data block
                val dataBlock = DataBlock()
                dataBlock.blockSize = blockSize
                val byteArray = ByteArray(dataBlock.blockSize)
                rawData?.get(byteArray)
                dataBlock.datas = byteArray
                frameBlockList.add(dataBlock)
            }
        }
        return frameBlockList
    }

    private fun checkData() {
        if (rawData == null) {
            throw IllegalStateException("The raw data is null or empty")
        }
    }

    private fun checkSignature() {
        val magicNumber = gifHeader.signature.toStrings()
        if (magicNumber != MAGIC_NUMBER) {
            throw IllegalStateException("error magic number: $magicNumber")
        }
    }

    private fun checkVersion() {
        val version = gifHeader.version.toStrings()
        if (version != VERSION_87 && version != VERSION_89) {
            throw IllegalStateException("error gif version: $version")
        }
    }

    private fun skip() {
        var blockSize: Int
        do {
            blockSize = readByte()
            val newPosition = Math.min(
                rawData?.position().nullOr(0) + blockSize,
                rawData?.limit().nullOr(0)
            )
            rawData?.position(newPosition)
        } while (blockSize > 0)
    }

    private fun readByte(): Int {
        return byteToInt(rawData?.get().nullOr(0))
    }

    private fun readShort(): Int {
        return shortToInt(rawData?.short.nullOr(0))
    }

    private fun byteToInt(byte: Byte): Int {
        return java.lang.Byte.toUnsignedInt(byte)
    }

    private fun shortToInt(short: Short): Int {
        return java.lang.Short.toUnsignedInt(short)
    }

}