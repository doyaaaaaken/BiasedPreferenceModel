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

/**シミュレーション終了後に、DBの値をCSVとしてファイル出力するオブジェクト*/
private[boot] object CsvOutputter {

  /**引数に受け取ったファイル名と関数を用いてCSVファイル出力を行う共通メソッド*/
  private[this] def outputCsvFile(fileName: String, pwUser: PrintWriterUser): Unit = {
    println("＊＊＊＊＊＊CSVファイルアウトプット開始＊＊＊＊＊＊")
    val file = new File(fileName)
    try {
      file.createNewFile //ファイルが存在しない時はファイルを作成
      val pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "Shift-JIS")))
      pwUser.csvOutput(pw)
      pw.close
    } catch {
      case e: IOException => println("CSV入出力中に入出力例外が発生しました ： " + e.printStackTrace)
      case e: RuntimeException => println("CSV入出力中に実行時例外が発生しました ： " + e.printStackTrace)
    }
    println("＊＊＊＊＊＊CSVファイルアウトプット終了しました＊＊＊＊＊＊")
  }

  /**タイムステップごとの度数分布：TraitFreqHistoryの出力メソッド*/
  def outputTraitFreqHistory(): Unit = {
    val outputter: PrintWriterUser = TraitFreqHistoryCsvOutputter
    outputCsvFile(Property.csvOutputFileNameForTraitFreqHistory, outputter)
  }

  /**度数最高値がTOP40のものの度数推移出力メソッド*/
  def outputTop40Trait(): Unit = {
    val outputter: PrintWriterUser = Top40TraitCsvOutputter
    outputCsvFile(Property.csvOutputFileNameForTop40Trait, outputter)
  }
}

/**PrintWriterを渡したら、それを使ってデータ出力をするTrait*/
private[boot] trait PrintWriterUser {
  def csvOutput(pw: PrintWriter): Unit
}