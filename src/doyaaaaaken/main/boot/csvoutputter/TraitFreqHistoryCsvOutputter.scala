package doyaaaaaken.main.boot.csvoutputter

import doyaaaaaken.model.TraitFreqHistory
import java.io.PrintWriter
import doyaaaaaken.main.db.DbSession
import doyaaaaaken.model.TraitFreqHistoryDataRow
import doyaaaaaken.main.boot.Property

/**trait_freq_historyテーブルの値をCSV出力するオブジェクト*/
private[boot] object TraitFreqHistoryCsvOutputter extends PrintWriterUser {

  override def csvOutput(pw: PrintWriter, rank: Int): Unit = {
    val con = DbSession.getConnection
    val tfhDataRows: Seq[TraitFreqHistoryDataRow] = TraitFreqHistory.selectAllData(con) //id,sim_num,timestep,trait_kind,freqの4カラムからなるデータ群を取得

    //sim_num（何回目のシミュレーションか）ごとにデータを分ける
    val tfhDataRowsMap: Map[Int, Seq[TraitFreqHistoryDataRow]] = tfhDataRows.groupBy(_.simNum)

    //sim_numごとに出力する
    tfhDataRowsMap.foreach {
      case (simNum, tfh) =>

        //使われている様式種類群
        val traitKindsList: Seq[Int] = tfh.map(_.trait_kind).toSet.toList.sorted //データとして存在したtrait_kind群
        //        val traitKindsPosition: Map[Int, Int] = { for ((x, i) <- traitKindsList.zipWithIndex) yield (x, i + 2) }.toMap //「._2列目がtraitKind ._1番目の列」という情報

        //タイトル行の出力
        //        pw.println(simNum + "回目のシミュレーション結果")

        //2行目の出力
        //        pw.print("timeStep")
        //        traitKindsList.foreach(traitKind => pw.print("," + traitKind))
        //        pw.println

        //3行目以下にデータを挿入
        //        tfh.groupBy(_.timestep).foreach {
        //          case (timeStep, dataRows) =>
        //            {
        //              var tmpTraitKindCounter = 1 //","の表示制御に使うための一時変数
        //              pw.print(timeStep)
        //              dataRows.sortBy(_.trait_kind).foreach {
        //                case row =>
        //                  for (i <- 1 to traitKindsPosition.apply(row.trait_kind) - tmpTraitKindCounter) pw.print(",")
        //                  pw.print(row.freq)
        //                  tmpTraitKindCounter = traitKindsPosition.apply(row.trait_kind)
        //              }
        //            }
        //            pw.println
        //        }

        pw.print(tfh.filter(_.timestep == Property.simTimeNum).map(_.trait_kind).toSet.size) //最後に残った様式の種類数を出力する
        pw.println
    }
  }
}