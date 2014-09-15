package doyaaaaaken.main

import doyaaaaaken.main.boot.Boot
import doyaaaaaken.main.properties.Property
import doyaaaaaken.model.CompleteGraphFactory
import doyaaaaaken.model.Network
import doyaaaaaken.model.AgentFactory
import doyaaaaaken.model.Agent
import doyaaaaaken.service.AgentImitationService
/**
 * シミュレーションの本骨組みとなるMainクラス
 */
object Main {
  def main(args: Array[String]): Unit = {
    Boot.start //シミュレーション開始時に必要な処理

    //エージェント間の繋がりを示すネットワークの生成
    val network: Network = CompleteGraphFactory.create(Property.agentNum) //完全グラフ
    //エージェントの初期化
    val tmp = for (i <- 1 to Property.agentNum) yield { AgentFactory.create() }
    var agents: Set[Agent] = tmp.toSet //AgentNum体のエージェントセット

    //シミュレーションの実行
    for (i <- 1 to Property.simNum) {
      //模倣フェーズ・・・全エージェント、自分と繋がっている他のエージェントを模倣する(アシンクロナス)
      agents = AgentImitationService.work(agents, network)

      //TODO 突然変異フェーズ

      //TODO データの格納

    }

    Boot.finish
  }
}