package doyaaaaaken.model

import scala.util.Random
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import doyaaaaaken.main.boot.Property

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

  /**
   * 現在時刻の様式の度数をDBに格納する
   */
  def insertDataSet(timeStep: Int, con: Connection): Unit = {
    try {
      val stmt: Statement = con.createStatement(); // ステートメント生成

      val sqls: Seq[String] = currentTraitMap.map {
        case (traitKind, freq) =>
          "INSERT INTO " + Property.dbName + "." + Property.traitFreqHistoryTableName +
            " (timestep, trait_kind, freq) VALUES (" + timeStep + "," + traitKind + "," + freq + ");"
      }.toList

      sqls.foreach(stmt.executeUpdate(_))
      stmt.close();
    } catch {
      case e: SQLException => println("Database error " + e)
      case e => {
        println("Some other exception type on DbSession:")
        e.printStackTrace()
      }
    }
  }

  /**
   * 現在時刻の様式の度数をDBに格納する
   */
  def selectDataSet(timeStep: Int, con: Connection): Unit = {
    try {
      val stmt: Statement = con.createStatement();
      val sql: String = "SELECT * FROM " + Property.dbName + "." + Property.traitFreqHistoryTableName;
      val rs: ResultSet = stmt.executeQuery(sql);

      while (rs.next()) {
        val id: Int = rs.getInt("id");
        val timeStep: Int = rs.getInt("timestep");
        val traitKind: Int = rs.getInt("trait_kind");
        val freq: Int = rs.getInt("freq");
        println(id + "：" + timeStep + " : " + traitKind + " : " + freq);
      }
      //TODO 取得したデータをコンソール出力するのではなく、CSVなどとして使えるように変数に格納する

      rs.close();
      stmt.close();
    } catch {
      case e: SQLException => println("Database error " + e)
      case e => {
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
}