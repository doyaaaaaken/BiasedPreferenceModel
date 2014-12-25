package doyaaaaaken.model

import doyaaaaaken.model.helper.TraitFactory
import doyaaaaaken.main.boot.Property
import scala.util.Random
import scala.collection.mutable.ListBuffer

class Agent(
  agentId: Int,
  antiConformThreshold: Double, //差別化行動を行う閾値([0,1]の範囲。コピー対象の様式がこの値より普及度が高ければ、差別化行動を行う)
  memory: ListBuffer[Map[Int, Int]], //直近10回の自分・コピー相手を見た、[様式番号->度数]の記憶を保持している
  traitFactory: TraitFactory) {

  val id = agentId

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
    } else if (traits.contains(traitNum) == false && existTraitCondition == true) {
      traits = traits :+ traitNum
    }
  }

  /**記憶の変更*/
  def updateMemory(otherPersonTraits: Seq[Int]): Unit = {
    //一番前の記憶(Map)を削除
    memory.remove(0)
    //一番後ろに、自身のtraitとコピー相手のtraitを合わせたMapを挿入する
    val traitsList: Seq[Int] = traits.toList ++ otherPersonTraits
    memory += traitsList.groupBy(x => x).map(x => (x._1, x._2.size))
  }

  /**ある様式種類の普及率を自身の記憶から計算する*/
  def calcDiffusion(traitNum: Int): Double = {
    var allTraitNumCount = 0 //Memory内の全様式数をカウント
    var targetTraitNumCount = 0 //Memory内の計算対象である様式の数をカウント

    //指定された様式番号の普及率が自分から見ればいくつなのかを確認する
    memory.foreach { traitFreqMap =>
      if (traitFreqMap.isEmpty) return 0 //EmptyMapが1つでもある場合は、メソッドを終了し普及率0を返してしまう
      traitFreqMap.foreach {
        case (traitKind, freq) => {
          if (traitKind == traitNum) targetTraitNumCount += 1
          allTraitNumCount += 1
        }
      }
    }
    targetTraitNumCount / allTraitNumCount
  }

  /**Memoryのゲッターメソッド*/
  def getMemory: Seq[Map[Int, Int]] = {
    memory.toList
  }

  //  /**エージェントが第一引数の様式番号に対しAnti-conformistであるかを判定する*/
  //  def isAnti(targetTraitKind: Option[Int], linkedAgentNums: Seq[Int], agents: Map[Int, Agent]): Boolean = {
  //    val agentNums = linkedAgentNums :+ id //自分含む、自身から見えるエージェント群のIDリスト
  //    val seenTraitList: Seq[Int] = agentNums.flatMap(agents(_).traits) //自身から見える様式群 例）(2,3,3,3,4,5,7)
  //    val diffusionRate: Double = if (seenTraitList.isEmpty) 0.0 else seenTraitList.filter(t => targetTraitKind.isDefined && targetTraitKind.get == t).size.toDouble / seenTraitList.size.toDouble //様式の普及率
  //    diffusionRate > 1 - antiConformism
  //  }

  //  /**エージェントが第一引数の様式番号に対しAnti-conformistであるかを判定する*/
  //  def isAnti(targetTraitKind: Option[Int], linkedAgentNums: Seq[Int], agents: Map[Int, Agent]): Boolean = {
  //    //注：自分自身の様式は考慮に入れない（なぜなら新興の様式がはやろうとした時に初めから普及率１に近い値になってしまうため）
  //    //seenTraitListのサイズがAgentNum以下だった場合はAntiにならない、的な制約入れている。
  //    val seenTraitList: Seq[Int] = linkedAgentNums.flatMap(agents(_).traits) //自身から見える様式群 例）(2,3,3,3,4,5,7)
  //    val diffusionRate: Double = if (seenTraitList.isEmpty || seenTraitList.size <= Property.agentNum) 0.0 else seenTraitList.filter(t => targetTraitKind.isDefined && targetTraitKind.get == t).size.toDouble / seenTraitList.size.toDouble //様式の普及率
  //    diffusionRate > 1 - antiConformThreshold
  //  }

  //  /**エージェントがアンチ行動をとる*/
  //  def becomeAnti(traitKind: Int): Unit = {
  //    //エージェントは、選んだ様式を保持しない状態になる＆その様式に対する好みが-1となる
  //    traits = traits.filterNot(_ == traitKind)
  //    preference.changePrefValue(traitKind, -1.0)
  //  }

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
    traits = traits :+ traitFactory.getNewTrait
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
  val memoryTimespanSize = 10; //直近何ステップ分の記憶を保持できるか

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