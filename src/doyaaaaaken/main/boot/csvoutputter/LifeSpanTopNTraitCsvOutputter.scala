package doyaaaaaken.main.boot.csvoutputter

import doyaaaaaken.model.TraitFreqHistory
import java.io.PrintWriter
import doyaaaaaken.main.db.DbSession
import doyaaaaaken.main.boot.Property

private[boot] object LifeSpanTopNTraitCsvOutputter extends PrintWriterUser {

  override def csvOutput(pw: PrintWriter, rankLimit: Int): Unit = {
    val con = DbSession.getConnection

    //sim_num（何回目のシミュレーションか）ごとに出力する
    for (simNum <- 1 to Property.simNum) {
      var topNTraitsDatas = TraitFreqHistory.selectLifeSpanTopNTraitsData(con, simNum, rankLimit)

      //使われている様式種類群
      val traitKindsList: Seq[Int] = topNTraitsDatas.map(_.trait_kind).toSet.toList.sorted //データとして存在したtrait_kind群
      //      val traitKindsPosition: Map[Int, Int] = { for ((x, i) <- traitKindsList.zipWithIndex) yield (x, i + 2) }.toMap //「._2列目がtraitKind ._1番目の列」という情報
      //
      //      //タイトル行の出力
      //      pw.println(simNum + "回目のシミュレーション結果")
      //
      //      //2行目の出力
      //      pw.print("timeStep")
      //      traitKindsList.foreach(traitKind => pw.print("," + traitKind))
      //      pw.println
      //
      //      //3行目以下にデータを挿入
      //      topNTraitsDatas.groupBy(_.timestep).foreach {
      //        case (timeStep, dataRows) =>
      //          {
      //            var tmpTraitKindCounter = 1 //","の表示制御に使うための一時変数
      //            pw.print(timeStep)
      //            dataRows.sortBy(_.trait_kind).foreach {
      //              case row =>
      //                for (i <- 1 to traitKindsPosition.apply(row.trait_kind) - tmpTraitKindCounter) pw.print(",")
      //                pw.print(row.freq)
      //                tmpTraitKindCounter = traitKindsPosition.apply(row.trait_kind)
      //            }
      //          }
      //          pw.println
      //      }
      //      pw.println

      //全様式数のうちのhopedな様式の数を末尾に出力する
      val sb: StringBuffer = new StringBuffer()
      sb.append("全様式数  ,")
      sb.append(traitKindsList.size)
      sb.append(",個のうち、hoped(平均値  ,")
      sb.append(Property.initialHopedPrefAvarage)
      sb.append(",)な様式の数は  ,")
      sb.append(traitKindsList.filter(_ % Property.hopedTraitGenerateInterval == 0).size)
      pw.println(sb.toString())
      //      pw.println

      if (simNum % 20 == 0) println(simNum + "タイムステップ数出力")
    }
  }
}