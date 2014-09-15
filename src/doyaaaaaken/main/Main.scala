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
    println("＊＊＊＊＊＊シミュレーション開始＊＊＊＊＊＊")
    val prop: Property = Boot.start
    println(prop.simNum)

    //エージェント間の繋がりを示すネットワークの生成
    val network: Network = CompleteGraphFactory.create(1000) //完全グラフ  //TODO プロパティ化
    //エージェントの初期化
    val agentsIndexedSeq = for (i <- 1 to 1000) yield { AgentFactory.create() } //TODO プロパティ化
    val agents: Set[Agent] = agentsIndexedSeq.toSet //AgentNum体のエージェントセット
  }
}