package test.doyaaaaaken.model

import doyaaaaaken.main.boot.Boot
import doyaaaaaken.main.boot.Property
import doyaaaaaken.model.Agent
import doyaaaaaken.model.AgentFactory
import doyaaaaaken.model.CompleteGraphFactory
import doyaaaaaken.model.Network
import doyaaaaaken.model.TraitFreqHistory
import doyaaaaaken.service.AgentImitationService
import doyaaaaaken.service.imitationStrategy.BothTraitExistConditionCopyStrategy
import doyaaaaaken.service.imitationStrategy.OnlyAgentPossessTraitCopyStrategy
import doyaaaaaken.service.imitationStrategy.YourAndMyTraitExistConditionCopyStrategy

object AgentImitationServiceTest {

  var currentTraitFreq: TraitFreqHistory = null //現タイムステップに存在する様式リストを保持する
  val agentImitationService: AgentImitationService = new AgentImitationService(YourAndMyTraitExistConditionCopyStrategy) //ここのアルゴリズムはテスト目的次第で適宜切り替える！！

  def main(args: Array[String]): Unit = {

    /*=================================================================================================
     * エージェント間での様式摸倣に関するテスト(用いるアルゴリズムは変数agentImitationServiceにて指定)
     ================================================================================================*/
    Boot.start(useDb = false)
    Property.setAntiConformThreshold(0.2) //anti-Conform Thresholdの値をここで設定できる

    //エージェント間の繋がりを示すネットワークの生成
    val network: Network = CompleteGraphFactory.create(Property.agentNum) //完全グラフ
    //エージェントの初期化
    val tmp = for (i <- 0 to Property.agentNum - 1) yield { (i, AgentFactory.create()) }
    val agents: Map[Int, Agent] = tmp.toMap //AgentNum体のエージェントセット

    //現在存在する様式リストの作成
    currentTraitFreq = TraitFreqHistory.apply(0, 0, agents)

    //////////////ここまで本コードのMainクラスでの初期化処理と同じ///////////////////
    //////////////ここからテストコード///////////////////
    println("＊＊＊＊ついでに社会全体での様式の採用度数も出力する＊＊＊＊＊＊")
    currentTraitFreq.debugPrint

    println("＊＊＊＊摸倣が行われていることを目視確認によりテスト＊＊＊＊")
    //摸倣前
    println("\n＊＊＊摸倣前＊＊＊")
    agents.foreach(_._2.debugPrint)

    //摸倣を行う
    agentImitationService.work(agents, network, currentTraitFreq)

    //摸倣後
    println("\n＊＊＊摸倣後＊＊＊")
    agents.foreach(_._2.debugPrint)

    ////////////ここから、Anti-Conform処理が正しく行われているかどうかをチェックする////////

    println("\n＊＊＊Anti-Conform処理のテストを行う＊＊＊")

    println("＊＊＊＊updateMemoryメソッドのテスト＊＊＊＊＊＊")
    agents.foreach(_._2.updateMemory(Seq(1, 2, 3, 4, 5))) //ダミーの様式群を見せ、記憶を更新する
    agents.foreach(_._2.debugPrint)

    println("＊＊＊＊＊calcDiffusionメソッドのテスト(副作用なし)＊＊＊＊＊")
    agents.foreach(aMap => println("ID:" + aMap._2.id + "  " + aMap._2.calcDiffusion(1)))
    println("＊＊＊＊＊isDiffrentiateメソッドのテスト(副作用なし)＊＊＊＊＊")
    agents.foreach(aMap => println("ID:" + aMap._2.id + "  " + aMap._2.isDifferentiate(1)))

    println("＊＊＊＊＊actDifferentiationメソッドのテスト＊＊＊＊＊")
    agents.foreach(_._2.actDifferentiation(1, Some(11)))
    agents.foreach(_._2.debugPrint)

  }
}
