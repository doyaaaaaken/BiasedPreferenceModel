//package doyaaaaaken.main.boot.csvoutputter
//
//import doyaaaaaken.model.TraitFreqHistoryDataRow
//import doyaaaaaken.model.TraitFreqHistory
//import java.io.PrintWriter
//import doyaaaaaken.main.db.DbSession
//import doyaaaaaken.main.boot.Property
//import doyaaaaaken.model.TraitLifeSpanDataRow
//import doyaaaaaken.model.TraitLifeSpanDataRow
//
///**様式の寿命の分布をCSV出力するオブジェクト*/
//object TraitLifeSpanFreqCsvOutputter extends PrintWriterUser {
//
//  override def csvOutput(pw: PrintWriter): Unit = {
//    if (Property.dbSaveInterval == 1) {
//      val con = DbSession.getConnection
//      val traitLifeSpanDatas: Seq[TraitLifeSpanDataRow] = TraitFreqHistory.getTraitLifeSpanFreq(con) //trait_kind,lifeSpanのペアデータ群を取得
//
//      //sim_num（何回目のシミュレーションか）ご とにデータを分ける
//      val traitLifeSpanDatasMap: Map[Int, Seq[TraitLifeSpanDataRow]] = traitLifeSpanDatas.groupBy(_.simNum)
//
//      //sim_numごとに出力する
//      traitLifeSpanDatasMap.foreach {
//        case (simNum, traitLifeSpanData) =>
//
//          //タイトル行の出力
//          pw.println(simNum + "回目のシミュレーション結果")
//
//          //2行目はタイトル行
//          pw.print("trait_kind,life_span")
//          pw.println
//
//          //3行目以下にデータを並べていく
//          traitLifeSpanData.foreach { data =>
//            {
//              pw.print(data.traitKind + "," + data.lifeSpan)
//              pw.println
//            }
//          }
//          pw.println
//      }
//    } else {
//      println
//      println("様式の寿命分布のアウトプット失敗！！！")
//      println("注：DBへの保存間隔が1タイムステップごとではないため、様式の寿命の分布のアウトプットはできません！！")
//      println
//    }
//  }
//}