package doyaaaaaken.model

import scala.util.Random

/**
 * 各ターンにおける様式の度数を表すクラス
 *
 * 注：毎ターンの終わりに呼ばれる
 */
class TraitFreqHistory(timeStep: Int, currentTraitList: Seq[Int]) {

  /**現存する様式番号のうちどれか1つをランダムに返す*/
  def getRandomTraitNum: Int = {
    currentTraitList(new Random(1234).nextInt(currentTraitList.size))
  }
}

object TraitFreqHistory {
  /**ファクトリが呼ばれたら、各エージェントにアクセスして様式の度数を集計する*/
  def apply(timeStep: Int, agents: Map[Int, Agent]): TraitFreqHistory = {
    //agentから度数一覧を取得
    val currentTraitList: Set[Int] = agents.flatMap(_._2.traits).toSet
    new TraitFreqHistory(timeStep, currentTraitList.toList)
  }
}