package doyaaaaaken.main.boot.csvoutputter.feature_value

import doyaaaaaken.main.boot.csvoutputter.PrintWriterUser
import java.io.PrintWriter
import doyaaaaaken.main.db.DbSession
import doyaaaaaken.main.boot.Property
import doyaaaaaken.model.TraitFreqHistory
import doyaaaaaken.model.TraitFreqHistory.TraitFeatureValueDataRow
import java.sql.Connection

private[boot] object MaxFreqAveCsvOutputter extends PrintWriterUser {

  override def csvOutput(pw: PrintWriter, rankLimit: Int): Unit = {
    val con = DbSession.getConnection

    getAndOutputMaxFreqAverage(pw, con, None)
    println("全様式における、normal様式・hoped様式それぞれの最高到達度数平均出力完了")

    getAndOutputMaxFreqAverage(pw, con, Some(100))
    println("TOP100における、normal様式・hoped様式それぞれの最高到達度数平均出力完了")

    getAndOutputMaxFreqAverage(pw, con, Some(40))
    println("TOP40における、normal様式・hoped様式それぞれの最高到達度数平均出力完了")

    getAndOutputMaxFreqAverage(pw, con, Some(20))
    println("TOP20における、normal様式・hoped様式それぞれの最高到達度数平均出力完了")
  }

  /**
   * 「様式最高到達度数に関して、指定したランキングの様式群の特徴量平均値をDBからデータ取得し、それをCSVファイル出力する」
   * という共通処理を切り出したメソッド
   */
  private[this] def getAndOutputMaxFreqAverage(pw: PrintWriter, con: Connection, rankLimit: Option[Int]): Unit = {
    pw.println(if (rankLimit.isDefined) "TOP" + rankLimit.get else "全様式")
    for (simNum <- 1 to Property.simNum) {
      val rows: Seq[TraitFeatureValueDataRow] = TraitFreqHistory.selectMaxFreqAveForTopNTraits(con, rankLimit, simNum)
      val normalAve: Double = rows.filter(_.isHoped == false).map(_.average).apply(0)
      val hopedAve: Double = rows.filter(_.isHoped == true).map(_.average).apply(0)
      pw.println("Hoped様式," + Property.initialHopedPrefAvarage + ",に関して、Normal様式の最高到達度数平均：," + normalAve + ",Hoped様式の最高到達度数平均：," + hopedAve)
    }
    pw.println
  }
}