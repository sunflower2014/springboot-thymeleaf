package com.sunflower.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PureJDBCExample {
	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		//Any JDBC 4.0 drivers that are found in your class path are automatically loaded. (However, you must manually load any drivers prior to JDBC 4.0 with the method Class.forName.)
		Class.forName("com.mysql.jdbc.Driver");  
		
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=utf-8&useSSL=false", "root", "Dreams576");
		//call the method Statement.close to immediately release the resources it is using. When you call this method, its ResultSet objects are closed.
		try (Statement stat = con.createStatement();) {
			ResultSet rs = stat.executeQuery("SELECT * FROM user");
			while (rs.next()) {
				System.out.println("Host:" + rs.getString("Host") + " User: " + rs.getString("User"));
			}
		}
		
		con.close();
	}
}
