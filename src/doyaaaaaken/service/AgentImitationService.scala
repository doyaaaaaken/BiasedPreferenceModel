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
    val copyAgentInfoList2: Seq[(Agent, Int, Int, Double)] = copyAgentComList.map {
      case (agent, copyAgentId, pSum) => (agent, copyAgentId, traitFreq.getRandomTraitNum, pSum)
    }

    //アシンクロナスに相手の様式・好みをコピーする
    //TODO 未実装

  }

  /*PomからComを計算する式*/
  private def calcCom(Pom: Double): Double = {
    1.0 / (1 + Math.exp(-Pom))
  }
}