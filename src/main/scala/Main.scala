import com.github.luben.zstd._

object Main {
  def main(args: Array[String]): Unit = {

    val input        = "Hello World".getBytes("UTF-8")
    val compressed   = Zstd.compress(input)
    val decompressed = Zstd.decompress(compressed, 10)

    println(new String(decompressed, "UTF-8"))
  }
}
