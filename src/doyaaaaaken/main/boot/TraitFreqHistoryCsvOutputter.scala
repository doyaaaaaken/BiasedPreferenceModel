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

    //使われている様式種類群
    val traitKindsList: Seq[Int] = tfh.map(_.trait_kind).toSet.toList.sorted //データとして存在したtrait_kind群
    val traitKindsPosition: Map[Int, Int] = { for ((x, i) <- traitKindsList.zipWithIndex) yield (x, i + 2) }.toMap //「._2列目がtraitKind ._1番目の列」という情報

    //1行目の出力
    pw.print("timeStep")
    traitKindsList.foreach(traitKind => pw.print("," + traitKind))
    pw.println

    //2行目以下にデータを挿入
    tfh.groupBy(_.timestep).foreach {
      case (timeStep, dataRows) =>
        {
          var tmpTraitKindCounter = 1 //","の表示制御に使うための一時変数
          pw.print(timeStep)
          dataRows.sortBy(_.trait_kind).foreach {
            case row =>
              for (i <- 1 to traitKindsPosition.apply(row.trait_kind) - tmpTraitKindCounter) pw.print(",")
              pw.print(row.freq)
              tmpTraitKindCounter = traitKindsPosition.apply(row.trait_kind)
          }
        }
        pw.println
    }
  }
}