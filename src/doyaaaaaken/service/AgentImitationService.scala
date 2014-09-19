package doyaaaaaken.service

import doyaaaaaken.model.Agent
import doyaaaaaken.model.Network
import doyaaaaaken.model.TraitFreqHistory
import doyaaaaaken.service.strategy.BothTraitExistConditionCopyStrategy
import doyaaaaaken.service.strategy.OnlyAgentPossessTraitCopyStrategy

/**
 * Agent同士で模倣しあう処理を行うクラス
 */
object AgentImitationService {

  /*摸倣処理。引数に渡ってきたエージェントに変化を起こす*/
  def work(oldAgents: Map[Int, Agent], network: Network, traitFreq: TraitFreqHistory): Unit = {
    //    BothTraitExistConditionCopyStrategy.work(oldAgents: Map[Int, Agent], network: Network, traitFreq: TraitFreqHistory)
    OnlyAgentPossessTraitCopyStrategy.work(oldAgents: Map[Int, Agent], network: Network, traitFreq: TraitFreqHistory)
  }

  /**PomからComを計算する式*/
  private[service] def calcCom(Pom: Double): Double = {
    1.0 / (1 + Math.exp(-Pom))
  }
}