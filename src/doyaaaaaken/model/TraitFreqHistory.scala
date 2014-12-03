package doyaaaaaken.model

import scala.util.Random
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import doyaaaaaken.main.boot.Property
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{ Map => MutableMap }

/**
 * 各ターンにおける様式の度数を表すクラス
 *
 * 注：毎ターンの終わりに呼ばれる
 */
class TraitFreqHistory(simNum: Int, timeStep: Int, currentTraitMap: Map[Int, Int]) {

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

  /**引数で指定された様式が、現存するか否かを返す*/
  def contains(traitKind: Int): Boolean = {
    currentTraitMap.keySet.contains(traitKind)
  }

  /**
   * 現在時刻の様式の度数をDBに格納する
   */
  def insertDataSet(con: Connection): Unit = {
    try {
      val sqlTemplate = "INSERT INTO " + Property.dbName + "." + Property.traitFreqHistoryTableName + " (sim_num, timestep, trait_kind, freq) VALUES (?, ?, ?, ?);"
      val ps = con.prepareStatement(sqlTemplate) //プリペアドステートメントを生成

      ps.setInt(1, simNum)
      ps.setInt(2, timeStep)
      currentTraitMap.foreach {
        case (traitKind, freq) => {
          ps.setInt(3, traitKind)
          ps.setInt(4, freq)
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

  lazy val dbTableName: String = Property.dbName + "." + Property.traitFreqHistoryTableName //オブジェクトに対応するDBテーブル名

  /**ファクトリが呼ばれたら、各エージェントにアクセスして様式の度数を集計する*/
  def apply(simNum: Int, timeStep: Int, agents: Map[Int, Agent]): TraitFreqHistory = {
    //agentから「様式種類 - 度数」の一覧を取得
    val currentTraitMap: Map[Int, Int] = agents.flatMap(_._2.traits).toList.groupBy(x => x).map(x => (x._1, x._2.size))
    new TraitFreqHistory(simNum, timeStep, currentTraitMap)
  }

  /**
   * trait_freq_historyテーブルの全値を取得する
   */
  def selectAllData(con: Connection): Seq[TraitFreqHistoryDataRow] = {

    val datas: ListBuffer[TraitFreqHistoryDataRow] = ListBuffer()
    try {
      val stmt: Statement = con.createStatement
      val sql: String = "SELECT * FROM " + dbTableName + " ORDER BY id ASC, timestep ASC"
      val rs: ResultSet = stmt.executeQuery(sql)

      while (rs.next()) {
        val id: Int = rs.getInt("id")
        val simNum: Int = rs.getInt("sim_num")
        val timeStep: Int = rs.getInt("timestep")
        val traitKind: Int = rs.getInt("trait_kind")
        val freq: Int = rs.getInt("freq")
        datas += TraitFreqHistoryDataRow(id, simNum, timeStep, traitKind, freq)
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
   * 指定のsim_num, trait_kindのデータを取得する
   */
  def findBySimNumAndTraitKind(con: Connection, simNum: Int, traitKind: Int): Seq[TraitFreqHistoryDataRow] = {

    val datas: ListBuffer[TraitFreqHistoryDataRow] = ListBuffer()
    try {
      val stmt: Statement = con.createStatement
      val sql: String =
        "SELECT * FROM %s WHERE sim_num=%d AND trait_kind=%d ORDER BY id ASC, sim_num ASC, trait_kind ASC, timestep ASC"
          .format(dbTableName, simNum, traitKind)
      val rs: ResultSet = stmt.executeQuery(sql)

      while (rs.next()) {
        val id: Int = rs.getInt("id")
        val simNum: Int = rs.getInt("sim_num")
        val timeStep: Int = rs.getInt("timestep")
        val traitKind: Int = rs.getInt("trait_kind")
        val freq: Int = rs.getInt("freq")
        datas += TraitFreqHistoryDataRow(id, simNum, timeStep, traitKind, freq)
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
   * 様式度数最大値TopNの様式の度数推移だけをtrait_freq_historyテーブルから抜き出す
   */
  //  def selectTopNTraitsData(con: Connection, simNumber: Int): Seq[TraitFreqHistoryDataRow] = {
  //    val datas: ListBuffer[TraitFreqHistoryDataRow] = ListBuffer()
  //    try {
  //      val stmt: Statement = con.createStatement
  //      val sql: StringBuilder = new StringBuilder
  //      sql.append("SELECT * FROM ")
  //      sql.append(dbTableName)
  //      sql.append(" AS tfh1 WHERE tfh1.sim_num = ")
  //      sql.append(simNumber)
  //      sql.append(" AND tfh1.trait_kind IN (SELECT tfh2.trait_kind FROM (SELECT tfh3.trait_kind FROM ")
  //      sql.append(dbTableName)
  //      sql.append(" AS tfh3 WHERE tfh3.sim_num = ")
  //      sql.append(simNumber)
  //      sql.append(" GROUP BY tfh3.trait_kind ORDER BY MAX(tfh3.freq) DESC LIMIT ")
  //      sql.append(Property.csvOutputTopNNum)
  //      sql.append(") AS tfh2);")
  //
  //      val rs: ResultSet = stmt.executeQuery(sql.toString())
  //
  //      while (rs.next()) {
  //        val id: Int = rs.getInt("id")
  //        val simNum: Int = rs.getInt("sim_num")
  //        val timeStep: Int = rs.getInt("timestep")
  //        val traitKind: Int = rs.getInt("trait_kind")
  //        val freq: Int = rs.getInt("freq")
  //        datas += TraitFreqHistoryDataRow(id, simNum, timeStep, traitKind, freq)
  //      }
  //      rs.close
  //      stmt.close
  //    } catch {
  //      case e: SQLException => println("Database error " + e)
  //      case e: Throwable => {
  //        println("Some other exception type on DbSession:")
  //        e.printStackTrace
  //      }
  //    }
  //    datas.toList
  //  }
  /**
   * 累積採用度数TopNの様式の度数推移だけをtrait_freq_historyテーブルから抜き出す
   */
  def selectCumulativeFreqTopNTraitsData(con: Connection, simNumber: Int, rankLimit: Int): Seq[TraitFreqHistoryDataRow] = {
    val datas: ListBuffer[TraitFreqHistoryDataRow] = ListBuffer()
    try {
      val stmt: Statement = con.createStatement
      val sql: StringBuilder = new StringBuilder

      sql.append("SELECT * FROM ")
      sql.append(dbTableName)
      sql.append(" AS tfh1 WHERE tfh1.sim_num = ")
      sql.append(simNumber)
      sql.append(" AND tfh1.trait_kind IN (SELECT tfh2.trait_kind FROM (SELECT tfh3.trait_kind FROM ")
      sql.append(dbTableName)
      sql.append(" AS tfh3 WHERE tfh3.sim_num = ")
      sql.append(simNumber)
      sql.append(" GROUP BY tfh3.trait_kind ORDER BY SUM(tfh3.freq) DESC LIMIT ")
      sql.append(rankLimit)
      sql.append(") AS tfh2);")

      val rs: ResultSet = stmt.executeQuery(sql.toString())

      while (rs.next()) {
        val id: Int = rs.getInt("id")
        val simNum: Int = rs.getInt("sim_num")
        val timeStep: Int = rs.getInt("timestep")
        val traitKind: Int = rs.getInt("trait_kind")
        val freq: Int = rs.getInt("freq")
        datas += TraitFreqHistoryDataRow(id, simNum, timeStep, traitKind, freq)
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

  /**
   * 様式寿命最長値TopNの様式の度数推移だけをtrait_freq_historyテーブルから抜き出す
   */
  def selectLifeSpanTopNTraitsData(con: Connection, simNumber: Int, rankLimit: Int): Seq[TraitFreqHistoryDataRow] = {
    val datas: ListBuffer[TraitFreqHistoryDataRow] = ListBuffer()
    try {
      val stmt: Statement = con.createStatement
      val sql: StringBuilder = new StringBuilder

      sql.append("SELECT * FROM ")
      sql.append(dbTableName)
      sql.append(" AS tfh1 WHERE tfh1.sim_num = ")
      sql.append(simNumber)
      sql.append(" AND tfh1.trait_kind IN (SELECT tfh2.trait_kind FROM (SELECT tfh3.trait_kind FROM ")
      sql.append(dbTableName)
      sql.append(" AS tfh3 WHERE tfh3.sim_num = ")
      sql.append(simNumber)
      sql.append(" GROUP BY tfh3.trait_kind ORDER BY COUNT(tfh3.freq) DESC LIMIT ")
      sql.append(rankLimit)
      sql.append(") AS tfh2);")

      val rs: ResultSet = stmt.executeQuery(sql.toString())

      while (rs.next()) {
        val id: Int = rs.getInt("id")
        val simNum: Int = rs.getInt("sim_num")
        val timeStep: Int = rs.getInt("timestep")
        val traitKind: Int = rs.getInt("trait_kind")
        val freq: Int = rs.getInt("freq")
        datas += TraitFreqHistoryDataRow(id, simNum, timeStep, traitKind, freq)
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
  //////////////////////////////////ここからが、hopedTraitSimulationブランチで使う部分//////////////////////////////////////////
  /**
   * 各シミュレーションでの最高到達度数TopNの様式について、最高到達度数の平均値をtrait_freq_historyテーブルから取得する
   */
  def selectMaxFreqAveForTopNTraits(con: Connection, rankLimit: Option[Int], simNum: Int): Seq[TraitFeatureValueDataRow] = {
    val datas: ListBuffer[TraitFeatureValueDataRow] = ListBuffer()
    try {
      val stmt: Statement = con.createStatement
      val sql: String = """
							SELECT
                	groups.is_hoped,
                	AVG(groups.max_freq) AS max_freq_average
                FROM (SELECT
                	trait_kind,
                	CASE WHEN tfh.trait_kind %% %d = 0 THEN "true" ELSE "false" END is_hoped,
                	MAX(tfh.freq) AS max_freq
                      FROM %s AS tfh
                	WHERE sim_num = %d
                	GROUP BY tfh.trait_kind
								  ORDER BY max_freq DESC %s) AS groups
                GROUP BY groups.is_hoped;"""
        .format(Property.hopedTraitGenerateInterval, dbTableName, simNum, if (rankLimit.isDefined) "LIMIT " + rankLimit.get else "")

      val rs: ResultSet = stmt.executeQuery(sql)

      while (rs.next()) {
        val isHoped: Boolean = rs.getBoolean("is_hoped")
        val maxFreqAverage: Double = rs.getDouble("max_freq_average")
        datas += TraitFeatureValueDataRow(simNum, isHoped, maxFreqAverage)
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

  /**
   * 各シミュレーションでの累積採用度数TopNの様式について、累積採用度数の平均値をtrait_freq_historyテーブルから取得する
   */
  def selectCumulativeFreqAveForTopNTraits(con: Connection, rankLimit: Option[Int], simNum: Int): Seq[TraitFeatureValueDataRow] = {
    val datas: ListBuffer[TraitFeatureValueDataRow] = ListBuffer()
    try {
      val stmt: Statement = con.createStatement
      val sql: String = """
							SELECT
                	groups.is_hoped,
                	AVG(groups.cumulative_freq) AS cumulative_freq_average
                FROM (SELECT
                	trait_kind,
                	CASE WHEN tfh.trait_kind %% %d = 0 THEN "true" ELSE "false" END is_hoped,
                	SUM(tfh.freq) AS cumulative_freq
                      FROM %s AS tfh
                	WHERE sim_num = %d
                	GROUP BY tfh.trait_kind
								  ORDER BY cumulative_freq DESC %s) AS groups
                GROUP BY groups.is_hoped;"""
        .format(Property.hopedTraitGenerateInterval, dbTableName, simNum, if (rankLimit.isDefined) "LIMIT " + rankLimit.get else "")

      val rs: ResultSet = stmt.executeQuery(sql.toString())

      while (rs.next()) {
        val isHoped: Boolean = rs.getBoolean("is_hoped")
        val cumulativeFreqAverage: Double = rs.getDouble("cumulative_freq_average")
        datas += TraitFeatureValueDataRow(simNum, isHoped, cumulativeFreqAverage)
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

  /**
   * 各シミュレーションでの様式寿命TopNの様式について、様式寿命の平均値をtrait_freq_historyテーブルから取得する
   */
  def selectLifeSpanAveForTopNTraits(con: Connection, rankLimit: Option[Int], simNum: Int): Seq[TraitFeatureValueDataRow] = {
    val datas: ListBuffer[TraitFeatureValueDataRow] = ListBuffer()
    try {
      val stmt: Statement = con.createStatement
      val sql: String = """
							SELECT
                	groups.is_hoped,
                	AVG(groups.life_span) AS life_span_average
                FROM (SELECT
                	trait_kind,
                	CASE WHEN tfh.trait_kind %% %d = 0 THEN "true" ELSE "false" END is_hoped,
                	COUNT(tfh.freq) AS life_span
                      FROM %s AS tfh
                	WHERE sim_num = %d
                	GROUP BY tfh.trait_kind
								  ORDER BY life_span DESC %s) AS groups
                GROUP BY groups.is_hoped;"""
        .format(Property.hopedTraitGenerateInterval, dbTableName, simNum, if (rankLimit.isDefined) "LIMIT " + rankLimit.get else "")

      val rs: ResultSet = stmt.executeQuery(sql.toString())

      while (rs.next()) {
        val isHoped: Boolean = rs.getBoolean("is_hoped")
        val lifeSpanAverage: Double = rs.getDouble("life_span_average")
        datas += TraitFeatureValueDataRow(simNum, isHoped, lifeSpanAverage)
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

  /**trait_freq_historyテーブルからSELECTし加工した、traitLifeSpanに関する行を格納するDTO*/
  case class TraitFeatureValueDataRow(simNum: Int, isHoped: Boolean, average: Double)

  //////////////////////////////////ここまでが、hopedTraitSimulationブランチで使う部分//////////////////////////////////////////

  /**
   * 様式の寿命の分布を抜き出すメソッド
   * @return キー:様式番号 値:寿命 のMap
   */
  def getTraitLifeSpanFreq(con: Connection): Seq[TraitLifeSpanDataRow] = {
    val datas: ListBuffer[TraitLifeSpanDataRow] = ListBuffer()
    try {
      val stmt: Statement = con.createStatement
      val sql: String = "SELECT sim_num, trait_kind, count(*) AS life_span FROM " + dbTableName + " GROUP BY sim_num, trait_kind;"

      val rs: ResultSet = stmt.executeQuery(sql.toString())
      while (rs.next()) {
        val simNum: Int = rs.getInt("sim_num")
        val traitKind: Int = rs.getInt("trait_kind")
        val lifeSpan: Int = rs.getInt("life_span")
        datas += TraitLifeSpanDataRow(simNum, traitKind, lifeSpan)
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
case class TraitFreqHistoryDataRow(id: Int, simNum: Int, timestep: Int, trait_kind: Int, freq: Int)

/**trait_freq_historyテーブルからSELECTし加工した、traitLifeSpanに関する行を格納するDTO*/
case class TraitLifeSpanDataRow(simNum: Int, traitKind: Int, lifeSpan: Int)