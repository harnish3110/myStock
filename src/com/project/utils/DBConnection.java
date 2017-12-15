package com.project.utils;

import java.sql.Connection;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DBConnection {
	private static DBConnection dataConnect = null;

	private DBConnection() {

	}

	public static DBConnection getInstance() {
		if (dataConnect == null)
			dataConnect = new DBConnection();
		return dataConnect;
	}

	public Connection createConnection() {
		Connection connection = null;
		try {

			MysqlDataSource db = new MysqlDataSource();
			System.out.println("Data Connect");
			System.out.println(System.getenv("ICSI518_SERVER"));
			System.out.println(Integer.parseInt(System.getenv("ICSI518_PORT")));
			System.out.println(System.getenv("ICSI518_DB"));
			System.out.println(System.getenv("ICSI518_USER"));
			System.out.println(System.getenv("ICSI518_PASSWORD"));

			db.setServerName(System.getenv("ICSI518_SERVER"));
			db.setPortNumber(Integer.parseInt(System.getenv("ICSI518_PORT")));
			db.setDatabaseName(System.getenv("ICSI518_DB"));
			db.setUser(System.getenv("ICSI518_USER"));
			db.setPassword(System.getenv("ICSI518_PASSWORD"));
			connection = db.getConnection();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
}
