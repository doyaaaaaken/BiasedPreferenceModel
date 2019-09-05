package doyaaaaaken.main

import java.text.DecimalFormat

object Util {
  /**
   * Java 仮想マシンのメモリ総容量、使用量、
   * 使用を試みる最大メモリ容量の情報を返します。
   * @return Java 仮想マシンのメモリ情報
   */
  def getMemoryInfo(): String = {
    val f1: DecimalFormat = new DecimalFormat("#,###KB")
    val f2: DecimalFormat = new DecimalFormat("##.#")

    val free: Long = Runtime.getRuntime().freeMemory() / 1024
    val total: Long = Runtime.getRuntime().totalMemory() / 1024
    val max: Long = Runtime.getRuntime().maxMemory() / 1024

    val used: Long = total - free
    val ratio: Double = used * 100 / total

    "Java メモリ情報 : 合計=" + f1.format(total) + "、" +
      "使用量=" + f1.format(used) + " (" + f2.format(ratio) + "%)、" +
      "使用可能最大=" + f1.format(max)
  }
}