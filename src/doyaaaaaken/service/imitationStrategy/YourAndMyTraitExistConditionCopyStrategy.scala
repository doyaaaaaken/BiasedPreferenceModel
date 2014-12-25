package doyaaaaaken.service.imitationStrategy

import scala.util.Random

import doyaaaaaken.main.boot.Property
import doyaaaaaken.model.Agent
import doyaaaaaken.model.Network
import doyaaaaaken.model.TraitFreqHistory

/**
 * 【AgentImitationServiceオブジェクトでのみ使われるアルゴリズム】
 *
 *  自分と相手の様式を合わせたリストの中から様式ありorなしの状態をコピーする
 */
object YourAndMyTraitExistConditionCopyStrategy extends Algorithm {

  override def work(oldAgents: Map[Int, Agent], network: Network, traitFreq: TraitFreqHistory): Unit = {

    //エージェント群を、(Agentインスタンス , コピー相手先のエージェント番号, コピー確率Com)という形式にする
    val copyAgentComList: Seq[(Agent, Int, Double)] = getCopyAgentComList(oldAgents, network)

    //各エージェント、どの様式番号のt-pペアをコピーするのか決める
    //(Agentインスタンス、コピー先エージェント番号、コピー対象の様式番号、コピー確率)という形式にする
    var copyAgentInfoList: Seq[(Agent, Int, Option[Int], Double)] = copyAgentComList.map {
      case (agent, copyAgentId, copyProb) => (agent, copyAgentId, getRandomTraitKind(agent.traits ++: oldAgents(copyAgentId).traits), copyProb)
    }

    //    // 以下の箇所でanti-conformist biasの処理を施す
    //    if (isBiasedAlgorithm) {
    //      val copyAndAntiAgentsPartition = copyAgentInfoList.partition { //copy,antiの２つにエージェント群を切り分ける
    //        case (agent, targetAgentNum, targetTraitKind, copyProb) =>
    //          agent.isAnti(targetTraitKind, network.getLinkedAgentNums(agent.id), oldAgents)
    //      }
    //      copyAgentInfoList = copyAndAntiAgentsPartition._2 //2つに切り分けられたうちのcopy行動をとるAgent群のほうを代入
    //      copyAndAntiAgentsPartition._1.foreach { //anti行動を取ると判定をされたAgent群に対し、アンチになる処理を施す
    //        case (agent, targetAgentNum, targetTraitKind, copyProb) =>
    //          if (targetTraitKind.isDefined) agent.becomeAnti(targetTraitKind.get)
    //      }
    //    }

    /*
     * アシンクロナスに相手の様式・好みをコピーする
     * アシンクロナスに行うため現在の相手の状態を直接コピーするのではなく一旦変数に入れる
     */
    //(Agentのインスタンス、コピー対象の様式番号、様式ありorなしの状態値)の形式。確率判定で成功しコピ－を行うエージェントのみリストに入れる。
    val traitCopyInfoList: Seq[(Agent, Int, Boolean)] = copyAgentInfoList.filter {
      case (agent, copyAgentId, targetTraitNum, copyProb) => copyProb > Math.random() && targetTraitNum.isDefined
    }.map {
      case (agent, copyAgentId, targetTraitNum, copyProb) => (agent, targetTraitNum.get, oldAgents.apply(copyAgentId).traits.contains(targetTraitNum.get))
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
      case (agent, targetTraitNum, existTrait) => agent.changeTrait(targetTraitNum, existTrait)
    }
    //コピー成功エージェントがpreferenceのコピーを行う
    preferenceCopyInfoList.foreach {
      case (agent, targetTraitNum, prefValue) => agent.changePreference(targetTraitNum, prefValue)
    }
  }

  /**与えられたSeq[Int]から、ランダムに値を取り出す*/
  private[this] def getRandomTraitKind(traitKinds: Seq[Int]): Option[Int] = {
    val traitKindsSet = traitKinds.toSet
    if (traitKindsSet.isEmpty) None else Some(traitKindsSet.toList(new Random().nextInt(traitKindsSet.size)))
  }
}