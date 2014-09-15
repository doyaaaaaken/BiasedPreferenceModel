package doyaaaaaken.model

import org.omg.SendingContext.RunTime

/**
 * 基本的には無向固定グラフ。2次元配列edgeを持ち、これによってエージェント同士でリンクが結ばれているかチェックする
 */
class Network(edgeList: List[List[Boolean]]) {
  val edge = edgeList

  /**
   * 引数に指定した2Agent間でリンクが貼られているかどうかを返す
   * 注；Agent数は0～AgentNum-1で指定しているとする
   */
  def isLinked(agent1: Int, agent2: Int): Boolean = {
    if (agent1 == agent2) throw new RuntimeException("引数が同じになっているため不正です")
    else edge.apply(agent1).apply(agent2)
  }

  /**
   * 指定したAgentとリンクが繋がっているAgentナンバーのリストを返す
   */
  def getLinkedAgentNums(agentNum: Int): Seq[Int] = {
    //TODO
  }
}

/*完全グラフのファクトリ*/
object CompleteGraphFactory {
  def create(agentNum: Int): Network = {
    //1~AgentNumまでの全値がtrue(リンクあり)である1行を定義
    var edgeRow: List[Boolean] = Nil
    for (i <- 1 to agentNum) {
      edgeRow :+ true
    }
    //1行を集合させ、全値がtrue（リンクあり）の行列を作成する
    var edge: List[List[Boolean]] = Nil
    for (i <- 1 to agentNum) {
      edge :+ edgeRow
    }

    new Network(edge)
  }
}

/*WSグラフのファクトリ*/
object SmallWorldGraphFactory {
  def create(agentNum: Int): Network = {
    //TODO
  }
}