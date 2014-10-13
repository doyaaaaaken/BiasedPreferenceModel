package doyaaaaaken.model

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import doyaaaaaken.main.boot.Property

/**
 * 各タイムステップにおける、特定のTrait番号に対して各エージェントが持つPreferenceの値群
 */
class PreferenceHistoryForOneTrait(timeStep: Int, traitKindNum: Int, preferenceOfAgent: Map[Int, Double]) {

  /**
   * 現在時刻の各エージェントが持つPreferenceの値をDBに格納する
   */
  def insertDataSet(timeStep: Int, con: Connection): Unit = {
    try {
      val sqlTemplate = "INSERT INTO " + Property.dbName + "." + Property.preferenceHistoryForOneTraitTableName + " (timestep, trait_kind, agent_id, preference) VALUES (?, ?, ?, ?);"
      val ps = con.prepareStatement(sqlTemplate) //プリペアドステートメントを生成
      ps.setInt(1, timeStep)
      ps.setInt(2, traitKindNum)
      preferenceOfAgent.foreach {
        case (agentId, preference) => {
          ps.setInt(3, agentId)
          ps.setDouble(4, preference)
          ps.executeUpdate()
        }
      }
      ps.close();
    } catch {
      case e: SQLException => println("Database error " + e)
      case e: Throwable => {
        println("Some other exception type on DbSession:")
        e.printStackTrace()
      }
    }
  }
}

object PreferenceHistoryForOneTrait {

  def apply(timeStep: Int, traitKindNum: Int, agents: Map[Int, Agent]): PreferenceHistoryForOneTrait = {
    val preferenceOfAgent = agents.map(agentMap => (agentMap._1, agentMap._2.preference.getPreferenceValue(traitKindNum)))
    new PreferenceHistoryForOneTrait(timeStep, traitKindNum, preferenceOfAgent)
  }
}