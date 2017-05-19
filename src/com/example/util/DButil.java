package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DButil {
	public static Connection getConnection(){
		Connection conn = null;
		// 加载数据库的驱动类
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String url = "jdbc:sqlserver://localhost:1433;DatabaseName=MapProject";
			String username = "吴庭明";
			String password = "1234";
			conn = DriverManager.getConnection(url,username,password); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static void closeConn(Object o){
		if(o!=null){
			if(o instanceof Connection){
				try {
					((Connection)o).close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if(o instanceof Statement){
				try {
					((Statement)o).close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if(o instanceof PreparedStatement){
				try {
					((PreparedStatement)o).close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(o instanceof ResultSet){
				try {
					((ResultSet)o).close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
