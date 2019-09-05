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
  private[this] var secureMap: Map[String, String] = null

  /**
   * 設定ファイルから読み込んだ情報の格納
   */
  def init(keyValueMap: Map[String, String], secureKeyValueMap: Map[String, String]) = {
    map = keyValueMap
    secureMap = secureKeyValueMap
  }

  //設定ファイルアクセス時のキー名一覧
  lazy val simNum: Int = map.apply("sim.num").toInt
  lazy val simTimeNum: Int = map.apply("sim.timestep.num").toInt
  lazy val debug: Boolean = map.apply("sim.debug").toBoolean

  lazy val dbClearBeforeSim: Boolean = map.apply("sim.db.clear").toBoolean
  lazy val dbSaveInterval: Int = map.apply("sim.db.saveInterval").toInt
  lazy val dbSaveStartTime: Int = map.apply("sim.db.traitSave.saveStartTime").toInt
  lazy val prefDbSaveTraitKind: Int = map.apply("sim.db.prefSave.observedTraitKind").toInt

  lazy val csvOutputTraitFreqHisory: Boolean = map.apply("sim.csv.output.traitFreqHistory").toBoolean
  lazy val csvOutputFileNameForTraitFreqHistory: String = map.apply("sim.csv.fileName.traitFreqHistory").toString
  lazy val csvOutputTopNTrait: Boolean = map.apply("sim.csv.output.topNtrait").toBoolean
  lazy val csvOutputTopNNum: String = map.apply("sim.csv.topNnum").toString
  lazy val csvOutputFileNameForTopNTrait: String = map.apply("sim.csv.fileName.topNtrait").toString
  lazy val csvOutputTraitLifeSpanFreq: Boolean = map.apply("sim.csv.output.traitLifeSpanFreq").toBoolean
  lazy val csvOutputFileNameForTraitLifeSpanFreq: String = map.apply("sim.csv.fileName.traitLifeSpanFreq").toString
  lazy val csvOutputPreferenceHistoryForOneTrait: Boolean = map.apply("sim.csv.output.preferenceHistoryForOneTrait").toBoolean
  lazy val csvOutputFileNameForPreferenceHistoryForOneTrait: String = map.apply("sim.csv.fileName.preferenceHistoryForOneTrait").toString

  lazy val agentNum: Int = map.apply("agent.num").toInt
  lazy val agentPossessTraitCapacity: Int = map.apply("agent.trait.possessCapacity").toInt
  lazy val memoryTimespanSize: Int = map.apply("agent.memory.size").toInt
  var antiConformThreshold: Double = _
  lazy val antiConformThresholdList: Seq[Double] = map.apply("agent.antiConformThreshold.list").toString.split(",").map(_.toDouble)

  lazy val initialTraitKind: Int = map.apply("trait.initialKindNum").toInt
  lazy val initialHavingTraitProp: Double = map.apply("trait.initialHavingKindProportion").toDouble
  lazy val hopedTraitGenerateInterval: Int = map.apply("trait.generateInterval.hoped").toInt

  lazy val initialNormalPrefAvarage: Double = map.apply("pref.normalTrait.initialPrefAve").toDouble
  lazy val initialNormalPrefSigmaPerRange: Int = map.apply("pref.normalTrait.initialPrefSigmaPerRange").toInt
  lazy val initialHopedPrefAvarage: Double = map.apply("pref.hopedTrait.initialPrefAve").toDouble
  lazy val initialHopedPrefSigmaPerRange: Int = map.apply("pref.hopedTrait.initialPrefSigmaPerRange").toInt

  lazy val newTraitMutationRate: Double = map.apply("sim.mutation.newTrait").toDouble
  lazy val randomizePreferenceMutationRate: Double = map.apply("sim.mutation.randomizePreference").toDouble
  lazy val agentRebornMutationRate: Double = map.apply("sim.mutation.agentReborn").toDouble

  lazy val dbHostName: String = map.apply("mysql.hostName")
  lazy val dbName: String = map.apply("mysql.dbName")
  lazy val traitFreqHistoryTableName: String = map.apply("mysql.tableName.traitFreqHistory")
  lazy val preferenceHistoryForOneTraitTableName: String = map.apply("mysql.tableName.preferenceHistoryForOneTrait")

  //セキュアな設定ファイルの情報へのアクセス時のキー名
  lazy val dbId: String = secureMap.apply("mysql.id")
  lazy val dbPass: String = secureMap.apply("mysql.pass")

  //setterメソッド
  def setAntiConformThreshold(act: Double) {
    antiConformThreshold = act
  }
}

/**
 * 設定ファイルの読み込み&Propetyオブジェクトの初期化
 */
object PropertyReader {

  def read(): Unit = {
    //普通の設定ファイルの読み込み
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

    //セキュリティの関係上ネット上に上げられない設定ファイルの読み込み
    val secureSource = Source.fromFile("./resource/secure/secureProp.txt")
    var secureSourceStr: String = ""
    try {
      secureSourceStr = secureSource.getLines.toList.mkString("")
    } finally {
      secureSource.close()
    }
    val secureKeyValueMap = pattern.findAllIn(secureSourceStr).matchData.map { m: Match =>
      (m.group("key"), m.group("value"))
    }.toMap

    Property.init(keyValueMap, secureKeyValueMap)
    println("＊＊＊＊＊＊設定ファイル読み込み完了＊＊＊＊＊＊")
  }
}
