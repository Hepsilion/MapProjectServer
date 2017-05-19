package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.tran.TranObject;
import com.example.util.Constants;
import com.example.util.DButil;
import com.example.util.User;

//�����û���������Ľ���������ע�ᣬ��¼�����Һ��ѣ�������Ӻ���
public class UserDaoProcess implements UserDao {

	// ע��
	@Override
	public boolean register(User u) {
		boolean flag = false;
		Connection conn = DButil.getConnection();
		String sql1 = "insert into mapUser(name,password,email) values(?,?,?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql1);
			ps.setString(1, u.getName());
			ps.setString(2, u.getPassword());
			ps.setString(3, u.getEmail());
			if (ps.executeUpdate() > 0) {
				String name = u.getName();
				// ע��ɹ��󣬴���һ�����û�idΪ�����ı����ڴ�ź�����Ϣ
				createFriendtable(name);
				flag = true;
				return flag;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.closeConn(conn);
		}
		return flag;
	}

	// ��¼����½�ɹ��󷵻��û������б�,�����¼ʧ��
	@Override
	public ArrayList<User> login(User u) {
		Connection conn = DButil.getConnection();
		String sql = "select * from mapUser where name=? and password=?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, u.getName());
			ps.setString(2, u.getPassword());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				setOnline(u.getName());
				ArrayList<User> refreshList = refresh(u.getName());
				return refreshList;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.closeConn(conn);
		}
		return null;
	}

	@Override
	public void logout(String name) {
		// TODO Auto-generated method stub
		Connection conn = DButil.getConnection();
		try {
			String sql = "update mapUser set isOnline=0 where name=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.executeUpdate();
			updateAllOff(name);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.closeConn(conn);
		}
	}

	// ����
	public User find(String name) {
		User user = new User();
		Connection conn = DButil.getConnection();
		String sql = "select * from mapUser where name=?";
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				user.setName(rs.getString("name"));
				user.setIsOnline(rs.getInt("isOnline"));
			}
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.closeConn(conn);
		}
		return null;
	}

	// ��ú����б�
	public ArrayList<User> refresh(String name) {
		ArrayList<User> list = new ArrayList<User>();
		User user = find(name);
		list.add(user);// ������Լ�
		Connection conn = DButil.getConnection();
		String sql = "select * from " + name;
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				User friend = new User();
				friend.setName(rs.getString("name"));
				friend.setIsOnline(rs.getInt("isOnline"));
				list.add(friend);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();// ��һ�б���ע�͵��ˣ����������һ�����쳣������ע�͵�����û���Ӱ��
		} finally {
			DButil.closeConn(conn);
		}
		return null;
	}

	/**
	 * ע��ɹ��󣬴���һ���û���������û�����
	 * 
	 * @param id
	 */
	public void createFriendtable(String name) {
		Connection conn = DButil.getConnection();
		try {
			String sql = "create table " + name + "(name varchar(20) not null,"
					+ "isOnline int not null default 0)";
			PreparedStatement ps = conn.prepareStatement(sql);
			int res = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.closeConn(conn);
		}
	}

	/**
	 * ����״̬Ϊ����
	 * 
	 * @param id
	 */
	public void setOnline(String name) {
		Connection conn = DButil.getConnection();
		try {
			String sql = "update mapUser set isOnline=1 where name=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.executeUpdate();
			updateAllOn(name);// �������к��ѵ����ѱ����Լ�״̬Ϊ����
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.closeConn(conn);
		}
	}

	/**
	 * ���������û���״̬Ϊ����
	 * 
	 * @param id
	 */
	public void updateAllOff(String name) {
		Connection conn = DButil.getConnection();
		try {
			for (String name1 : getAllName()) {
				String sql = "update " + name1 + " set isOnline=0 where name=?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, name);
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.closeConn(conn);
		}
	}

	/**
	 * ���������û�״̬Ϊ����
	 * 
	 * @param id
	 */
	public void updateAllOn(String name) {
		Connection conn = DButil.getConnection();
		try {
			for (String name1 : getAllName()) {
				String sql = "update " + name1 + " set isOnline=1 where name=?";
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, name);
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();// ��һ�б���ע�͵��ˣ����������һ�����쳣������ע�͵�����û���Ӱ��
		} finally {
			DButil.closeConn(conn);
		}
	}

	public List<String> getAllName() {
		Connection conn = DButil.getConnection();
		List<String> list = new ArrayList<String>();
		try {
			String sql = "select name from mapUser";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				list.add(name);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.closeConn(conn);
		}
		return null;
	}

	// ͬ���������󣬷��ظ��û������б�
	@Override
	public void addFriend(String Ofname, String Addname) {
		// TODO Auto-generated method stub
		Connection conn = DButil.getConnection();
		try {
			String sql = "insert into " + Ofname + " (name) values(?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, Addname);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DButil.closeConn(conn);
		}
	}

	// public static void main(String[] args) {
	// User u = new User();
	// u.setName("qq1");
	// u.setPassword("123");
	// u.setEmail("158342219@qq.com");
	// UserDaoProcess dao = new UserDaoProcess();
	// // //��֤ע��
	// boolean register=dao.register(u);
	// if(register==true)
	// System.out.print("ע��ɹ���");
	// else
	// System.out.print("ע��ʧ�ܣ�");
	// //��֤��¼
	// // ArrayList list=dao.login(u);
	// }

}
