package doyaaaaaken.main.properties

import scala.io.Source
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

/**
 * Property情報にアクセスしたい時は、objectの中のキー名一覧から、props.get("キー名")でアクセスできる
 */
class Property(keyValueStore: Map[String, String]) {
  //アクセス時のキー名一覧
  val simNum: Int = keyValueStore.apply("sim.num").toInt
  val agentNum: Int = keyValueStore.apply("agent.num").toInt
  val agentPossessTraitCapacity: Int = keyValueStore.apply("agent.trait.possessCapacity").toInt
}

object Property {
  def apply(keyValueMap: Map[String, String]) = {
    new Property(keyValueMap)
  }
}

/**
 * 設定ファイルの読み込み＆値をマップへ格納
 */
object PropertyReader {

  def read(): Property = {
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

    Property(keyValueMap)
  }
}