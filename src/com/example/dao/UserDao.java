package com.example.dao;

import java.util.ArrayList;

import com.example.tran.TranObject;
import com.example.util.User;

//UserDao接口
public interface UserDao {
	//注册成功返回注册信息
	public boolean register(User u);
	//登录，登陆成功后返回用户好友列表
	public ArrayList<User> login(User u);
	//用户退出登录
	public void logout(String name);
	//获得好友列表
	public ArrayList<User> refresh(String name);
	//同意好友请求后，在各自好友的数据库中插入好友
	public void addFriend(String Ofname,String Addname);
}