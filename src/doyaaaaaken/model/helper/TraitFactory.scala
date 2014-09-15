package doyaaaaaken.model.helper

import doyaaaaaken.main.properties.Property

/**
 * AgentFactory内部にて初期化され、各Agentインスタンスに参照として保持され利用される。
 *
 * つまりTraitFactoryはAgentクラスからしか利用されない
 */
private[model] class TraitFactory(initialTraitKindNum: Int) {

  val initialTraitKind = initialTraitKindNum //初期の様式種類数
  var latestTraitKind = initialTraitKind //最新の様式種類番号

  /**
   * エージェントの初期様式を作成する
   */
  def getInitialTrait(): Seq[Int] = {
    (0 to initialTraitKind).toList.filter(_ => Math.random < Property.initialHavingTraitProp)
  }

  /**
   * エージェントが新規様式を生みだした際に、その様式番号を返す
   */
  def getNewTrait(): Int = {
    //TODO 未実装
    latestTraitKind + 1
  }

}

/**
 * 初期の様式種類数を指定して、TraitFactoryを1つだけ生成する
 */
object TraitFactory {

  val limitInstanceNum = 1 //生成できるTraitFactoryは1つだけにする
  var instanceCount = 0

  def apply(): TraitFactory = {
    if (instanceCount >= limitInstanceNum) throw new RuntimeException("TraitFactoryは1つしか生成できません")
    instanceCount += 1
    new TraitFactory(Property.initialTraitKind)
  }
}