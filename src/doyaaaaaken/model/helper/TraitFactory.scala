package doyaaaaaken.model.helper

import doyaaaaaken.main.boot.Property

/**
 * AgentFactory内部にて初期化され、各Agentインスタンスに参照として保持され利用される。
 *
 * つまりTraitFactoryはAgentクラスからしか利用されない
 */
private[model] class TraitFactory(initialTraitKindNum: Int) {

  val initialTraitKind = initialTraitKindNum //初期の様式種類数
  var latestTraitKind = initialTraitKind - 1 //現在の最新の様式種類番号

  /**
   * エージェントの初期様式(0～initialTraitKind-1のどれか)を作成する
   */
  def getInitialTrait(): Seq[Int] = {
    (0 to initialTraitKind - 1).toList.filter(_ => Math.random < Property.initialHavingTraitProp)
  }

  /**
   * エージェントが新規様式を生みだした際に、その様式番号を返す
   */
  def getNewTrait(): Int = {
    latestTraitKind = latestTraitKind + 1
    latestTraitKind
  }

}

/**
 * 初期の様式種類数を指定して、TraitFactoryを1つだけ生成する
 */
object TraitFactory {

  val limitInstanceNum = Property.simNum //生成できるTraitFactoryはシミュレーション回数個分だけにする
  var instanceCount = 0

  def apply(): TraitFactory = {
    if (instanceCount >= limitInstanceNum) throw new RuntimeException("TraitFactoryはシミュレーション回数以下個しか生成できません")
    new TraitFactory(Property.initialTraitKind)
  }
}