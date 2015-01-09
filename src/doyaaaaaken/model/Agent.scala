package doyaaaaaken.model

import doyaaaaaken.model.helper.TraitFactory
import doyaaaaaken.main.boot.Property
import scala.util.Random
import scala.collection.mutable.ListBuffer

class Agent(
  agentId: Int,
  antiConformThreshold: Double, //差別化行動を行う閾値([0,1]の範囲。コピー対象の様式がこの値より普及度が高ければ、差別化行動を行う)
  memory: ListBuffer[Map[Int, Int]], //memoryの長さ分の直近タイムステップの自分・コピー相手を見た、[様式番号->度数]の記憶を保持している
  traitFactory: TraitFactory) {

  val id = agentId
  val possessTraitNumCapacity = Property.agentPossessTraitCapacity //持てる様式の上限数

  var traits: Seq[Int] = traitFactory.getInitialTrait
  val preference: Preference = Preference.apply

  /**
   * 受け取った様式番号リストに対して、好みの総和Pomを返す
   */
  def calcPom(copyTraitNums: Seq[Int]): Double = {
    preference.calcPrefSum(copyTraitNums)
  }

  /**自身が保持する様式の種類をランダムに返す*/
  def getTraitKindRandom: Option[Int] = {
    if (traits.isEmpty) None else Some(traits(new Random().nextInt(traits.size)))
  }

  /**指定の様式番号の様式を持つor持たない状態にする*/
  def changeTrait(traitNum: Int, existTraitCondition: Boolean): Unit = {
    if (traits.contains(traitNum) == true && existTraitCondition == false) {
      traits = traits.filter(_ != traitNum)
    } else if (traits.contains(traitNum) == false && existTraitCondition == true && traits.length < possessTraitNumCapacity) {
      traits = traits :+ traitNum
    }
  }

  /**記憶の変更*/
  def updateMemory(otherPersonTraits: Seq[Int]): Unit = {
    //一番前の記憶(Map)を削除
    memory.remove(0)
    //一番後ろに、自身のtraitとコピー相手のtraitを合わせたMapを挿入する
    val traitsList: Seq[Int] = traits.toList ++ otherPersonTraits.toList
    memory += traitsList.groupBy(x => x).map(x => (x._1, x._2.size))
  }

  /**ある様式種類の普及率を自身の記憶から計算する*/
  def calcDiffusion(traitNum: Int): Double = {
    var allTraitNumCount = 0.0 //Memory内の全様式数をカウント
    var targetTraitNumCount = 0.0 //Memory内の計算対象である様式の数をカウント

    //指定された様式番号の普及率が自分から見ればいくつなのかを確認する
    memory.foreach { traitFreqMap =>
      if (traitFreqMap.isEmpty) return 0 //EmptyMapが1つでもある場合は、メソッドを終了し普及率0を返してしまう
      traitFreqMap.foreach {
        case (traitKind, freq) => {
          if (traitKind == traitNum) targetTraitNumCount += freq
          allTraitNumCount += freq
        }
      }
    }
    targetTraitNumCount / allTraitNumCount
  }

  /**普及率とAnti-Conform Thresholdから、差別化行動をとるかどうかを判定する*/
  def isDifferentiate(traitNum: Int): Boolean = {
    val diffusionRate = calcDiffusion(traitNum)
    if (antiConformThreshold < diffusionRate) true else false
  }

  /**Memoryのゲッターメソッド*/
  def getMemory: Seq[Map[Int, Int]] = {
    memory.toList
  }

  /**差別化行動をとる。指定の様式を破棄し、代わりに別の指定の様式を取得する*/
  def actDifferentiation(abondonTraitKind: Int, gotTraitKind: Option[Int]): Unit = {
    if (traits.contains(abondonTraitKind)) traits = traits.filter(_ != abondonTraitKind)
    val abondonTraitDiffusion = calcDiffusion(abondonTraitKind)
    val gotTraitDiffusion = calcDiffusion(gotTraitKind.getOrElse(-1))
    if (abondonTraitDiffusion >= gotTraitDiffusion && gotTraitKind.isDefined && !traits.contains(gotTraitKind.get) && traits.length < possessTraitNumCapacity) traits = traits :+ gotTraitKind.get
  }

  /**指定の様式番号の様式に対する好みを変更する*/
  def changePreference(traitNum: Int, prefValue: Double): Unit = {
    preference.changePrefValue(traitNum, prefValue)
  }

  /**好みの値をランダムに振り直す*/
  def randomizePreference(): Unit = {
    preference.randomizePrefValue
  }

  /**引数に指定された様式群以外に対する好みを消す*/
  def eraseExceptNecessaryPreference(currentTraitList: Seq[Int]): Unit = {
    preference.eraseExceptNecessaryPreference(currentTraitList)
  }

  /**エージェントが転生する（様式を全て破棄して、現存する全様式に対する好みを1にする）*/
  def reborn(currentTraitList: Seq[Int]): Unit = {
    traits = Nil
    preference.rebornAgent(currentTraitList)
  }

  /**新規様式を獲得する*/
  def acquireNewTrait: Unit = {
    if (traits.length < possessTraitNumCapacity) {
      traits = traits :+ traitFactory.getNewTrait
    }
  }

  /**【デバッグ用】 エージェントの情報をコンソール出力する*/
  def debugPrint(): Unit = {
    println("+++++++++++++++++")
    println("agentId = " + agentId + " , antiConformism = " + antiConformThreshold + " , traitList = " + traits + " , memory = " + memory + " , preferences = " + preference.getPreference)
    println("+++++++++++++++++")
  }
}

object AgentFactory {

  var agentId = -1 //作成するAgentにつけるID
  var traitFactory: TraitFactory = TraitFactory.apply()
  val memoryTimespanSize = Property.memoryTimespanSize; //直近何ステップ分の記憶を保持できるか

  def create(): Agent = {
    agentId += 1
    val antiConformThreshold: Double = Property.antiConformThreshold
    val memory: ListBuffer[Map[Int, Int]] = ListBuffer()
    for (i <- 1 to memoryTimespanSize) { memory += Map.empty[Int, Int] }
    new Agent(agentId, antiConformThreshold, memory, traitFactory)
  }

  def init(): Unit = {
    agentId = -1
    traitFactory = TraitFactory.apply()
  }
}