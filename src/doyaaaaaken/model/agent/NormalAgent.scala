package doyaaaaaken.model.agent

import doyaaaaaken.model.helper.TraitFactory

/**
 * Extreme様式に対して普通の態度をとるAgent
 */
class NormalAgent(agentId: Int, antiConformism: Double, traitFactory: TraitFactory)
  extends Agent(agentId: Int, antiConformism: Double, traitFactory: TraitFactory) {

  override def getAgentType: Int = AgentType.NORMAL
}