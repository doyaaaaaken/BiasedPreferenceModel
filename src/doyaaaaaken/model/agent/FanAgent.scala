package doyaaaaaken.model.agent

import doyaaaaaken.model.helper.TraitFactory

/**
 * Extreme様式に対して熱狂的な信者となっているエージェント
 */
class FanAgent(agentId: Int, antiConformism: Double, traitFactory: TraitFactory)
  extends Agent(agentId: Int, antiConformism: Double, traitFactory: TraitFactory) {

  override def getAgentType: Int = AgentType.FAN
}