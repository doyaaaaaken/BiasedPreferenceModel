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

  def refresh(initialHopedPrefAve: Double): Unit = {
    //DBの値をクリアする
    if (Property.dbClearBeforeSim) DbSession.clearData
    //変数をセットし直す
    resetVariables(initialHopedPrefAve)
  }

  def output(): Unit = {
    if (Property.csvOutputTopNTrait) {
      //      CsvOutputter.outputTopNHopedTraitNum //特徴量についてTopNの様式群のうち、Hopedな様式の数を出力する
      CsvOutputter.outputNormalHopedFeatureValue //Normal様式群とHoped様式群の特徴量を、様式全体・特徴量に関してTop100,40,20の様式群 から抽出して出力する
    }
  }

  def finish(): Unit = {
    DbSession.close
    println("＊＊＊＊＊＊シミュレーション終了＊＊＊＊＊＊")
    println("終了時刻：" + new Date().toString)
  }

  private[this] def resetVariables(initialHopedPrefAve: Double): Unit = {
    Property.setInitialHopedPrefAvarage(initialHopedPrefAve)
  }
}