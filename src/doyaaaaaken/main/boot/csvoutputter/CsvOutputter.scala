package doyaaaaaken.main.boot.csvoutputter

import java.io.BufferedWriter
import java.io.File
import java.io.PrintWriter
import java.io.IOException
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import doyaaaaaken.main.boot.Property
import java.util.Date
import doyaaaaaken.main.boot.csvoutputter.feature_value.LifeSpanAveCsvOutputter
import doyaaaaaken.main.boot.csvoutputter.feature_value.CumulativeFreqAveCsvOutputter
import doyaaaaaken.main.boot.csvoutputter.feature_value.MaxFreqAveCsvOutputter

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

  /**タイムステップごとの度数分布：TraitFreqHistoryの出力メソッド*/
  def outputTraitFreqHistory(): Unit = {
    val outputter: PrintWriterUser = TraitFreqHistoryCsvOutputter
    outputCsvFile(Property.csvOutputFileNameForTraitFreqHistory, outputter, -1)
  }

  /**特徴量についてTopNの様式群のうち、Hopedな様式の数を出力するメソッド*/
  def outputTopNHopedTraitNum(): Unit = {
    //    outputMaxFreqTopNTrait()
    outputCumulativeFreqTopNHopedTraitNum()
    outputLifeSpanTopNHopedTraitNum()
  }

  /**度数最高値がTop20, 40, 100のものの度数推移出力メソッド*/
  private[this] def outputMaxFreqTopNTrait(): Unit = {
    //既に検討したので、今は書かないでおく
  }

  /**累積採用度数がTop20, 40, 100の様式群の度数推移出力メソッド*/
  private[this] def outputCumulativeFreqTopNHopedTraitNum(): Unit = {
    println("***********累積採用度数TOPNの様式群の情報出力************")
    val outputter: PrintWriterUser = CumulativeFreqTopNTraitCsvOutputter
    outputCsvFile("cumulativeFreqTop20Trait-hope" + Property.initialHopedPrefAvarage + ".csv", outputter, 20)
    outputCsvFile("cumulativeFreqTop40Trait-hope" + Property.initialHopedPrefAvarage + ".csv", outputter, 40)
    outputCsvFile("cumulativeFreqTop100Trait-hope" + Property.initialHopedPrefAvarage + ".csv", outputter, 100)
    println("***********出力完了***************" + new Date().toString)
  }

  /**様式寿命最長値がTop20, 40, 100の様式群の度数推移出力メソッド*/
  private[this] def outputLifeSpanTopNHopedTraitNum(): Unit = {
    println("***********様式寿命最長値TOPNの様式群の情報出力************")
    val outputter: PrintWriterUser = LifeSpanTopNTraitCsvOutputter
    outputCsvFile("lifeSpanTop20Trait-hope" + Property.initialHopedPrefAvarage + ".csv", outputter, 20)
    outputCsvFile("lifeSpanTop40Trait-hope" + Property.initialHopedPrefAvarage + ".csv", outputter, 40)
    outputCsvFile("lifeSpanTop100Trait-hope" + Property.initialHopedPrefAvarage + ".csv", outputter, 100)
    println("***********出力完了***************" + new Date().toString)
  }

  /**Normal様式群とHoped様式群の特徴量を、様式全体・特徴量に関してTop100,40,20の様式群 から抽出して出力する*/
  def outputNormalHopedFeatureValue(): Unit = {
    outputMaxFreqAve() //様式の最高到達度数の平均値
    outputCumulativeFreqAve() //様式の累積採用度数の平均値
    outputLifeSpanAve() //様式の寿命平均値
  }

  private[this] def outputMaxFreqAve(): Unit = {
    println("***********Normal度数とHoped度数の、最高到達度数の平均値情報出力************")
    val outputter: PrintWriterUser = MaxFreqAveCsvOutputter
    outputCsvFile("maxFreqAve_hope" + Property.initialHopedPrefAvarage + ".csv", outputter, -1)
    println("***********出力完了***************" + new Date().toString)
  }

  private[this] def outputCumulativeFreqAve(): Unit = {
    println("***********Normal度数とHoped度数の、累積採用度数の平均値情報出力************")
    val outputter: PrintWriterUser = CumulativeFreqAveCsvOutputter
    outputCsvFile("cumulativeFreqAve_hope" + Property.initialHopedPrefAvarage + ".csv", outputter, -1)
    println("***********出力完了***************" + new Date().toString)
  }

  private[this] def outputLifeSpanAve(): Unit = {
    println("***********Normal度数とHoped度数の、様式寿命の平均値情報出力************")
    val outputter: PrintWriterUser = LifeSpanAveCsvOutputter
    outputCsvFile("lifeSpanAve_hope" + Property.initialHopedPrefAvarage + ".csv", outputter, -1)
    println("***********出力完了***************" + new Date().toString)
  }

  //  /**様式の寿命の分布出力メソッド*/
  //  def outputTraitLifeSpanFreq(): Unit = {
  //    val outputter: PrintWriterUser = TraitLifeSpanFreqCsvOutputter
  //    outputCsvFile(Property.csvOutputFileNameForTraitLifeSpanFreq, outputter)
  //  }
  //
  def outputPreferenceHistoryForOneTrait(): Unit = {
    val outputter: PrintWriterUser = PreferenceHistoryForOneTraitCsvOutputter
    outputCsvFile(Property.csvOutputFileNameForPreferenceHistoryForOneTrait, outputter, rank = -1)
  }
}

/**PrintWriterを渡したら、それを使ってデータ出力をするTrait*/
private[csvoutputter] trait PrintWriterUser {
  def csvOutput(pw: PrintWriter, rank: Int): Unit
}