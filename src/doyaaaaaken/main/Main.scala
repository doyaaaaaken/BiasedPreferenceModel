package doyaaaaaken.main

import doyaaaaaken.main.boot.Boot
import doyaaaaaken.main.properties.Property
/**
 * シミュレーションの本骨組みとなるMainクラス
 */
object Main {
  def main(args: Array[String]): Unit = {
    println("＊＊＊＊＊＊シミュレーション開始＊＊＊＊＊＊")
    val prop: Property = Boot.start
    println(prop.simNum)
  }
}