package doyaaaaaken.model

import scala.util.Random
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import doyaaaaaken.main.boot.Property
import scala.collection.mutable.ListBuffer

/**
 * 各ターンにおける様式の度数を表すクラス
 *
 * 注：毎ターンの終わりに呼ばれる
 */
class TraitFreqHistory(timeStep: Int, currentTraitMap: Map[Int, Int]) {

  /**現存する様式番号のうちどれか1つをランダムに返す*/
  def getRandomTraitNum: Int = {
    if (!currentTraitMap.isEmpty) {
      currentTraitMap.keys.toList(new Random().nextInt(currentTraitMap.size))
    } else {
      println("様式がなくなったためシミュレーション終了です")
      sys.exit(-1)
    }
  }

  /**現存する様式種類番号リストを得る*/
  def getCurrentTraitKindList: Seq[Int] = {
    currentTraitMap.keySet.toList
  }

  /**
   * 現在時刻の様式の度数をDBに格納する
   */
  def insertDataSet(timeStep: Int, con: Connection): Unit = {
    try {
      val sqlTemplate = "INSERT INTO " + Property.dbName + "." + Property.traitFreqHistoryTableName + " (timestep, trait_kind, freq) VALUES (?, ?, ?);"
      val ps = con.prepareStatement(sqlTemplate) //プリペアドステートメントを生成

      ps.setInt(1, timeStep)
      currentTraitMap.foreach {
        case (traitKind, freq) => {
          ps.setInt(2, traitKind)
          ps.setInt(3, freq)
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

  /**デバッグ用：保持する変数をコンソール出力する*/
  def debugPrint: Unit = {
    println("++++++++++++++++++++++++++++++++++")
    println("＜社会において存在する様式群＞")
    println("タイムステップ：" + timeStep + "  存在する様式リスト：" + currentTraitMap)
    println("++++++++++++++++++++++++++++++++++")
  }
}

object TraitFreqHistory {
  /**ファクトリが呼ばれたら、各エージェントにアクセスして様式の度数を集計する*/
  def apply(timeStep: Int, agents: Map[Int, Agent]): TraitFreqHistory = {
    //agentから「様式種類 - 度数」の一覧を取得
    val currentTraitMap: Map[Int, Int] = agents.flatMap(_._2.traits).toList.groupBy(x => x).map(x => (x._1, x._2.size))
    new TraitFreqHistory(timeStep, currentTraitMap)
  }

  /**
   * trait_freq_historyテーブルの全値を取得する
   */
  def selectAllData(con: Connection): Seq[TraitFreqHistoryDataRow] = {

    val datas: ListBuffer[TraitFreqHistoryDataRow] = ListBuffer()
    try {
      val stmt: Statement = con.createStatement
      val sql: String = "SELECT * FROM " + Property.dbName + "." + Property.traitFreqHistoryTableName + " ORDER BY id ASC, timestep ASC"
      val rs: ResultSet = stmt.executeQuery(sql)

      while (rs.next()) {
        val id: Int = rs.getInt("id")
        val timeStep: Int = rs.getInt("timestep")
        val traitKind: Int = rs.getInt("trait_kind")
        val freq: Int = rs.getInt("freq")
        datas += TraitFreqHistoryDataRow(id, timeStep, traitKind, freq)
      }

      rs.close
      stmt.close
    } catch {
      case e: SQLException => println("Database error " + e)
      case e: Throwable => {
        println("Some other exception type on DbSession:")
        e.printStackTrace
      }
    }
    datas
  }

  /**
   * TopNの様式の度数推移だけをtrait_freq_historyテーブルから抜き出す
   */
  def selectTopNTraitsData(con: Connection): Seq[TraitFreqHistoryDataRow] = {
    val datas: ListBuffer[TraitFreqHistoryDataRow] = ListBuffer()
    try {
      val stmt: Statement = con.createStatement
      val dbTableName: String = Property.dbName + "." + Property.traitFreqHistoryTableName
      val sql: StringBuilder = new StringBuilder
      sql.append("SELECT * FROM ")
      sql.append(dbTableName)
      sql.append(" AS tfh1 WHERE tfh1.trait_kind IN (SELECT tfh2.trait_kind FROM (SELECT tfh3.trait_kind FROM ")
      sql.append(dbTableName)
      sql.append(" AS tfh3 GROUP BY tfh3.trait_kind ORDER BY MAX(tfh3.freq) DESC LIMIT 40) AS tfh2);")

      val rs: ResultSet = stmt.executeQuery(sql.toString())

      while (rs.next()) {
        val id: Int = rs.getInt("id")
        val timeStep: Int = rs.getInt("timestep")
        val traitKind: Int = rs.getInt("trait_kind")
        val freq: Int = rs.getInt("freq")
        datas += TraitFreqHistoryDataRow(id, timeStep, traitKind, freq)
      }
      rs.close
      stmt.close
    } catch {
      case e: SQLException => println("Database error " + e)
      case e: Throwable => {
        println("Some other exception type on DbSession:")
        e.printStackTrace
      }
    }
    datas.toList
  }
}

/**trait_freq_historyテーブルからSELECTした1行を格納するDTO*/
case class TraitFreqHistoryDataRow(id: Int, timestep: Int, trait_kind: Int, freq: Int)