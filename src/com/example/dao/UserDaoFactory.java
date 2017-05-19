package com.example.dao;

public class UserDaoFactory {
	private static UserDao dao;

	//获得UserDao的实例
	public static UserDao getInstance() {
		if (dao == null) {
			dao = new UserDaoProcess();
		}
		return dao;
	}
}