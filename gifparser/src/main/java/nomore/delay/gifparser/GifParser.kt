package nomore.delay.gifparser

import nomore.delay.gifparser.model.GifHeader
import nomore.delay.gifparser.model.GifLogicScreenDescriptor
import java.io.*
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GifParser {

    companion object {

        private const val MAGIC_NUMBER = "GIF"
        private const val VERSION_87 = "87a"
        private const val VERSION_89 = "89a"

        private const val LABEL_EXT_PLAIN_TEXT = 0x01
        private const val LABEL_EXT_GRAPHIC_CONTROL = 0xF9
        private const val LABEL_EXT_COMMENT = 0xFE
        private const val LABEL_EXT_APPLICATION = 0xFF
        private const val LABEL_TRAILER = 0x3B // 文件结束

        private const val MASK_GLOBAL_COLOR_TABLE: Int = 0b10000000 // 全局颜色表
        private const val MASK_COLOR_RESOLUTION: Int = 0b01110000 // 色彩解析度
        private const val MASK_GLOBAL_COLOR_TABLE_SORT : Int = 0b00001000 // 全局颜色表排序
        private const val MASK_GLOBAL_COLOR_TABLE_SIZE : Int = 0b00000111 // 全局颜色表排序

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

    fun parseHeader() {
        checkData()

        rawData?.get(gifHeader.signature)
        rawData?.get(gifHeader.version)

        print(
            "parse header, " +
                    "signature: ${gifHeader.signature.toStrings()}, " +
                    "version: ${gifHeader.version.toStrings()}"
        )

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

        lsd.backgroundColorIndex = rawData?.get().nullOr(-1)
        lsd.pixelAspectRatio = rawData?.get().nullOr(-1)

        parseGlobalColorTable()
    }

    private fun parseGlobalColorTable() {
        val tableSize = lsd.globalColorTableSizeInByte
        lsd.globalColorTable = ByteArray(tableSize)
        rawData?.get(lsd.globalColorTable)
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


}