import com.github.luben.zstd.{ Zstd, ZstdInputStream }

import java.io.{ File, FileInputStream, FileOutputStream, RandomAccessFile }
import java.nio.ByteBuffer
import scala.util.control.Breaks.break

object ZstdUtils {
  def compress(info: String): String =
    info

  def decompress(info: String): String =
    info

  def compressFile(inFile: String, outFolder: String, compressionLevel: Int): Long = {
    val file     = new File(inFile)
    val outFile  = new File(outFolder, file.getName + ".zs")
    var numBytes = 0L
    val inBuffer = ByteBuffer.allocateDirect(8 * 1024 * 1024) //要被压缩的字节缓冲区

    val compressedBuffer = ByteBuffer.allocateDirect(8 * 1024 * 1024) //压缩后放置到该缓冲区

    try {
      val inRaFile   = new RandomAccessFile(file, "r")
      //读取文件
      val outRaFile  = new RandomAccessFile(outFile, "rw")
      val inChannel  = inRaFile.getChannel
      //通道
      val outChannel = outRaFile.getChannel
      try {
        inBuffer.clear
        while (inChannel.read(inBuffer) > 0) { //当文件还有字节未压缩时
          inBuffer.flip                        //反转缓冲区的读写模式

          compressedBuffer.clear
          //将 inBuffer的0-inBuffer.limit()压缩到compressedBuffer的0-compressedBuffer.capacity()。
          val compressedSize = Zstd.compressDirectByteBuffer(
            compressedBuffer,
            0,
            compressedBuffer.capacity,
            inBuffer,
            0,
            inBuffer.limit,
            compressionLevel
          )
          numBytes += compressedSize
          compressedBuffer.position(compressedSize.toInt) //

          compressedBuffer.flip
          outChannel.write(compressedBuffer) //把压缩后得到的缓冲区写入文件输出通道

          inBuffer.clear
        }
      } finally {
        if (inRaFile != null) inRaFile.close()
        if (outRaFile != null) outRaFile.close()
        if (inChannel != null) inChannel.close()
        if (outChannel != null) outChannel.close()
      }
    }
    numBytes
  }

  def decompressFile(afterCompressFile: String, decompressFile: String): Unit = {
    val file = new File(afterCompressFile) //待解压文件

    val out = new File("xxx") //解压后文件

    val buffer                       = new Array[Byte](1024 * 1024 * 8)
    var fo: Option[FileOutputStream] = None
    var fi: Option[FileInputStream]  = None
    var zs: Option[ZstdInputStream]  = None
    try {
      fo = Some(new FileOutputStream(out))
      fi = Some(new FileInputStream(file.getPath))
      zs = Some(new ZstdInputStream(fi.getOrElse(throw new Exception("failed")))) //将文件输入流复制到zs

      while (true) {
        val count = zs.get.read(buffer, 0, buffer.length) //zs中重写了read方法，该方法包含解压过程，将0-buffer.length读入buffer

        if (count == -1) break         //todo: break is not supported
        fo.get.write(buffer, 0, count) //将buffer中的0-count写入文件输出流

      }
      fo.get.flush()
    } catch {
      case e: Throwable =>
        e.printStackTrace()
    } finally {
      if (zs != null)
        try zs.get.close()
        catch {
          case x: Exception =>

        }
      if (fi != null)
        try fi.get.close()
        catch {
          case x: Exception =>

        }
      if (fo != null)
        try fo.get.close()
        catch {
          case x: Exception =>

        }
    }
  }

}
