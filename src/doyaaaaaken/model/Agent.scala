package doyaaaaaken.model

import doyaaaaaken.model.helper.TraitFactory
import doyaaaaaken.main.properties.Property

class Agent(agentIdNum: Int, antiConf: Double, traitFactoryInstance: TraitFactory) {

  val traitFactory = traitFactoryInstance

  val agentId = agentIdNum
  val possessTraitNumCapacity = Property.agentPossessTraitCapacity //持てる様式の上限数

  var traits: Seq[Int] = traitFactory.getInitialTrait
  var preference: Map[Int, Double] = null

  val antiConformism = antiConf //はやりと差別化したがる傾向([0,1]の範囲。1に近いほど差別化欲求が大きく0に近いほど同調欲求が大きい)

}

object AgentFactory {

  var agentId = -1 //作成するAgentにつけるID
  val traitFactory: TraitFactory = TraitFactory.apply()

  def create(): Agent = {
    agentId += 1
    new Agent(agentId, Math.random(), traitFactory)
  }
}