package doyaaaaaken.service

import doyaaaaaken.model.Agent
import doyaaaaaken.model.TraitFreqHistory

/**
 * Agent内で起こる突然変異の処理を行うクラス
 */
object AgentMutationService {

  /**新規様式の発生*/
  def acquireNewTrait(agents: Map[Int, Agent], currentTraitFreq: TraitFreqHistory): Unit = {

  }

  /**好みをランダムに振り直す処理*/
  def randomizePreference(agents: Map[Int, Agent]): Unit = {

  }

  /**エージェントの転生*/
  def reborn(agents: Map[Int, Agent]): Unit = {

  }
}