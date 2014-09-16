package doyaaaaaken.main.boot

/**
 * シミュレーションの節目で必要な処理
 */
object Boot {

  def start(): Unit = {
    println("＊＊＊＊＊＊シミュレーション開始＊＊＊＊＊＊")
    PropertyReader.read
  }

  def finish(): Unit = {
    //TODO DBの接続閉じるとか
    println("＊＊＊＊＊＊シミュレーション終了＊＊＊＊＊＊")
  }
}