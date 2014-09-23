package doyaaaaaken.main.boot

import doyaaaaaken.main.db.DbSession

/**
 * シミュレーションの節目で必要な処理
 */
object Boot {

  def start(useDb: Boolean): Unit = {
    println("＊＊＊＊＊＊シミュレーション開始＊＊＊＊＊＊")
    PropertyReader.read
    if (useDb) {
      DbSession.open
      if (Property.dbClearBeforeSim) DbSession.clearData
    }
  }

  def finish(): Unit = {
    if (Property.csvOutputTraitFreqHisory) CsvOutputter.outputTraitFreqHistory
    if (Property.csvOutputTopNTrait) CsvOutputter.outputTopNTrait
    DbSession.close
    println("＊＊＊＊＊＊シミュレーション終了＊＊＊＊＊＊")
  }
}