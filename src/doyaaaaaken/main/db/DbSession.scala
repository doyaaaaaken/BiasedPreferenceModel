package doyaaaaaken.main.db

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import doyaaaaaken.main.boot.Property
import java.sql.SQLException

object DbSession {

  private[this] var con: Connection = null

  private[main] def open: Unit = {
    println("＊＊＊＊＊＊DB接続処理中...＊＊＊＊＊＊")
    val msg: String = ""
    try {
      Class.forName("org.gjt.mm.mysql.Driver") // ドライバロード
      con = DriverManager.getConnection("jdbc:mysql://" + Property.dbHostName + "/" + Property.dbName, Property.dbId, Property.dbPass); // MySQLに接続
    } catch {
      case e: SQLException => println("Database error " + e)
      case e: Throwable => {
        println("Some other exception type on DbSession:")
        e.printStackTrace()
      }
    }
    println("＊＊＊＊＊＊DB接続完了＊＊＊＊＊＊")
  }

  /**conのゲッター*/
  private[main] def getConnection: Connection = {
    con
  }

  /**シミュレーション前にDBのデータを全て消しておくメソッド*/
  private[main] def clearData: Unit = {
    println("＊＊＊＊＊＊DB全データ消去中...＊＊＊＊＊＊")
    val stmt: Statement = con.createStatement();
    val sql1: String = "TRUNCATE TABLE " + Property.dbName + "." + Property.traitFreqHistoryTableName + ";";
    val sql2: String = "TRUNCATE TABLE " + Property.dbName + "." + Property.preferenceHistoryForOneTraitTableName + ";";

    stmt.executeUpdate(sql1);
    stmt.executeUpdate(sql2);
    stmt.close();
    println("＊＊＊＊＊＊DB全データ消去完了＊＊＊＊＊＊")
  }

  private[main] def close: Unit = {
    println("＊＊＊＊＊＊DB接続切断中...＊＊＊＊＊＊")
    try {
      if (con != null) con.close();
    } catch {
      case e: Throwable => {
        println("Some other exception type on DbSession:")
        e.printStackTrace()
      }
    }
    println("＊＊＊＊＊＊DB接続切断完了＊＊＊＊＊＊")
  }
}
