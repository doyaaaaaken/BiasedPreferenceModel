package doyaaaaaken.main.boot.csvoutputter

import java.io.BufferedWriter
import java.io.File
import java.io.PrintWriter
import java.io.IOException
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import doyaaaaaken.main.boot.Property

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
    outputCsvFile("act" + Property.antiConformThreshold + Property.csvOutputFileNameForTraitFreqHistory, outputter)
  }

  /**度数最高値がTopNのものの度数推移出力メソッド*/
  def outputTopNTrait(): Unit = {
    val outputter: PrintWriterUser = TopNTraitCsvOutputter
    outputCsvFile("act" + Property.antiConformThreshold + Property.csvOutputFileNameForTopNTrait, outputter)
  }

  /**様式の寿命の分布出力メソッド*/
  def outputTraitLifeSpanFreq(): Unit = {
    val outputter: PrintWriterUser = TraitLifeSpanFreqCsvOutputter
    outputCsvFile("act" + Property.antiConformThreshold + Property.csvOutputFileNameForTraitLifeSpanFreq, outputter)
  }

  def outputPreferenceHistoryForOneTrait(): Unit = {
    val outputter: PrintWriterUser = PreferenceHistoryForOneTraitCsvOutputter
    outputCsvFile("act" + Property.antiConformThreshold + Property.csvOutputFileNameForPreferenceHistoryForOneTrait, outputter)
  }
}

/**PrintWriterを渡したら、それを使ってデータ出力をするTrait*/
private[csvoutputter] trait PrintWriterUser {
  def csvOutput(pw: PrintWriter): Unit
}