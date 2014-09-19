package doyaaaaaken.main

import doyaaaaaken.main.boot.Boot
import doyaaaaaken.main.boot.Property
import doyaaaaaken.model.CompleteGraphFactory
import doyaaaaaken.model.Network
import doyaaaaaken.model.AgentFactory
import doyaaaaaken.model.Agent
import doyaaaaaken.service.AgentImitationService
import doyaaaaaken.model.TraitFreqHistory
import doyaaaaaken.main.db.DbSession
import doyaaaaaken.service.AgentMutationService
/**
 * シミュレーションの本骨組みとなるMainクラス
 */
object Main {

  var currentTraitFreq: TraitFreqHistory = null //現タイムステップに存在する様式リストを保持する

  def main(args: Array[String]): Unit = {

    Boot.start(useDb = true)

    /*エージェント間の繋がりを示すネットワークの生成*/
    val network: Network = CompleteGraphFactory.create(Property.agentNum) //完全グラフ
    /*エージェントの初期化*/
    val tmp = for (i <- 0 to Property.agentNum - 1) yield { (i, AgentFactory.create()) }
    val agents: Map[Int, Agent] = tmp.toMap //AgentNum体のエージェントセット

    currentTraitFreq = TraitFreqHistory.apply(0, agents) //現在存在する様式リストの作成

    /*シミュレーションの実行*/
    for (i <- 1 to Property.simNum) {
      //模倣フェーズ・・・全エージェント、自分と繋がっている他のエージェントを模倣する(アシンクロナス)
      AgentImitationService.work(agents, network, currentTraitFreq)

      //突然変異フェーズ
      AgentMutationService.randomizePreference(agents) //好みをランダムに振り直す
      AgentMutationService.reborn(agents, currentTraitFreq.getCurrentTraitKindList) //エージェントが転生する
      AgentMutationService.acquireNewTrait(agents) //新規様式の発生

      //現在存在する様式リストの更新（注：「突然変異：新規様式の発生」により現存する様式リストに変更があったため更新）
      currentTraitFreq = TraitFreqHistory.apply(i, agents)

      //TODO preferenceの値が長くなりすぎるのを防ぐため、currentTraitFreqにないものは消す【計算量削減処置】

      //データの格納
      if (i % Property.dbSaveInterval == 0) currentTraitFreq.insertDataSet(i, DbSession.getConnection)

      if (i % 100 == 0) println(i + "タイムステップ経過")
    }

    Boot.finish
  }
}