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

	public static void main(String a[]) {
		DBConnection data = DBConnection.getInstance();
		try {
			Connection con = data.createConnection();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
