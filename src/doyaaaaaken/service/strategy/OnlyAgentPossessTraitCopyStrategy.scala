package doyaaaaaken.service.strategy

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
object OnlyAgentPossessTraitCopyStrategy extends Algorithm {

  override def work(oldAgents: Map[Int, Agent], network: Network, traitFreq: TraitFreqHistory): Unit = {

    //(Agentインスタンス , コピー相手先のエージェント番号)のリストを作る
    val copyAgentNumList: Seq[(Agent, Int)] = oldAgents.map {
      case (id, agent) => (agent, network.getLinkedAgentNums(id))
    }.map {
      case (agent, idList) => (agent, idList(new Random().nextInt(idList.size)))
    }.toList

    //相手先に対する好みPomを計算し、それをComに変換してcopyAgentNumListに情報付与したリストを作成する
    //(Agentインスタンス , コピー相手先のエージェント番号, Com)という形式にする
    val copyAgentComList: Seq[(Agent, Int, Double)] = copyAgentNumList.map {
      case (agent, copyAgentId) => (agent, copyAgentId, agent.calcPom(oldAgents.apply(copyAgentId).traits))
    }.map {
      case (agent, copyAgentId, pSum) => (agent, copyAgentId, calcCom(pSum))
    }

    //各エージェント、どの様式番号のt-pペアをコピーするのか決める
    //(Agentインスタンス、コピー先エージェント番号、コピー対象の様式番号、コピー確率)という形式にする
    val copyAgentInfoList: Seq[(Agent, Int, Option[Int], Double)] = copyAgentComList.map {
      case (agent, copyAgentId, copyProb) => (agent, copyAgentId, oldAgents(copyAgentId).getTraitKindRandom, copyProb)
    }

    /*
       * アシンクロナスに相手の様式・好みをコピーする
       *アシンクロナスに行うため現在の相手の状態を直接コピーするのではなく一旦変数に入れる
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
    debugPrint(Property.debug, copyAgentNumList, copyAgentComList, copyAgentInfoList, traitCopyInfoList, preferenceCopyInfoList) //デバッグモードの場合は変数の値をコンソール出力する
    //コピー成功エージェントがtraitのコピーを行う
    traitCopyInfoList.foreach {
      case (agent, targetTraitNum) => agent.changeTrait(targetTraitNum, true)
    }
    //コピー成功エージェントがpreferenceのコピーを行う
    preferenceCopyInfoList.foreach {
      case (agent, targetTraitNum, prefValue) => agent.changePreference(targetTraitNum, prefValue)
    }
  }

  /**デバッグ用：摸倣の際の各種変数値をコンソール出力する*/
  private[service] def debugPrint(debug: Boolean, copyAgentNumList: Seq[(Agent, Int)], copyAgentComList: Seq[(Agent, Int, Double)], copyAgentInfoList: Seq[(Agent, Int, Option[Int], Double)], traitCopyInfoList: Seq[(Agent, Int)], preferenceCopyInfoList: Seq[(Agent, Int, Double)]): Unit = {
    if (debug) {
      println("""|＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
							 |AgentImitationService（摸倣処理）のデバッグ開始
""".stripMargin)

      println("(AgentインスタンスとIDの対応) : " + copyAgentNumList.map(aMap => (aMap._1, aMap._1.id)))
      println("(Agentインスタンス , コピー相手先のエージェント番号, Com) : " + copyAgentComList)
      println("(Agentインスタンス、コピー先エージェント番号、コピー対象の様式番号、コピー確率) : " + copyAgentInfoList)
      println("*コピーするもののみ (Agentのインスタンス、コピー対象の様式番号、様式ありorなしの状態値) : " + traitCopyInfoList)
      println("*コピーするもののみ (Agentのインスタンス、コピー対象の様式番号、好みの状態値-1.0～1.0) : " + preferenceCopyInfoList)

      println("""|この後摸倣に寄るTrait,Preferenceの変更を行う。なのでデバッグ出力はここで終了
					     |＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊
""".stripMargin)
    }
  }

}