package test.doyaaaaaken.model

import doyaaaaaken.main.boot.Boot
import doyaaaaaken.model.Network
import doyaaaaaken.model.AgentFactory
import doyaaaaaken.model.CompleteGraphFactory
import doyaaaaaken.model.Agent
import doyaaaaaken.main.boot.Property
import doyaaaaaken.model.TraitFreqHistory

object AgentMutationServiceTest {

  var currentTraitFreq: TraitFreqHistory = null //現タイムステップに存在する様式リストを保持する

  def main(args: Array[String]): Unit = {

    /*==========================================
     * エージェントの突然変異に関するテスト
     ===========================================*/
    Boot.start(useDb = false)

    //エージェント間の繋がりを示すネットワークの生成
    val network: Network = CompleteGraphFactory.create(Property.agentNum) //完全グラフ
    //エージェントの初期化
    val tmp = for (i <- 0 to Property.agentNum - 1) yield { (i, AgentFactory.create()) }
    val agents: Map[Int, Agent] = tmp.toMap //AgentNum体のエージェントセット

    //現在存在する様式リストの作成
    currentTraitFreq = TraitFreqHistory.apply(0, agents)

    //////////////ここまで本コードのMainクラスでの初期化処理と同じ///////////////////
    //////////////ここからテストコード///////////////////

    //好みがランダムに振り直されていることを目視で確認する
    println("＊＊＊＊＊＊好みのランダムな振り直しのテスト＊＊＊＊＊＊")
    println("++++++++++振り直し前++++++++++")
    agents.foreach(aMap => println(aMap._2.preference.getPreference))
    agents.foreach(aMap => aMap._2.randomizePreference) //ランダマイズした
    println("++++++++++振り直し後++++++++++")
    agents.foreach(aMap => println(aMap._2.preference.getPreference))

  }

}