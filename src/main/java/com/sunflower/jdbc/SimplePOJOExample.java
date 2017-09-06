package com.sunflower.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class SimplePOJOExample {
	public static void main(String[] args) {
		System.out.println("+++++++++++++++++++++++");
		PoolProperties p = new PoolProperties();
		p.setUrl("jdbc:mysql://localhost:3306/mysql?useUnicode=true&characterEncoding=utf-8&useSSL=false");
		p.setDriverClassName("com.mysql.jdbc.Driver");
		p.setUsername("root");
		p.setPassword("dreams");
//		p.setJmxEnabled(true);
//        p.setTestWhileIdle(false);
//        p.setTestOnBorrow(true);
		p.setValidationQuery("SELECT 1");
		p.setTestOnReturn(false);
		p.setValidationInterval(30000);
//		p.setTimeBetweenEvictionRunsMillis(30000);
//        p.setMaxActive(100);
//        p.setInitialSize(10);
//        p.setMaxWait(10000);
//        p.setRemoveAbandonedTimeout(60);
//        p.setMinEvictableIdleTimeMillis(30000);
//        p.setMinIdle(10);
//        p.setLogAbandoned(true);
//        p.setRemoveAbandoned(true);
		p.setJdbcInterceptors(
				"org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
				"org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
		System.out.println("+++++++++++++++++++++++");
		DataSource dataSource = new DataSource();
		dataSource.setPoolProperties(p);
		
		Connection con = null;
		try {
			con = dataSource.getConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("select * from user");
			System.out.println(rs.next());
			System.out.println(rs.getString("User"));
			int cnt = 1;
			while (rs.next()) {
				System.out.println((cnt++) + ". Host: " + rs.getString("Host") + " User: " + rs.getString("User"));
			}
			rs.close();
			st.close();
		} catch (Exception e) {
			
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (Exception ignore) {
					
				}
			}
		}
		
		
		
	}
}
