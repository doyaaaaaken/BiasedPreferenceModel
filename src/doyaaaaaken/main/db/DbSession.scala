package doyaaaaaken.main.db

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import doyaaaaaken.main.boot.Property
import java.sql.SQLException

object DbSession {

  private[this] var con: Connection = null

  private[main] def open = {
    val msg: String = ""
    try {
      Class.forName("org.gjt.mm.mysql.Driver") // ドライバロード
      con = DriverManager.getConnection("jdbc:mysql://" + Property.dbHostName + "/" + Property.dbName, Property.dbId, Property.dbPass); // MySQLに接続
      // ～使い方～
      //      try {
      //        // ステートメント生成
      //        val stmt: Statement = con.createStatement();
      //
      //        // SQLを実行
      //        val sqlStr: String = "SELECT * FROM test_table";
      //        val rs: ResultSet = stmt.executeQuery(sqlStr);
      //
      //        // 結果行をループ
      //        while (rs.next()) {
      //          // レコードの値
      //          val id: Int = rs.getInt("id");
      //          val name: String = rs.getString("name");
      //          val flag: Boolean = rs.getBoolean("flag");
      //
      //          println(id + "：" + name + " : " + flag);
      //        }
      //        rs.close();
      //        stmt.close();
      //      }
    } catch {
      case e: SQLException => println("Database error " + e)
      case e => {
        println("Some other exception type on DbSession:")
        e.printStackTrace()
      }
    }
  }

  private[main] def close = {
    try {
      if (con != null) con.close();
    } catch {
      case e => {
        println("Some other exception type on DbSession:")
        e.printStackTrace()
      }
    }
  }
}
