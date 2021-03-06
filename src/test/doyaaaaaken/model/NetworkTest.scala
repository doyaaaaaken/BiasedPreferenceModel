package test.doyaaaaaken.model

import doyaaaaaken.model.CompleteGraphFactory
import doyaaaaaken.model.Network

object NetworkTest {

  def main(args: Array[String]): Unit = {

    /*=================================
     * 完全グラフネットワークのテスト
     ==================================*/
    val cNet: Network = CompleteGraphFactory.create(10) //エージェント数10の完全グラフ

    //isLinkedメソッドのテスト
    assert(cNet.isLinked(0, 1), "完全グラフなのでエージェント0と1はリンクで繋がれているはずです")
    assert(cNet.isLinked(9, 0), "完全グラフなのでエージェント9と0はリンクで繋がれているはずです")
    try {
      assert(!cNet.isLinked(9, 9)) //同じ引数なので接続未接続がわからないのでエラー
    } catch {
      case e: RuntimeException =>
    }
    try {
      assert(!cNet.isLinked(0, 10)) //エージェントナンバー10は存在しないのでエラー
    } catch {
      case e: IndexOutOfBoundsException =>
    }

    //getLinkedAgentNumsメソッドのテスト
    assert(cNet.getLinkedAgentNums(0) == Seq(1, 2, 3, 4, 5, 6, 7, 8, 9), "完全グラフなので自身以外のエージェントとリンクが繋がっている必要があります")
    assert(cNet.getLinkedAgentNums(5) == Seq(0, 1, 2, 3, 4, 6, 7, 8, 9), "完全グラフなので自身以外のエージェントとリンクが繋がっている必要があります")
    assert(cNet.getLinkedAgentNums(9) == Seq(0, 1, 2, 3, 4, 5, 6, 7, 8), "完全グラフなので自身以外のエージェントとリンクが繋がっている必要があります")
    try {
      assert(cNet.getLinkedAgentNums(10) == Seq(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)) //エージェントナンバー10は存在しないのでエラー
    } catch {
      case e: IndexOutOfBoundsException =>
    }

    println("テスト成功")

    /*=========================
     * WSネットワークのテスト
     ==========================*/
    //    val wsNet: Network = SmallWorldGraphFactory.create(30) //エージェント数30の完全グラフ

  }
}