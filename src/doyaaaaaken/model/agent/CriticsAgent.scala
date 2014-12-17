package doyaaaaaken.model.agent

import doyaaaaaken.model.helper.TraitFactory

/**
 * Extreme様式に対して、アンチの態度を示すエージェント
 */
class CriticsAgent(agentId: Int, antiConformism: Double, traitFactory: TraitFactory)
  extends Agent(agentId: Int, antiConformism: Double, traitFactory: TraitFactory) {

  override def getAgentType: Int = AgentType.CRITICS
}