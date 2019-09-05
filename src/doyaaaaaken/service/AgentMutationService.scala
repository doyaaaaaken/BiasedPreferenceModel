package doyaaaaaken.service

import doyaaaaaken.model.Agent
import doyaaaaaken.model.TraitFreqHistory
import doyaaaaaken.main.boot.Property

/**
 * Agent内で起こる突然変異の処理を行うクラス
 */
object AgentMutationService {

  /**好みをランダムに振り直す処理*/
  def randomizePreference(agents: Map[Int, Agent]): Unit = {
    agents.foreach(aMap => if (Property.randomizePreferenceMutationRate > Math.random()) aMap._2.randomizePreference)
  }

  /**エージェントの転生*/
  def reborn(agents: Map[Int, Agent], currentTraitList: Seq[Int]): Unit = {
    agents.foreach(aMap => if (Property.agentRebornMutationRate > Math.random) aMap._2.reborn(currentTraitList))
  }

  /**新規様式の発生*/
  def acquireNewTrait(agents: Map[Int, Agent]): Unit = {
    agents.foreach(aMap => if (Property.newTraitMutationRate > Math.random) aMap._2.acquireNewTrait)
  }
}