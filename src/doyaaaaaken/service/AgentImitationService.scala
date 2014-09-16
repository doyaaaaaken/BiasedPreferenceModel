package doyaaaaaken.service

import scala.util.Random

import doyaaaaaken.model.Agent
import doyaaaaaken.model.Network
import doyaaaaaken.model.TraitFreqHistory

/**
 * Agent同士で模倣しあう処理を行うクラス
 */
object AgentImitationService {

  /*摸倣処理。引数に渡ってきたエージェントに変化を起こす*/
  def work(oldAgents: Map[Int, Agent], network: Network, traitFreq: TraitFreqHistory): Unit = {

    //(Agentインスタンス , コピー相手先のエージェント番号)のリストを作る
    val copyAgentNumList: Seq[(Agent, Int)] = oldAgents.map {
      case (id, agent) => (agent, network.getLinkedAgentNums(id))
    }.map {
      case (agent, idList) => (agent, idList(new Random(9876).nextInt(idList.size)))
    }.toList

    //相手先に対する好みPomを計算し、それをComに変換してcopyAgentNumMapに情報付与したリストを作成する
    //(Agentインスタンス , コピー相手先のエージェント番号, Com)という形式にする
    val copyAgentComList: Seq[(Agent, Int, Double)] = copyAgentNumList.map {
      case (agent, copyAgentId) => (agent, copyAgentId, agent.calcPom(oldAgents.apply(copyAgentId).traits))
    }.map {
      case (agent, copyAgentId, pSum) => (agent, copyAgentId, calcCom(pSum))
    }

    //各エージェント、どの様式番号のt-pペアをコピーするのか決める
    //(Agentインスタンス、コピー先エージェント番号、コピー対象の様式番号、コピー確率)という形式にする
    val copyAgentInfoList: Seq[(Agent, Int, Int, Double)] = copyAgentComList.map {
      case (agent, copyAgentId, copyProb) => (agent, copyAgentId, traitFreq.getRandomTraitNum, copyProb)
    }

    /*
     * アシンクロナスに相手の様式・好みをコピーする
     *アシンクロナスに行うため現在の相手の状態を直接コピーするのではなく一旦変数に入れる
     */
    //(Agentのインスタンス、コピー対象の様式番号、様式ありorなしの状態値)の形式。確率判定で成功しコピ－を行うエージェントのみリストに入れる。
    val traitCopyInfoList: Seq[(Agent, Int, Boolean)] = copyAgentInfoList.filter {
      case (agent, copyAgentId, targetTraitNum, copyProb) => copyProb > Math.random()
    }.map {
      case (agent, copyAgentId, targetTraitNum, copyProb) => (agent, targetTraitNum, oldAgents.apply(copyAgentId).traits.contains(targetTraitNum))
    }
    //(Agentのインスタンス、コピー対象にする様式番号、好みの状態値-1.0～1.0)の形式。確率判定で成功しコピ－を行うエージェントのみリストに入れる。
    val preferenceCopyInfoList: Seq[(Agent, Int, Double)] = copyAgentInfoList.filter {
      case (agent, copyAgentId, targetTraitNum, copyProb) => copyProb > Math.random()
    }.map {
      case (agent, copyAgentId, targetTraitNum, copyProb) => (agent, targetTraitNum, oldAgents.apply(copyAgentId).preference.getPreferenceValue(targetTraitNum))
    }
    //コピー成功エージェントがtraitのコピーを行う
    traitCopyInfoList.foreach {
      case (agent, targetTraitNum, existTrait) => agent.changeTrait(targetTraitNum, existTrait)
    }
    //コピー成功エージェントがpreferenceのコピーを行う
    preferenceCopyInfoList.foreach {
      case (agent, targetTraitNum, prefValue) => agent.changePreference(targetTraitNum, prefValue)
    }
  }

  /*PomからComを計算する式*/
  private def calcCom(Pom: Double): Double = {
    1.0 / (1 + Math.exp(-Pom))
  }
}