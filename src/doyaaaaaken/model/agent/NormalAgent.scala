package doyaaaaaken.model.agent

import doyaaaaaken.model.helper.TraitFactory

/**
 * Extreme様式に対して普通の態度をとるAgent
 */
class NormalAgent(agentId: Int, antiConformism: Double, traitFactory: TraitFactory)
  extends Agent(agentId: Int, antiConformism: Double, traitFactory: TraitFactory) {

  /**オブジェクトのAgentType（Normal,Fan,Critics）を返す*/
  override def getAgentType: Int = AgentType.NORMAL

  /**
   * 受け取った様式番号リストに対して、好みの総和Pomを返す
   */
  override def calcPom(copyTraitNums: Seq[Int]): Double = {
    preference.calcPrefSum(copyTraitNums, AgentType.NORMAL)
  }
}