package doyaaaaaken.service.imitationStrategy

import doyaaaaaken.model.Network
import doyaaaaaken.model.TraitFreqHistory
import scala.util.Random
import doyaaaaaken.model.agent.Agent

abstract class Algorithm {
  /**アルゴリズムを起動させる*/
  private[service] def work(oldAgents: Map[Int, Agent], network: Network, traitFreq: TraitFreqHistory): Unit

  /**PomからComを計算する式*/
  private[imitationStrategy] def calcCom(Pom: Double): Double = {
    1.0 / (1 + Math.exp(-Pom))
  }

  /**エージェント群を、(Agentインスタンス , コピー相手先のエージェント番号, コピー確率Com)という形式にする*/
  private[imitationStrategy] def getCopyAgentComList(agents: Map[Int, Agent], network: Network): Seq[(Agent, Int, Double)] = {
    //(Agentインスタンス , コピー相手先のエージェント番号)のリストを作る
    val copyAgentNumList: Seq[(Agent, Int)] = agents.map {
      case (id, agent) => (agent, network.getLinkedAgentNums(id))
    }.map {
      case (agent, idList) => (agent, idList(new Random().nextInt(idList.size)))
    }.toList
    //相手先に対する好みPomを計算し、それをComに変換してcopyAgentNumListに情報付与したリストを作成する
    copyAgentNumList.map {
      case (agent, copyAgentId) => (agent, copyAgentId, agent.calcPom(agents.apply(copyAgentId).traits))
    }.map {
      case (agent, copyAgentId, pSum) => (agent, copyAgentId, calcCom(pSum))
    }
  }

  /**デバッグ用：摸倣の際の各種変数値をコンソール出力する*/
  private[imitationStrategy] def debugPrint(debug: Boolean, copyAgentComList: Seq[(Agent, Int, Double)], copyAgentInfoList: Seq[AnyRef], traitCopyInfoList: Seq[AnyRef], preferenceCopyInfoList: Seq[(Agent, Int, Double)]): Unit = {
    if (debug) {
      println("""|＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
							 |AgentImitationService（摸倣処理）のデバッグ開始
""".stripMargin)

      println("(AgentId , Agentインスタンス , コピー相手先のエージェント番号, Com) : " + copyAgentComList.map(seq => (seq._1.id, seq._1, seq._2, seq._3)))
      println("(Agentインスタンス、コピー先エージェント番号、コピー対象の様式番号、コピー確率) : " + copyAgentInfoList)
      println("*コピーするもののみ (Agentのインスタンス、コピー対象の様式番号、様式ありorなしの状態値) : " + traitCopyInfoList)
      println("*コピーするもののみ (Agentのインスタンス、コピー対象の様式番号、好みの状態値-1.0～1.0) : " + preferenceCopyInfoList)

      println("""|この後摸倣に寄るTrait,Preferenceの変更を行う。なのでデバッグ出力はここで終了
					     |＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
""".stripMargin)
    }
  }
}