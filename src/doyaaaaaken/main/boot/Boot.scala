package doyaaaaaken.main.boot

import doyaaaaaken.main.properties.PropertyReader
import doyaaaaaken.main.properties.Property
/**
 * �V�~�����[�V�����̐ߖڂŕK�v�ȏ���
 */
object Boot {

  /**
   * �V�~�����[�V�����J�n���ɕK�v�ȏ���
   * ���F�����_�ł�Property�t�@�C���݂̂�Ԃ��ݒ�ɂ��Ă���
   */
  def start(): Property = {
    PropertyReader.read
  }
}