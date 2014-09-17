package doyaaaaaken.model

import scala.util.Random
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

/**
 * 各ターンにおける様式の度数を表すクラス
 *
 * 注：毎ターンの終わりに呼ばれる
 */
class TraitFreqHistory(timeStep: Int, currentTraitList: Seq[Int]) {

  /**現存する様式番号のうちどれか1つをランダムに返す*/
  def getRandomTraitNum: Int = {
    currentTraitList(new Random().nextInt(currentTraitList.size))
  }

  /**
   * 現在時刻の様式の度数をDBに格納する
   */
  def insertDataSet(timeStep: Int, con: Connection): Unit = {
    try {
      val stmt: Statement = con.createStatement(); // ステートメント生成
      //TODO 内容をちゃんとしたものに直す
      val sqlStr: String = "SELECT * FROM test_table"; // SQLを実行
      val rs: ResultSet = stmt.executeQuery(sqlStr);

      while (rs.next()) { // 結果行をループ
        val id: Int = rs.getInt("id");
        val name: String = rs.getString("name");
        val flag: Boolean = rs.getBoolean("flag");
        println(id + "：" + name + " : " + flag);
      }

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
    println("タイムステップ：" + timeStep + "  存在する様式リスト：" + currentTraitList)
    println("++++++++++++++++++++++++++++++++++")
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