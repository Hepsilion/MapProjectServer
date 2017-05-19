package com.example.dao;

import java.util.ArrayList;

import com.example.tran.TranObject;
import com.example.util.User;

//UserDao�ӿ�
public interface UserDao {
	//ע��ɹ�����ע����Ϣ
	public boolean register(User u);
	//��¼����½�ɹ��󷵻��û������б�
	public ArrayList<User> login(User u);
	//�û��˳���¼
	public void logout(String name);
	//��ú����б�
	public ArrayList<User> refresh(String name);
	//ͬ�����������ڸ��Ժ��ѵ����ݿ��в������
	public void addFriend(String Ofname,String Addname);
}