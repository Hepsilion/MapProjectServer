package com.example.util;

import java.io.Serializable;

/*
 * 小程序，就以姓名做为主键
 */
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;//用户名
	private String email;//email
	private String password;//密码
	private int isOnline;// 是否在线
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public int getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(int isOnline) {
		this.isOnline = isOnline;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof User) {
			User user = (User) o;
			if (user.getName() == name ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", email=" + email
				+ ", password=" + password + ", isOnline=" + isOnline
				+ "]";
	}

}
