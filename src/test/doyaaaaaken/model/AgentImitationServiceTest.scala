package test.doyaaaaaken.model

import doyaaaaaken.main.boot.Boot
import doyaaaaaken.model.Network
import doyaaaaaken.model.CompleteGraphFactory
import doyaaaaaken.model.Agent
import doyaaaaaken.model.AgentFactory
import doyaaaaaken.model.TraitFreqHistory
import doyaaaaaken.service.AgentImitationService
import doyaaaaaken.main.boot.Property

object AgentImitationServiceTest {

  var currentTraitFreq: TraitFreqHistory = null //現タイムステップに存在する様式リストを保持する

  def main(args: Array[String]): Unit = {

    /*==========================================
     * エージェント間での様式摸倣に関するテスト
     ===========================================*/
    Boot.start

    //エージェント間の繋がりを示すネットワークの生成
    val network: Network = CompleteGraphFactory.create(Property.agentNum) //完全グラフ
    //エージェントの初期化
    val tmp = for (i <- 0 to Property.agentNum - 1) yield { (i, AgentFactory.create()) }
    val agents: Map[Int, Agent] = tmp.toMap //AgentNum体のエージェントセット

    //現在存在する様式リストの作成
    currentTraitFreq = TraitFreqHistory.apply(0, agents)

    //////////////ここまで本コードのMainクラスでの初期化処理と同じ///////////////////
    //////////////ここからテストコード///////////////////
    println("＊＊＊＊ついでに社会全体での様式の採用度数も出力する＊＊＊＊＊＊")
    currentTraitFreq.debugPrint

    println("＊＊＊＊摸倣が行われていることを目視確認によりテスト＊＊＊＊")
    //摸倣前
    println("\n＊＊＊摸倣前＊＊＊")
    agents.foreach(_._2.debugPrint)

    //摸倣を行う
    AgentImitationService.work(agents, network, currentTraitFreq)

    //摸倣後
    println("\n＊＊＊摸倣後＊＊＊")
    agents.foreach(_._2.debugPrint)

  }
}