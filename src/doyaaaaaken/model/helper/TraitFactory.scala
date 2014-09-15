package doyaaaaaken.model.helper

private[model] class TraitFactory(initialTraitKindNum: Int) {

  val initialTraitKind = initialTraitKindNum //初期の様式種類数
  var latestTraitKind = initialTraitKind //最新の様式種類番号

  /**
   * エージェントの初期様式を作成する
   */
  def getInitialTrait(): Seq[Int] = {

    null
  }

  /**
   * エージェントが新規様式を生みだした際に、その様式番号を返す
   */
  def getNewTrait(): Int = {
    latestTraitKind + 1
  }

}

//初期の様式種類数を指定して、TraitFactoryを生成する
object TraitFactoryInit {
  def apply(initialTraitKind: Int): TraitFactory = {
    new TraitFactory(initialTraitKind)
  }
}