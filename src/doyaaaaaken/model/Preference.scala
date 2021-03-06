package doyaaaaaken.model

import doyaaaaaken.main.boot.Property
import scala.util.Random

private[model] class Preference(preference: Map[Int, Double]) {

  var pref: Map[Int, Double] = preference

  /**指定の様式種類番号に対する好みの値を返す*/
  def getPreferenceValue(traitKind: Int): Double = {
    getOrCreatePrefValue(traitKind)
  }

  /**ゲッター：Preferenceの値を格納したMapをコピーして返す*/
  def getPreference: Map[Int, Double] = pref.toMap

  /**与えられた様式種類群に対する好みの総和を返す*/
  def calcPrefSum(traitNums: Seq[Int]): Double = {
    traitNums.map(getOrCreatePrefValue(_)).sum
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

  /**エージェントの転生に伴い、好みの値を現存する様式群に対して全て1.0に変更する*/
  def rebornAgent(currentTraitList: Seq[Int]): Unit = {
    pref = currentTraitList.map((_ -> 1.0)).toMap
  }

  /**好みの値をランダムに振り直す*/
  def randomizePrefValue: Unit = {
    pref = pref.map(prefMap => (prefMap._1, Math.random() * 2 - 1.0))
  }

  /**引数に指定された様式群以外に対する好みを消す*/
  def eraseExceptNecessaryPreference(currentTraitList: Seq[Int]): Unit = {
    pref = pref.filterKeys(traitNum => currentTraitList.contains(traitNum))
  }

  /**指定の様式番号に対する好みの値を得る。まだなかった場合は適宜その場で作る*/
  private def getOrCreatePrefValue(traitKind: Int): Double = {
    if (pref.contains(traitKind)) {
      pref.apply(traitKind)
    } else {
      val newPrefValue = Preference.calcInitialPrefByType(traitKind)
      pref = pref + (traitKind -> newPrefValue)
      newPrefValue
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
    val tmp = for (i <- (0 to Property.initialTraitKind - 1)) yield {
      //      print(HopedPrefDistribution.getPref + ",") //pref分布出力用
      (i, calcInitialPrefByType(i))
    }
    val pref: Map[Int, Double] = tmp.toMap
    new Preference(pref)
  }

  /**
   * 様式番号から様式のタイプがNormalかHopedか判断してPreferenceの初期値を作り分ける
   */
  def calcInitialPrefByType(traitNum: Int): Double = {
    if (traitNum % Property.hopedTraitGenerateInterval == 0) HopedPrefDistribution.getPref else NormalPrefDistribution.getPref
  }
}

sealed abstract class PrefDistributionType { //好みの分布を表す抽象クラス
  val rand = new Random()
  def getPref: Double
  def validateAndRevise(p: Double): Double = if (p > 1.0 || p < -1.0) getPref else p
}

object NormalPrefDistribution extends PrefDistributionType { //普通の様式に対する分布
  def getPref: Double = validateAndRevise( //平均値と、何σが右端（1.0）に当たるのかを指定して正規分布を作成する
    rand.nextGaussian() * (1.0 - Property.initialNormalPrefAvarage) / Property.initialNormalPrefSigmaPerRange + Property.initialNormalPrefAvarage)
}

object HopedPrefDistribution extends PrefDistributionType { //好まれている様式に対する分布
  def getPref: Double = validateAndRevise( //平均値と、何σが右端（1.0）に当たるのかを指定して正規分布を作成する
    rand.nextGaussian() * (1.0 - Property.initialHopedPrefAvarage) / Property.initialHopedPrefSigmaPerRange + Property.initialHopedPrefAvarage)
}