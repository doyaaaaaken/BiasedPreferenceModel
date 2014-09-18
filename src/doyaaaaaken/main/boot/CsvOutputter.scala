package doyaaaaaken.main.boot

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.IOException
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import doyaaaaaken.main.db.DbSession
import doyaaaaaken.model.helper.TraitFactory
import doyaaaaaken.model.TraitFreqHistory

object CsvOutputter {
  /**シミュレーション終了後に、DBの値をCSVとしてファイル出力するメソッド*/
  def work(): Unit = {
    println("＊＊＊＊＊＊CSVファイルアウトプット開始＊＊＊＊＊＊")
    val file = new File(Property.csvOutputFileName)
    try {
      file.createNewFile //ファイルが存在しない時はファイルを作成
      val pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "Shift-JIS")))
      outputTraitFreqHistory(pw) //trait_freq_historyテーブルの値をCSV出力
      pw.close
    } catch {
      case e: IOException => println("CSV入出力中に入出力例外が発生しました ： " + e.printStackTrace)
      case e: RuntimeException => println("CSV入出力中に実行時例外が発生しました ： " + e.printStackTrace)
    }
    println("＊＊＊＊＊＊CSVファイルアウトプット終了しました＊＊＊＊＊＊")
  }

  /**trait_freq_historyテーブルの値をCSV出力するメソッド*/
  private[this] def outputTraitFreqHistory(pw: PrintWriter): Unit = {
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