package doyaaaaaken.model

import doyaaaaaken.model.helper.TraitFactory

class Agent(agentIdNum: Int, antiConf: Double, traitFactoryInstance: TraitFactory) {

  val agentId = agentIdNum
  val possessTraitNumCapacity = 0 //持てる様式の上限数 //TODO Property化

  val traits: Seq[Int] = traitFactoryInstance.getInitialTrait
  val preference: Map[Int, Double] = null

  val antiConformism = antiConf //はやりと差別化したがる傾向([0,1]の範囲。1に近いほど差別化欲求が大きく0に近いほど同調欲求が大きい)

  val traitFactory = traitFactoryInstance

}

object AgentFactory {

  var agentId = -1 //作成するAgentにつけるID
  val traitFactory: TraitFactory = TraitFactory()

  def create(): Agent = {
    agentId += 1
    new Agent(agentId, Math.random(), traitFactory)
  }
}