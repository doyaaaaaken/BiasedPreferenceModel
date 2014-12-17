package doyaaaaaken.model.agent

import doyaaaaaken.model.helper.TraitFactory
import doyaaaaaken.main.boot.Property

/**
 * Extreme様式に対して熱狂的な信者となっているエージェント
 */
class FanAgent(agentId: Int, antiConformism: Double, traitFactory: TraitFactory)
  extends Agent(agentId: Int, antiConformism: Double, traitFactory: TraitFactory) {

  /**オブジェクトのAgentType（Normal,Fan,Critics）を返す*/
  override def getAgentType: Int = AgentType.FAN

  /**
   * 受け取った様式番号リストに対して、好みの総和Pomを返す
   */
  override def calcPom(copyTraitNums: Seq[Int]): Double = {
    preference.calcPrefSum(copyTraitNums, AgentType.FAN)
  }

  /**Extreme様式以外の好みの値をランダムに振り直す*/
  override def randomizePreference(): Unit = {
    preference.randomizePrefValueExceptExtremeTrait
  }

  /**指定の様式番号の様式に対する好みを変更する*/
  override def changePreference(traitNum: Int, prefValue: Double): Unit = {
    if (traitNum % Property.extremeTraitGenerateInterval == 0) return //Extreme様式だった場合は好みを変更しない
    preference.changePrefValue(traitNum, prefValue)
  }

  /**エージェントが転生する（Extreme様式以外の全ての様式を破棄して、現存する全様式に対する好みを1にする）*/
  override def reborn(currentTraitList: Seq[Int]): Unit = {
    traits = currentTraitList.filter(_ % Property.extremeTraitGenerateInterval == 0)
    preference.rebornAgent(currentTraitList, false)
  }
}