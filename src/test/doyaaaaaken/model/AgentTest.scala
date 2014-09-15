package test.doyaaaaaken.model

import doyaaaaaken.main.boot.Boot
import doyaaaaaken.main.properties.Property
import doyaaaaaken.model.AgentFactory
import doyaaaaaken.model.Agent
import doyaaaaaken.model.helper.TraitFactory

object AgentTest {

  def main(args: Array[String]): Unit = {

    /*============================
     * エージェント初期化テスト
     ============================*/

    Boot.start //シミュレーション開始時に必要な処理

    //エージェントの初期化
    val agentsIndexedSeq = for (i <- 1 to Property.agentNum) yield { AgentFactory.create() }
    val agents: Set[Agent] = agentsIndexedSeq.toSet //AgentNum体のエージェントセット

    //////////////ここまで本コードのMainクラスでの初期化処理と同じ///////////////////
    //////////////ここからテストコード///////////////////

    println("****以下のAgentの持つTrait群が0～initialTraitKind-1となることを目視確認する****")
    agents.foreach(a => println(a.traits))
    println()

  }

}