package com.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.project.bean.UserBean;
import com.project.utils.DBConnection;

public class UserDao {
	Connection connection;

	public UserDao() {
		DBConnection dataConnect = DBConnection.getInstance();
		connection = dataConnect.createConnection();
	}

	public boolean registerUser(UserBean userBean) {
		try {
			PreparedStatement statement = connection.prepareStatement(
					"insert into user(`name`,`email`,`password`,`number`,`address`,`city`,`state`,`zip`,`type`,`status`)VALUES(?,?,?,?,?,?,?,?,?,?); ");
			statement.setString(1, userBean.getFullName());
			statement.setString(2, userBean.getEmail());
			statement.setString(3, userBean.getPassword());
			statement.setString(4, userBean.getNumber());
			statement.setString(5, userBean.getAddress());
			statement.setString(6, userBean.getCity());
			statement.setString(7, userBean.getState());
			statement.setString(8, userBean.getZip());
			statement.setString(9, userBean.getType());
			statement.setString(10, userBean.getStatus());
			statement.executeUpdate();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	public boolean loginUser(UserBean userBean) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement statement = connection.prepareStatement(
					"select * from user where email=? and password = ? and type=? and status='approved'");
			statement.setString(1, userBean.getEmail());
			statement.setString(2, userBean.getPassword());
			statement.setString(3, userBean.getType());

			ResultSet rSet = statement.executeQuery();
			if (rSet.next()) {
				userBean.setFullName(rSet.getString("name"));
				userBean.setNumber(rSet.getString("number"));
				userBean.setAddress(rSet.getString("address"));
				userBean.setCity(rSet.getString("city"));
				userBean.setState(rSet.getString("state"));
				userBean.setZip(rSet.getString("zip"));
				userBean.setType(rSet.getString("type"));
				userBean.setUser_id(rSet.getInt("user_id"));
				userBean.setBalance(rSet.getDouble("balance"));
				return true;
			} else
				return false;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return false;
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
