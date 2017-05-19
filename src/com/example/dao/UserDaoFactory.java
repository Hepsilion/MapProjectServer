package com.example.dao;

public class UserDaoFactory {
	private static UserDao dao;

	//���UserDao��ʵ��
	public static UserDao getInstance() {
		if (dao == null) {
			dao = new UserDaoProcess();
		}
		return dao;
	}
}