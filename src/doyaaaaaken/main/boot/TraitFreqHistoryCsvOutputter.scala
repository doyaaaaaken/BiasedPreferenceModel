package doyaaaaaken.main.boot

import doyaaaaaken.model.TraitFreqHistory
import java.io.PrintWriter
import doyaaaaaken.main.db.DbSession

/**trait_freq_historyテーブルの値をCSV出力するオブジェクト*/
private[boot] object TraitFreqHistoryCsvOutputter extends PrintWriterUser {
  override def csvOutput(pw: PrintWriter): Unit = {
    val con = DbSession.getConnection
    val tfh: Seq[(Int, Int, Int, Int)] = TraitFreqHistory.selectAllData(con) //id,timestep,trait_kind,freqの4カラムからなるデータ群を取得

    //1行目を項目行として作成
    pw.print("timeStep,")
    for (i <- 1 to 16382) pw.print(i + ",")
    pw.println

    //2行目以下にデータを挿入
    tfh.groupBy(_._2).foreach { //timeStepの値でグループ化
      case (timeStep, dataRow) =>
        {
          var tmpTraitKindCounter = 1 //","の表示制御に使うための一時変数
          pw.print(timeStep + ",")
          dataRow.sortBy(_._3).foreach { //traitKindでソートする
            case (id, timestep, traitKind, freq) =>
              for (i <- 1 to traitKind - tmpTraitKindCounter) pw.print(",")
              tmpTraitKindCounter = traitKind + 1
              pw.print(freq + ",")
          }
        }
        pw.println
    }
  }
}