package doyaaaaaken.main.properties

import scala.io.Source
import scala.util.matching.Regex
import scala.util.matching.Regex.Match

/**
 * Property���ɃA�N�Z�X���������́Aobject�̒��̃L�[���ꗗ����Aprops.get("�L�[��")�ŃA�N�Z�X�ł���
 */
class Property(keyValueStore: Map[String, String]) {
  //�A�N�Z�X���̃L�[���ꗗ
  val simNum = keyValueStore.get("sim.num").get
  val agentNum = keyValueStore.get("agent.num").get
}

object Property {
  def apply(keyValueMap: Map[String, String]) = {
    new Property(keyValueMap)
  }
}

/**
 * �ݒ�t�@�C���̓ǂݍ��݁��l���}�b�v�֊i�[
 */
object PropertyReader {

  def read(): Property = {
    val source = Source.fromFile("./resource/prop.txt")
    var sourceStr: String = ""
    try {
      sourceStr = source.getLines.toList.mkString("")
    } finally {
      source.close()
    }

    val pattern: Regex = new Regex(""" *([.A-Za-z0-9]*) *= *([^;]+); *""", "key", "value")
    val keyValueMap = pattern.findAllIn(sourceStr).matchData.map { m: Match =>
      (m.group("key"), m.group("value"))
    }.toMap

    Property(keyValueMap)
  }
}