package doyaaaaaken.main.boot.csvoutputter.feature_value

import doyaaaaaken.main.boot.csvoutputter.PrintWriterUser
import java.io.PrintWriter
import doyaaaaaken.main.db.DbSession
import doyaaaaaken.main.boot.Property
import doyaaaaaken.model.TraitFreqHistory
import doyaaaaaken.model.TraitFreqHistory.TraitFeatureValueDataRow
import java.sql.Connection

private[boot] object CumulativeFreqAveCsvOutputter extends PrintWriterUser {

  override def csvOutput(pw: PrintWriter, rankLimit: Int): Unit = {
    val con = DbSession.getConnection

    getAndOutputMaxFreqAverage(pw, con, None)
    println("全様式における、normal様式・hoped様式それぞれの累積採用度数平均出力完了")

    getAndOutputMaxFreqAverage(pw, con, Some(100))
    println("TOP100における、normal様式・hoped様式それぞれの累積採用度数平均出力完了")

    getAndOutputMaxFreqAverage(pw, con, Some(40))
    println("TOP40における、normal様式・hoped様式それぞれの累積採用度数平均出力完了")

    getAndOutputMaxFreqAverage(pw, con, Some(20))
    println("TOP20における、normal様式・hoped様式それぞれの累積採用度数平均出力完了")
  }

  /**
   * 「様式最高到達度数に関して、指定したランキングの様式群の特徴量平均値をDBからデータ取得し、それをCSVファイル出力する」
   * という共通処理を切り出したメソッド
   */
  private[this] def getAndOutputMaxFreqAverage(pw: PrintWriter, con: Connection, rankLimit: Option[Int]): Unit = {
    pw.println(if (rankLimit.isDefined) "TOP" + rankLimit.get else "全様式")
    for (simNum <- 1 to Property.simNum) {
      val rows: Seq[TraitFeatureValueDataRow] = TraitFreqHistory.selectCumulativeFreqAveForTopNTraits(con, rankLimit, simNum)
      val normalAve: Option[Double] = rows.filter(_.isHoped == false).map(_.average).headOption
      val hopedAve: Option[Double] = rows.filter(_.isHoped == true).map(_.average).headOption
      pw.println("Hoped様式," + Property.initialHopedPrefAvarage + ",に関して、Normal様式の累積採用度数平均：," + normalAve.getOrElse("null") + ",Hoped様式の累積採用度数平均：," + hopedAve.getOrElse("null"))
    }
    pw.println
  }
}