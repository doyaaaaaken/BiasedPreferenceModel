package doyaaaaaken.service

import doyaaaaaken.model.Agent
import doyaaaaaken.model.Network
import doyaaaaaken.model.TraitFreqHistory
import doyaaaaaken.service.imitationStrategy.Algorithm

/**
 * Agent同士で模倣しあう処理を行うクラス
 */
case class AgentImitationService(algorithm: Algorithm) {

  /*摸倣処理。引数に渡ってきたエージェントに変化を起こす*/
  def work(oldAgents: Map[Int, Agent], network: Network, traitFreq: TraitFreqHistory): Unit = {
    algorithm.work(oldAgents: Map[Int, Agent], network: Network, traitFreq: TraitFreqHistory)
  }
}