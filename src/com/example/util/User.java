package com.example.util;

import java.io.Serializable;

/*
 * С���򣬾���������Ϊ����
 */
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;//�û���
	private String email;//email
	private String password;//����
	private int isOnline;// �Ƿ�����
	
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
