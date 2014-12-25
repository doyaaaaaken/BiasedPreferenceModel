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
    }
  }

  def refresh(antiConformThreshold: Double): Unit = {
    //DBの値をクリアする
    if (Property.dbClearBeforeSim) DbSession.clearData
    //変数をセットし直す
    resetVariables(antiConformThreshold)
  }

  def output(outputId: Int): Unit = {
    if (Property.csvOutputTopNTrait) CsvOutputter.outputTopNTrait(outputId)
    if (Property.csvOutputPreferenceHistoryForOneTrait) CsvOutputter.outputPreferenceHistoryForOneTrait(outputId)
    if (Property.csvOutputTraitLifeSpanFreq) CsvOutputter.outputTraitLifeSpanFreq(outputId)
    if (Property.csvOutputTraitFreqHisory) CsvOutputter.outputTraitFreqHistory(outputId)
  }

  def finish(): Unit = {
    DbSession.close
    println("＊＊＊＊＊＊シミュレーション終了＊＊＊＊＊＊")
    println("終了時刻：" + new Date().toString)
  }

  private[this] def resetVariables(antiConformThreshold: Double): Unit = {
    Property.setAntiConformThreshold(antiConformThreshold)
  }
}