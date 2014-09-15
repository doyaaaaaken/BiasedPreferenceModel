package doyaaaaaken.main.boot

import doyaaaaaken.main.properties.PropertyReader
import doyaaaaaken.main.properties.Property
/**
 * シミュレーションの節目で必要な処理
 */
object Boot {

  /**
   * シミュレーション開始時に必要な処理
   * 注：現時点ではPropertyファイルのみを返す設定にしている
   */
  def start(): Unit = {
    println("＊＊＊＊＊＊シミュレーション開始＊＊＊＊＊＊")
    PropertyReader.read
  }
}