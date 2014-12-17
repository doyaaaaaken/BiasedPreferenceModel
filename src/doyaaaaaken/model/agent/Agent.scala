package doyaaaaaken.model.agent

import scala.util.Random
import doyaaaaaken.model.Preference
import doyaaaaaken.model.helper.TraitFactory
import doyaaaaaken.main.boot.Property

abstract class Agent(
  agentId: Int,
  antiConformism: Double, //はやりと差別化したがる傾向([0,1]の範囲。1に近いほど差別化欲求が大きく0に近いほど同調欲求が大きい)
  traitFactory: TraitFactory) {

  val id = agentId

  //TODO 様式上限数系の制限を実装する
  val possessTraitNumCapacity = Property.agentPossessTraitCapacity //持てる様式の上限数

  var traits: Seq[Int] = traitFactory.getInitialTrait
  val preference: Preference = Preference.apply

  /**
   * オブジェクトのAgentType（Normal,Fan,Critics）を返す
   */
  def getAgentType(): Int

  /**
   * 受け取った様式番号リストに対して、好みの総和Pomを返す
   */
  def calcPom(copyTraitNums: Seq[Int]): Double

  /**自身が保持する様式の種類をランダムに返す*/
  def getTraitKindRandom: Option[Int] = {
    if (traits.isEmpty) None else Some(traits(new Random().nextInt(traits.size)))
  }

  /**指定の様式番号の様式を持つor持たない状態にする*/
  def changeTrait(traitNum: Int, existTraitCondition: Boolean): Unit = {
    if (traits.contains(traitNum) == true && existTraitCondition == false) {
      traits = traits.filter(_ != traitNum)
    } else if (traits.contains(traitNum) == false && existTraitCondition == true) {
      //TODO 持てる様式の上限数以下の場合のみ、という条件を入れる
      traits = traits :+ traitNum
    }
  }

  /**エージェントが第一引数の様式番号に対しAnti-conformistであるかを判定する*/
  def isAnti(targetTraitKind: Option[Int], linkedAgentNums: Seq[Int], agents: Map[Int, Agent]): Boolean = {
    //注：自分自身の様式は考慮に入れない（なぜなら新興の様式がはやろうとした時に初めから普及率１に近い値になってしまうため）
    //seenTraitListのサイズがAgentNum以下だった場合はAntiにならない、的な制約入れている。
    val seenTraitList: Seq[Int] = linkedAgentNums.flatMap(agents(_).traits) //自身から見える様式群 例）(2,3,3,3,4,5,7)
    val diffusionRate: Double = if (seenTraitList.isEmpty || seenTraitList.size <= Property.agentNum) 0.0 else seenTraitList.filter(t => targetTraitKind.isDefined && targetTraitKind.get == t).size.toDouble / seenTraitList.size.toDouble //様式の普及率
    diffusionRate > 1 - antiConformism
  }

  /**エージェントがアンチ行動をとる*/
  def becomeAnti(traitKind: Int): Unit = {
    //エージェントは、選んだ様式を保持しない状態になる＆その様式に対する好みが-1となる
    traits = traits.filterNot(_ == traitKind)
    preference.changePrefValue(traitKind, -1.0)
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
    preference.rebornAgent(currentTraitList, false)
  }

  /**新規様式を獲得する*/
  def acquireNewTrait: Unit = {
    traits = traits :+ traitFactory.getNewTrait
  }

  /**【デバッグ用】 エージェントの情報をコンソール出力する*/
  def debugPrint(): Unit = {
    println("+++++++++++++++++")
    println("agentId = " + agentId + " , antiConformism = " + antiConformism + " , traitList = " + traits + " , preferences = " + preference.getPreference)
    println("+++++++++++++++++")
  }
}

/**
 * Agentの種類を表すオブジェクト
 */
object AgentType {
  val NORMAL = 1234
  val FAN = 5678
  val CRITICS = 9012
}

/**
 * Agent生成の責務を担うオブジェクト
 */
object AgentFactory {

  var agentId = -1 //作成するAgentにつけるID
  var traitFactory: TraitFactory = TraitFactory.apply()

  def create(): Agent = {
    agentId += 1
    val antiConformism: Double = new Random().nextGaussian() / 8.0 * 3.0 + 0.5 //平均が0.5、分散が3/8の正規分布

    if (agentId < Property.fanAgentNum) new FanAgent(agentId, antiConformism, traitFactory)
    else if (agentId < Property.fanAgentNum + Property.criticsAgentNum) new CriticsAgent(agentId, antiConformism, traitFactory)
    else new NormalAgent(agentId, antiConformism, traitFactory)
  }

  def init(): Unit = {
    agentId = -1
    traitFactory = TraitFactory.apply()
  }
}