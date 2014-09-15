package doyaaaaaken.main

import doyaaaaaken.main.boot.Boot
import doyaaaaaken.main.properties.Property
import doyaaaaaken.model.CompleteGraphFactory
import doyaaaaaken.model.Network
import doyaaaaaken.model.AgentFactory
import doyaaaaaken.model.Agent
/**
 * シミュレーションの本骨組みとなるMainクラス
 */
object Main {
  def main(args: Array[String]): Unit = {
    Boot.start //シミュレーション開始時に必要な処理

    //エージェント間の繋がりを示すネットワークの生成
    val network: Network = CompleteGraphFactory.create(Property.agentNum) //完全グラフ
    //エージェントの初期化
    val agentsIndexedSeq = for (i <- 1 to Property.agentNum) yield { AgentFactory.create() }
    val agents: Set[Agent] = agentsIndexedSeq.toSet //AgentNum体のエージェントセット
  }
}