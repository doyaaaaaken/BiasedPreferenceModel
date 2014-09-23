package doyaaaaaken.main.boot

import java.io.PrintWriter

/**
 * trait_freq_historyテーブルよりデータ取得し
 * 度数最高値がTopNに達した様式群の推移をCSV出力するオブジェクト
 */
private[boot] object TopNTraitCsvOutputter extends PrintWriterUser {
  override def csvOutput(pw: PrintWriter): Unit = {
    //TODO 未実装
  }
}