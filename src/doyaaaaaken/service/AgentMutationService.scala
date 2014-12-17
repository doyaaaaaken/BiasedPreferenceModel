package doyaaaaaken.service

import doyaaaaaken.main.boot.Property
import doyaaaaaken.model.agent.Agent
import doyaaaaaken.model.helper.TraitFactory
import doyaaaaaken.model.agent.AgentFactory
import doyaaaaaken.model.agent.AgentType

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
    val latestTraitNum = AgentFactory.traitFactory.latestTraitKind //最新様式番号を一旦記録
    agents.foreach(aMap => if (Property.newTraitMutationRate > Math.random) aMap._2.acquireNewTrait) //新規様式の発生
    val newLatestTraitNum = AgentFactory.traitFactory.latestTraitKind //更新された最新様式番号を一旦記録

    val traitList = for (traitNum <- latestTraitNum to newLatestTraitNum) yield traitNum
    val newExtremeTraitList = traitList.filter(_ % Property.extremeTraitGenerateInterval == 0) //新しく追加されたExtreme様式番号群

    //Fanエージェントは新しくできたExtreme様式を初めから保持する
    agents.foreach(aMap =>
      if (aMap._2.getAgentType() == AgentType.FAN) newExtremeTraitList.foreach(traitNum =>
        aMap._2.changeTrait(traitNum, true)))
  }
}