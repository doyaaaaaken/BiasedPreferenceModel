package doyaaaaaken.service.strategy

import doyaaaaaken.model.Network
import doyaaaaaken.model.TraitFreqHistory
import doyaaaaaken.model.Agent

abstract class Algorithm {
  /**アルゴリズムを起動させる*/
  private[service] def work(oldAgents: Map[Int, Agent], network: Network, traitFreq: TraitFreqHistory): Unit

  /**PomからComを計算する式*/
  private[service] def calcCom(Pom: Double): Double = {
    1.0 / (1 + Math.exp(-Pom))
  }
}