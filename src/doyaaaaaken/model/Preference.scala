package doyaaaaaken.model

import doyaaaaaken.main.boot.Property

private[model] class Preference(preference: Map[Int, Double]) {

  private var pref = preference

  /**指定の様式種類番号に対する好みの値を返す*/
  def getPreferenceValue(traitKind: Int): Double = {
    pref.apply(traitKind)
  }

  /**ゲッター：Preferenceの値を格納したMapをコピーして返す*/
  def getPreference: Map[Int, Double] = pref.toMap

  /**与えられた様式種類群に対する好みの総和を返す*/
  def calcPrefSum(traitNums: Seq[Int]): Double = {
    traitNums.map(pref.apply(_)).sum
  }

  /**指定の様式番号の様式に対する好みを変更する*/
  def changePrefValue(traitNum: Int, prefValue: Double): Unit = {
    pref = if (pref.contains(traitNum)) {
      pref.map {
        case (tKind, preValue) => if (tKind != traitNum) (tKind, preValue) else (tKind, prefValue)
      }
    } else {
      pref ++ Map(traitNum -> prefValue)
    }
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