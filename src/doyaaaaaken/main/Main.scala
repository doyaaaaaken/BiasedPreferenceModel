package doyaaaaaken.main

import doyaaaaaken.main.boot.Boot
import doyaaaaaken.main.boot.Property
import doyaaaaaken.main.db.DbSession
import doyaaaaaken.model.Agent
import doyaaaaaken.model.AgentFactory
import doyaaaaaken.model.CompleteGraphFactory
import doyaaaaaken.model.Network
import doyaaaaaken.model.TraitFreqHistory
import doyaaaaaken.service.AgentImitationService
import doyaaaaaken.service.AgentImitationService
import doyaaaaaken.service.AgentMutationService
import doyaaaaaken.service.imitationStrategy.BothTraitExistConditionCopyStrategy
import doyaaaaaken.service.imitationStrategy.OnlyAgentPossessTraitCopyStrategy
import doyaaaaaken.service.imitationStrategy.YourAndMyTraitExistConditionCopyStrategy
import doyaaaaaken.model.PreferenceHistoryForOneTrait
/**
 * シミュレーションの本骨組みとなるMainクラス
 */
object Main {

  var currentTraitFreq: TraitFreqHistory = null //現タイムステップに存在する様式リストを保持する
  val agentImitationService: AgentImitationService = new AgentImitationService(YourAndMyTraitExistConditionCopyStrategy) //注：Agentが持つ様式のみを模倣するアルゴリズムを用いている

  def main(args: Array[String]): Unit = {

    Boot.start(useDb = true)

    /*エージェント間の繋がりを示すネットワークの生成*/
    val network: Network = CompleteGraphFactory.create(Property.agentNum) //完全グラフ

    var outputCount = 0 //Boot.output関数を呼んだ回数

    Property.antiConformThresholdList.foreach { antiConformThreshold =>
      /*変数（antiConformThreshold）をセットしてシミュレーションを行う*/
      Boot.refresh(antiConformThreshold)

      /*指定の回数シミュレーションを実行する*/
      for (simNum <- (1 to Property.simNum)) {

        /*エージェントファクトリーの初期化*/
        AgentFactory.init()

        /*エージェントの初期化*/
        val tmp = for (i <- 0 to Property.agentNum - 1) yield { (i, AgentFactory.create()) }
        val agents: Map[Int, Agent] = tmp.toMap //AgentNum体のエージェントセット

        currentTraitFreq = TraitFreqHistory.apply(simNum, 0, agents) //現在存在する様式リストの作成

        /*指定のタイムステップ数シミュレーションを実行する*/
        for (time <- 1 to Property.simTimeNum) {

          //模倣フェーズ・・・全エージェント、自分と繋がっている他のエージェントを模倣する(アシンクロナス)
          agentImitationService.work(agents, network, currentTraitFreq)

          //突然変異フェーズ
          AgentMutationService.randomizePreference(agents) //好みをランダムに振り直す
          AgentMutationService.reborn(agents, currentTraitFreq.getCurrentTraitKindList) //エージェントが転生する
          AgentMutationService.acquireNewTrait(agents) //新規様式の発生

          //現在存在する様式リストの更新（注：「突然変異：新規様式の発生」により現存する様式リストに変更があったため更新）
          currentTraitFreq = TraitFreqHistory.apply(simNum, time, agents)

          //【計算量削減処置】preferenceの値が長くなりすぎるのを防ぐため、現存する様式に対する好み以外は消す
          agents.foreach { agent => agent._2.eraseExceptNecessaryPreference(currentTraitFreq.getCurrentTraitKindList) }

          //Preference推移監視対象の様式番号が存在する間は、その様式番号に対するPreference群をDBに記録する
          if (currentTraitFreq.contains(Property.prefDbSaveTraitKind)) PreferenceHistoryForOneTrait.apply(simNum, time, Property.prefDbSaveTraitKind, agents).insertDataSet(DbSession.getConnection)

          //データの格納
          if (time % Property.dbSaveInterval == 0 && time >= Property.dbSaveStartTime) currentTraitFreq.insertDataSet(DbSession.getConnection)

          if (time % 100 == 0) println(simNum + "回目Sim ： " + time + "タイムステップ経過")
          if (time == Property.simTimeNum) println(Util.getMemoryInfo())
        }
      }
      Boot.output(outputCount)
      outputCount += 1
    }

    Boot.finish
  }
}