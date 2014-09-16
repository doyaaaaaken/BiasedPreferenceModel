package doyaaaaaken.model

import doyaaaaaken.model.helper.TraitFactory
import doyaaaaaken.main.boot.Property

class Agent(
  agentId: Int,
  antiConformism: Double, //はやりと差別化したがる傾向([0,1]の範囲。1に近いほど差別化欲求が大きく0に近いほど同調欲求が大きい)
  traitFactory: TraitFactory) {

  val possessTraitNumCapacity = Property.agentPossessTraitCapacity //持てる様式の上限数

  var traits: Seq[Int] = traitFactory.getInitialTrait
  val preference: Preference = Preference.apply

  /**
   * 受け取った様式番号リストに対して、好みの総和Pomを返す
   */
  def calcPom(copyTraitNums: Seq[Int]): Double = {
    preference.calcPrefSum(copyTraitNums)
  }

}

object AgentFactory {

  var agentId = -1 //作成するAgentにつけるID
  val traitFactory: TraitFactory = TraitFactory.apply()

  def create(): Agent = {
    agentId += 1
    new Agent(agentId, Math.random(), traitFactory)
  }
}