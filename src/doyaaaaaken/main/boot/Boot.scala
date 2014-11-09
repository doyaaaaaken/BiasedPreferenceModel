package doyaaaaaken.main.boot

import doyaaaaaken.main.db.DbSession
import doyaaaaaken.main.boot.csvoutputter.CsvOutputter
import java.util.Date

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
    if (Property.csvOutputTopNTrait) CsvOutputter.outputTopNTrait
    if (Property.csvOutputTopNTrait) CsvOutputter.outputTop40Trait //偽装しているメソッド
    if (Property.csvOutputTopNTrait) CsvOutputter.outputTop20Trait //偽装しているメソッド
    if (Property.csvOutputPreferenceHistoryForOneTrait) CsvOutputter.outputPreferenceHistoryForOneTrait
    if (Property.csvOutputTraitLifeSpanFreq) CsvOutputter.outputTraitLifeSpanFreq
    if (Property.csvOutputTraitFreqHisory) CsvOutputter.outputTraitFreqHistory
    DbSession.close
    println("＊＊＊＊＊＊シミュレーション終了＊＊＊＊＊＊")
    println("終了時刻：" + new Date().toString)
  }
}