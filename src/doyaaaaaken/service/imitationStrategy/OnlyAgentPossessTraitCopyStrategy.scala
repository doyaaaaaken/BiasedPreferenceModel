package doyaaaaaken.service.imitationStrategy

import doyaaaaaken.service.AgentImitationService
import doyaaaaaken.model.Network
import doyaaaaaken.model.TraitFreqHistory
import doyaaaaaken.model.Agent
import doyaaaaaken.main.boot.Property
import scala.util.Random

/**
 * 【AgentImitationServiceオブジェクトでのみ使われるアルゴリズム】
 *
 *  Agentが持っている様式しかコピーしないアルゴリズム
 */
@deprecated //恐らくはもう使わないアルゴリズム
object OnlyAgentPossessTraitCopyStrategy extends Algorithm {

  override def work(oldAgents: Map[Int, Agent], network: Network, traitFreq: TraitFreqHistory): Unit = {

    //エージェント群を、(Agentインスタンス , コピー相手先のエージェント番号, コピー確率Com)という形式にする
    val copyAgentComList: Seq[(Agent, Int, Double)] = getCopyAgentComList(oldAgents, network)

    //各エージェント、どの様式番号のt-pペアをコピーするのか決める
    //(Agentインスタンス、コピー先エージェント番号、コピー対象の様式番号、コピー確率)という形式にする
    val copyAgentInfoList: Seq[(Agent, Int, Option[Int], Double)] = copyAgentComList.map {
      case (agent, copyAgentId, copyProb) => (agent, copyAgentId, oldAgents(copyAgentId).getTraitKindRandom, copyProb)
    }

    /*
     * アシンクロナスに相手の様式・好みをコピーする
     * アシンクロナスに行うため現在の相手の状態を直接コピーするのではなく一旦変数に入れる
     */
    //(Agentのインスタンス、コピー対象の様式番号)の形式。確率判定で成功し”様式ありの状態”のコピ－を行うエージェントのみリストに入れる。
    val traitCopyInfoList: Seq[(Agent, Int)] = copyAgentInfoList.filter {
      case (agent, copyAgentId, targetTraitNum, copyProb) => copyProb > Math.random() && targetTraitNum.isDefined
    }.map {
      case (agent, copyAgentId, targetTraitNum, copyProb) => (agent, targetTraitNum.get)
    }
    //(Agentのインスタンス、コピー対象にする様式番号、好みの状態値-1.0～1.0)の形式。確率判定で成功しコピ－を行うエージェントのみリストに入れる。
    val preferenceCopyInfoList: Seq[(Agent, Int, Double)] = copyAgentInfoList.filter {
      case (agent, copyAgentId, targetTraitNum, copyProb) => copyProb > Math.random() && targetTraitNum.isDefined
    }.map {
      case (agent, copyAgentId, targetTraitNum, copyProb) => (agent, targetTraitNum.get, oldAgents.apply(copyAgentId).preference.getPreferenceValue(targetTraitNum.get))
    }
    debugPrint(Property.debug, copyAgentComList, copyAgentInfoList, traitCopyInfoList, preferenceCopyInfoList) //デバッグモードの場合は変数の値をコンソール出力する
    //コピー成功エージェントがtraitのコピーを行う
    traitCopyInfoList.foreach {
      case (agent, targetTraitNum) => agent.changeTrait(targetTraitNum, true)
    }
    //コピー成功エージェントがpreferenceのコピーを行う
    preferenceCopyInfoList.foreach {
      case (agent, targetTraitNum, prefValue) => agent.changePreference(targetTraitNum, prefValue)
    }
  }

}