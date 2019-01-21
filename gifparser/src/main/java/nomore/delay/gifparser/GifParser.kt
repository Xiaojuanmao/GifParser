package nomore.delay.gifparser

import nomore.delay.gifparser.model.GifHeader
import nomore.delay.gifparser.model.GifLogicScreenDescriptor
import nomore.delay.gifparser.model.ext.ApplicationExtension
import nomore.delay.gifparser.model.ext.GraphicControlExtension
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

        lsd.widthInPx = rawData?.short.nullOr(0)
        lsd.heightInPx = rawData?.short.nullOr(0)

        val multiParams = rawData?.get()?.toInt()
        lsd.hasGlobalColorTable = multiParams?.and(MASK_GLOBAL_COLOR_TABLE) == 1
        lsd.colorResolution = multiParams?.and(MASK_COLOR_RESOLUTION).nullOr(0)
        lsd.sortFlag = multiParams?.and(MASK_GLOBAL_COLOR_TABLE_SORT).nullOr(0) == 1
        lsd.globalColorTableSizeInByte = multiParams?.and(MASK_GLOBAL_COLOR_TABLE_SIZE).nullOr(0)

        lsd.backgroundColorIndex = if (lsd.hasGlobalColorTable) {
            rawData?.get().nullOr(-1)
        } else {
            0
        }
        lsd.pixelAspectRatio = rawData?.get().nullOr(-1)
        /**
         * 获取全局颜色表
         * 3*2^(Size of Global Color Table+1)
         */
        if (lsd.hasGlobalColorTable) {
            val tableSize = (3 * (lsd.globalColorTableSizeInByte + 1).toFloat().pow(2)).toInt()
            lsd.globalColorTable = ByteArray(tableSize)
            rawData?.get(lsd.globalColorTable)
        }

        print(lsd.toString())
    }

    private fun parseContents() {
        var done = false
        while (!done) {
            val next: Int? = rawData?.get()?.toInt()
            when (next) {
                LABEL_EXT_START -> {
                    val extLabel: Int? = rawData?.get()?.toInt()
                    when (extLabel) {

                        LABEL_EXT_GRAPHIC_CONTROL -> {
                            parseGraphicControlExt()
                        }

                        LABEL_EXT_APPLICATION -> {
                            parseApplicationExt()
                        }

                        LABEL_EXT_COMMENT -> {
                            print("\nskip comment extension")
                            skip()
                        }

                        LABEL_EXT_PLAIN_TEXT -> {
                            print("\nskip plain text")
                            skip()
                        }

                        else -> {
                            skip()
                        }
                    }
                }

                LABEL_GIF_FRAME -> {
                    print("\ngif frame")
                }

                LABEL_TRAILER -> {
                    print("\nfile trailer")
                    done = true
                }
                LABEL_EMPTY -> {
                    print("\nempty label")
                    // empty
                }
                else -> {

                }
            }
        }
    }

    private fun parseGraphicControlExt() {
        val graphicControlExtension = GraphicControlExtension()
        graphicControlExtension.blockSize = rawData?.get().nullOr(0)

        val multiParams = rawData?.get()?.toInt()
        graphicControlExtension.reservedFlag = multiParams?.and(MASK_GRAPHIC_CONTROL_RESERVED_FLAG).nullOr(0)
        graphicControlExtension.disposalMethod = multiParams?.and(MASK_GRAPHIC_CONTROL_DISPOSABLE_METHOD).nullOr(0)
        graphicControlExtension.userInputFlag = multiParams?.and(MASK_GRAPHIC_CONTROL_USER_INPUT) == 1
        graphicControlExtension.transparentFlag = multiParams?.and(MASK_GRAPHIC_CONTROL_TRANSPARENT) == 1

        graphicControlExtension.delayTime = rawData?.short?.toInt().nullOr(0)
        graphicControlExtension.transparentColorIndex = rawData?.get()?.toInt().nullOr(0)

        graphicControlExtension.blockTerminator = rawData?.get()?.toInt().nullOr(0)

        print(graphicControlExtension.toString())
    }

    private fun parseApplicationExt() {
        val applicationExt = ApplicationExtension()
        applicationExt.blockSize = rawData?.get().nullOr(-1)
        rawData?.get(applicationExt.identifier)
        rawData?.get(applicationExt.authenticationCode)

        // 获取计算出的二进制标识，知道ext结束
        val buffer = ByteBuffer.allocate(1)
        while (true) {
            val currentByte = rawData?.get()
            if (currentByte == null || currentByte.toInt() == 0) {
                // ext结束
                applicationExt.blockTerminator = 0
                break
            } else {
                buffer.put(currentByte)
            }
        }
        applicationExt.identifierCode = buffer.array()

        print(applicationExt.toString())
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
            blockSize = rawData?.get()?.toInt().nullOr(0)
            val newPosition = Math.min(rawData?.position().nullOr(0) + blockSize, rawData?.limit().nullOr(0))
            rawData?.position(newPosition)
        } while (blockSize > 0)
    }


}