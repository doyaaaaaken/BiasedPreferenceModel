package doyaaaaaken.main.boot

import java.io.PrintWriter
import doyaaaaaken.main.db.DbSession
import doyaaaaaken.model.TraitFreqHistory
import doyaaaaaken.model.TraitFreqHistoryDataRow

/**
 * trait_freq_historyテーブルよりデータ取得し
 * 度数最高値がTopNに達した様式群の推移をCSV出力するオブジェクト
 */
private[boot] object TopNTraitCsvOutputter extends PrintWriterUser {
  override def csvOutput(pw: PrintWriter): Unit = {
    val con = DbSession.getConnection
    val topNTraitsData: Seq[TraitFreqHistoryDataRow] = TraitFreqHistory.selectTopNTraitsData(con)

    //1行目を項目行として作成
    pw.print("timeStep,")
    for (i <- 1 to 16382) pw.print(i + ",")
    pw.println

    //2行目以下にデータを挿入
    topNTraitsData.groupBy(_.timestep).foreach {
      case (timeStep, dataRow) =>
        {
          var tmpTraitKindCounter = 1 //","の表示制御に使うための一時変数
          pw.print(timeStep + ",")
          dataRow.sortBy(_.trait_kind).foreach {
            case row =>
              for (i <- 1 to row.trait_kind - tmpTraitKindCounter) pw.print(",")
              tmpTraitKindCounter = row.trait_kind + 1
              pw.print(row.freq + ",")
          }
        }
        pw.println
    }
  }
}