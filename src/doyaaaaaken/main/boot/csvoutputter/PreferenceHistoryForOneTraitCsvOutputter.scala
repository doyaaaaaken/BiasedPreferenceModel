package doyaaaaaken.main.boot.csvoutputter

import java.io.PrintWriter
import doyaaaaaken.main.db.DbSession
import doyaaaaaken.model.PreferenceHistoryForOneTraitDataRow
import doyaaaaaken.model.PreferenceHistoryForOneTrait
import doyaaaaaken.main.boot.Property

/**
 * preference_history_for_one_traitテーブルよりデータ取得し
 * 監視対象の様式に対する全エージェントのPreferenceの値推移をCSV出力するオブジェクト
 *
 * （行：タイムステップ 、行：エージェント番号
 */
private[boot] object PreferenceHistoryForOneTraitCsvOutputter extends PrintWriterUser {

  override def csvOutput(pw: PrintWriter, rank: Int): Unit = {
    val con = DbSession.getConnection
    val prefHistoryDataRows: Seq[PreferenceHistoryForOneTraitDataRow] = PreferenceHistoryForOneTrait.selectAllData(con)

    //sim_num（何回目のシミュレーションか）ごとにデータを分ける
    val prefHistoryDataRowsMap: Map[Int, Seq[PreferenceHistoryForOneTraitDataRow]] = prefHistoryDataRows.groupBy(_.simNum)

    //sim_numごとに出力する
    prefHistoryDataRowsMap.foreach {
      case (simNum, prefHistorys) =>

        //trait_kindごとに出力する
        prefHistorys.groupBy(_.traitKind).foreach {
          case (traitKind, prefHisoryForOneTrait) =>

            val timeStepRowDatas = prefHisoryForOneTrait.groupBy(_.timeStep).map { //(timeStep, Map(agentId, preference))の形式に変換
              case (timeStep, phSeq) =>
                (timeStep, phSeq.map(ph => (ph.agentId, ph.preference)).toMap)
            }

            //タイトル行の出力
            pw.println(simNum + "回目のシミュレーション結果")

            //2行目にはタイトルとAgent番号を出力する
            pw.print("様式番号：" + traitKind + "に対するPreferenceの値の推移")
            for (i <- 0 to Property.agentNum - 1) { pw.print("," + i) }
            pw.println

            //3行目以下にデータを挿入
            timeStepRowDatas.foreach {
              case (timeStep, preferenceMap) => {
                pw.print(timeStep + ",")
                for (i <- 0 to Property.agentNum - 1) {
                  pw.print(preferenceMap(i) + ",")
                }
                pw.println
              }
            }
            pw.println
        }
    }
  }
}