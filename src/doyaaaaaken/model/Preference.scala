package doyaaaaaken.model

import doyaaaaaken.main.properties.Property

private[model] class Preference(pref: Map[Int, Double]) {

  def getPreferenceValue(traitKind: Int): Option[Double] = {
    pref.get(traitKind)
  }

  //TODO Preferenceクラスの実装

}

object Preference {
  /**
   * エージェントと結びついているPreferenceの初期値として
   * 初期状態にて存在する各様式に対する好みを
   * -1.0～1.0までのランダムな値としてもつようにする
   */
  def apply: Preference = {
    val prefIndexedSeq = for (i <- (0 to Property.initialTraitKind - 1)) yield { (i, Math.random * 2 - 1.0) }
    val pref: Map[Int, Double] = prefIndexedSeq.toMap
    new Preference(pref)
  }
}