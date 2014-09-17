package doyaaaaaken.main.boot

import scala.io.Source
import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import java.util.Collections.EmptyMap

/**
 * どのクラスからでも参照できる設定ファイル情報オブジェクト
 */
object Property {

  private[this] var map: Map[String, String] = null

  /**
   * 設定ファイルから読み込んだ情報の格納
   */
  def init(keyValueMap: Map[String, String]) = {
    map = keyValueMap
  }

  //アクセス時のキー名一覧
  lazy val simNum: Int = map.apply("sim.num").toInt
  lazy val agentNum: Int = map.apply("agent.num").toInt
  lazy val agentPossessTraitCapacity: Int = map.apply("agent.trait.possessCapacity").toInt
  lazy val initialTraitKind: Int = map.apply("trait.initialKindNum").toInt
  lazy val initialHavingTraitProp: Double = map.apply("trait.initialHavingKindProportion").toDouble
  lazy val debug: Boolean = map.apply("sim.debug").toBoolean
}

/**
 * 設定ファイルの読み込み&Propetyオブジェクトの初期化
 */
object PropertyReader {

  def read(): Unit = {
    val source = Source.fromFile("./resource/prop.txt")
    var sourceStr: String = ""
    try {
      sourceStr = source.getLines.toList.mkString("")
    } finally {
      source.close()
    }

    val pattern: Regex = new Regex(""" *([.A-Za-z0-9]*) *= *([^;]+); *""", "key", "value")
    val keyValueMap = pattern.findAllIn(sourceStr).matchData.map { m: Match =>
      (m.group("key"), m.group("value"))
    }.toMap

    Property.init(keyValueMap)
  }
}
