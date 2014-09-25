package doyaaaaaken.main.boot.csvoutputter

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

    //使われている様式種類群
    val traitKindsList: Seq[Int] = topNTraitsData.map(_.trait_kind).toSet.toList.sorted //データとして存在したtrait_kind群
    val traitKindsPosition: Map[Int, Int] = { for ((x, i) <- traitKindsList.zipWithIndex) yield (x, i + 2) }.toMap //「._2列目がtraitKind ._1番目の列」という情報

    //1行目の出力
    pw.print("timeStep")
    traitKindsList.foreach(traitKind => pw.print("," + traitKind))
    pw.println

    //2行目以下にデータを挿入
    topNTraitsData.groupBy(_.timestep).foreach {
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

  /**
   * 注：以前使っていたアルゴリズムだが、現在のほうが出力形式が見やすいためもう使わない
   * しかし表示が正確になされるという保証があるので一応残しておく
   */
  @deprecated
  private[this] def outputUnusedTraitsInclude(pw: PrintWriter, topNTraitsData: Seq[TraitFreqHistoryDataRow]): Unit = {
    /*使われていない様式も含めて出力するメソッド*/

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