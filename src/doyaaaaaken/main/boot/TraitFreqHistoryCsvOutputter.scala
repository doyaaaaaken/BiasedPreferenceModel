package doyaaaaaken.main.boot

import doyaaaaaken.model.TraitFreqHistory
import java.io.PrintWriter
import doyaaaaaken.main.db.DbSession
import doyaaaaaken.model.TraitFreqHistoryDataRow

/**trait_freq_historyテーブルの値をCSV出力するオブジェクト*/
private[boot] object TraitFreqHistoryCsvOutputter extends PrintWriterUser {
  override def csvOutput(pw: PrintWriter): Unit = {
    val con = DbSession.getConnection
    val tfh: Seq[TraitFreqHistoryDataRow] = TraitFreqHistory.selectAllData(con) //id,timestep,trait_kind,freqの4カラムからなるデータ群を取得

    //1行目を項目行として作成
    pw.print("timeStep,")
    for (i <- 1 to 16382) pw.print(i + ",")
    pw.println

    //2行目以下にデータを挿入
    tfh.groupBy(_.timestep).foreach {
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