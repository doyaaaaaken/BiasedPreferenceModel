package doyaaaaaken.main.boot.csvoutputter

import doyaaaaaken.model.TraitFreqHistoryDataRow
import doyaaaaaken.model.TraitFreqHistory
import java.io.PrintWriter
import doyaaaaaken.main.db.DbSession
import doyaaaaaken.main.boot.Property

/**様式の寿命の分布をCSV出力するオブジェクト*/
object TraitLifeSpanFreqCsvOutputter extends PrintWriterUser {

  override def csvOutput(pw: PrintWriter): Unit = {
    if (Property.dbSaveInterval == 1) {
      val con = DbSession.getConnection

      val traitLifeSpanMap: Map[Int, Int] = TraitFreqHistory.getTraitLifeSpanFreq(con) //trait_kind,lifeSpanのペアデータ群を取得

      //１行目はタイトル行
      pw.print("trait_kind,life_span")
      pw.println

      //２行目以下にデータを並べていく
      traitLifeSpanMap.foreach {
        case (traitKind, lifeSpan) => {
          pw.print(traitKind + "," + lifeSpan)
          pw.println
        }
      }
    } else {
      println
      println("様式の寿命分布のアウトプット失敗！！！")
      println("注：DBへの保存間隔が1タイムステップごとではないため、様式の寿命の分布のアウトプットはできません！！")
      println
    }
  }
}