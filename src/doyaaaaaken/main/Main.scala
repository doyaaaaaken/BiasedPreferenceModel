package doyaaaaaken.main

import doyaaaaaken.main.boot.Boot
import doyaaaaaken.main.properties.Property
/**
 * �V�~�����[�V�����̖{���g�݂ƂȂ�Main�N���X
 */
object Main {
  def main(args: Array[String]): Unit = {
    println("�������������V�~�����[�V�����J�n������������")
    val prop: Property = Boot.start
    println(prop.simNum)
  }
}