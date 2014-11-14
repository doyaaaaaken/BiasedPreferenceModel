package doyaaaaaken.main.boot.csvoutputter

import java.io.BufferedWriter
import java.io.File
import java.io.PrintWriter
import java.io.IOException
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import doyaaaaaken.main.boot.Property
import java.util.Date

/**シミュレーション終了後に、DBの値をCSVとしてファイル出力するオブジェクト*/
private[boot] object CsvOutputter {

  /**引数に受け取ったファイル名と関数を用いてCSVファイル出力を行う共通メソッド*/
  private[this] def outputCsvFile(fileName: String, pwUser: PrintWriterUser, rank: Int): Unit = {
    println("＊＊＊＊＊＊CSVファイルアウトプット開始＊＊＊＊＊＊")
    val file = new File(fileName)
    try {
      file.createNewFile //ファイルが存在しない時はファイルを作成
      val pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "Shift-JIS")))
      pwUser.csvOutput(pw, rank)
      pw.close
    } catch {
      case e: IOException => println("CSV入出力中に入出力例外が発生しました ： " + e.printStackTrace)
      case e: RuntimeException => println("CSV入出力中に実行時例外が発生しました ： " + e.printStackTrace)
    }
    println("＊＊＊＊＊＊CSVファイルアウトプット終了しました＊＊＊＊＊＊")
  }

  //  /**タイムステップごとの度数分布：TraitFreqHistoryの出力メソッド*/
  //  def outputTraitFreqHistory(): Unit = {
  //    val outputter: PrintWriterUser = TraitFreqHistoryCsvOutputter
  //    outputCsvFile(Property.csvOutputFileNameForTraitFreqHistory, outputter)
  //  }

  /**度数最高値がTopNのものの度数推移出力メソッド*/
  def outputTopNTrait(): Unit = {
    //    outputMaxFreqTopNTrait()
    outputCumulativeFreqTopNTrait()
    outputLifeSpanTopNTrait()
  }

  /**度数最高値がTop20, 40, 100のものの度数推移出力メソッド*/
  private[this] def outputMaxFreqTopNTrait(): Unit = {
    //既に検討したので、今は書かないでおく
  }

  /**累積採用度数がTop20, 40, 100の様式群の度数推移出力メソッド*/
  private[this] def outputCumulativeFreqTopNTrait(): Unit = {
    println("***********累積採用度数TOPNの様式群の情報出力************")
    val outputter: PrintWriterUser = CumulativeFreqTopNTraitCsvOutputter
    outputCsvFile("cumulativeFreqTop20Trait.csv", outputter, 20)
    outputCsvFile("cumulativeFreqTop40Trait.csv", outputter, 40)
    outputCsvFile("cumulativeFreqTop100Trait.csv", outputter, 100)
    println("***********出力完了***************" + new Date().toString)
  }

  /**様式寿命最長値がTop20, 40, 100の様式群の度数推移出力メソッド*/
  private[this] def outputLifeSpanTopNTrait(): Unit = {
    println("***********様式寿命最長値TOPNの様式群の情報出力************")
    val outputter: PrintWriterUser = LifeSpanTopNTraitCsvOutputter
    outputCsvFile("lifeSpanTop20Trait.csv", outputter, 20)
    outputCsvFile("lifeSpanTop40Trait.csv", outputter, 40)
    outputCsvFile("lifeSpanTop100Trait.csv", outputter, 100)
    println("***********出力完了***************" + new Date().toString)
  }

  //  /**様式の寿命の分布出力メソッド*/
  //  def outputTraitLifeSpanFreq(): Unit = {
  //    val outputter: PrintWriterUser = TraitLifeSpanFreqCsvOutputter
  //    outputCsvFile(Property.csvOutputFileNameForTraitLifeSpanFreq, outputter)
  //  }
  //
  //  def outputPreferenceHistoryForOneTrait(): Unit = {
  //    val outputter: PrintWriterUser = PreferenceHistoryForOneTraitCsvOutputter
  //    outputCsvFile(Property.csvOutputFileNameForPreferenceHistoryForOneTrait, outputter)
  //  }
}

/**PrintWriterを渡したら、それを使ってデータ出力をするTrait*/
private[csvoutputter] trait PrintWriterUser {
  def csvOutput(pw: PrintWriter): Unit
  def csvOutput(pw: PrintWriter, rank: Int): Unit
}