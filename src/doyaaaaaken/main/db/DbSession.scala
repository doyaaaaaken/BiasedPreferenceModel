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
    } catch {
      case e: SQLException => println("Database error " + e)
      case e => {
        println("Some other exception type on DbSession:")
        e.printStackTrace()
      }
    }
  }

  /**conのゲッター*/
  private[main] def getConnection: Connection = {
    con
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
