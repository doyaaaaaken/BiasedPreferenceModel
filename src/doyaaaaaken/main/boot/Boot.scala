package doyaaaaaken.main.boot

import doyaaaaaken.main.db.DbSession

/**
 * シミュレーションの節目で必要な処理
 */
object Boot {

  def start(): Unit = {
    println("＊＊＊＊＊＊シミュレーション開始＊＊＊＊＊＊")
    PropertyReader.read
    DbSession.open
    if (Property.dbClearBeforeSim) DbSession.clearData
  }

  def finish(): Unit = {
    if (Property.csvOutputAfterSim) CsvOutputter.work
    DbSession.close
    println("＊＊＊＊＊＊シミュレーション終了＊＊＊＊＊＊")
  }
}