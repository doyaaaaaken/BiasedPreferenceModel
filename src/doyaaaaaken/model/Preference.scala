package doyaaaaaken.model

import doyaaaaaken.main.boot.Property

private[model] class Preference(pref: Map[Int, Double]) {

  /**指定の様式種類番号に対する好みの値を返す*/
  def getPreferenceValue(traitKind: Int): Double = {
    pref.apply(traitKind)
  }

  /**与えられた様式種類群に対する好みの総和を返す*/
  def calcPrefSum(traitNums: Seq[Int]): Double = {
    traitNums.map(pref.apply(_)).sum
  }
}

object Preference {
  /**
   * エージェントと結びついているPreferenceの初期値として
   * 初期状態にて存在する各様式に対する好みを
   * -1.0～1.0までのランダムな値としてもつようにする
   */
  def apply: Preference = {
    val tmp = for (i <- (0 to Property.initialTraitKind - 1)) yield { (i, Math.random * 2 - 1.0) }
    val pref: Map[Int, Double] = tmp.toMap
    new Preference(pref)
  }
}