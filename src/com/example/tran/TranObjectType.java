package com.example.tran;

/**
 * �����������
 * @author wutingming
 *
 */
public enum TranObjectType {
	REGISTER, // ע��
	LOGIN, // �û���¼
	LOGOUT,//�û��ǳ�
	FRIEND_REQUEST, // ��������
	ANSWER_YES_FRIEND_REQUEST,//ͬ����Ӻ�������
	ANSWER_NO_FRIEND_REQUEST,//��ͬ����Ӻ�������
	LOCATION_SHARE, // λ�ù���
	ANSWER_YES_LOCATION_SHARE, // ͬ��λ�ù���
	ANSWER_NO_LOCATION_SHARE, // ��ͬ��λ�ù���
	FRIENDLOGIN, // ��������
	FRIENDLOGOUT, // ��������
	LOCATION, // λ����Ϣ
	MESSAGE, // �û�������Ϣ
	UNCONNECTED, // �޷�����
	FILE, // �����ļ�
	REFRESH, // ˢ��
}