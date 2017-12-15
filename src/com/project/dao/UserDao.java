package com.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.project.bean.AdminBean;
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

	public boolean updateUser(String fullName, String number, String address, String city, String state, String zip,
			String email) {
		try {
			PreparedStatement statement = connection
					.prepareStatement("update user SET name=?,number=?,address=?,city=?,state=?,zip=? where email=?");
			statement.setString(1, fullName);
			statement.setString(2, number);
			statement.setString(3, address);
			statement.setString(4, city);
			statement.setString(5, state);
			statement.setString(6, zip);
			statement.setString(7, email);

			statement.executeUpdate();
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}

	}

	public boolean validateAdminLogin(AdminBean adminBean) {
		// TODO Auto-generated method stub
		try {
			Statement statement = connection.createStatement();
			ResultSet rSet = statement.executeQuery("select * from user where email='" + adminBean.getEmail()
					+ "' and password='" + adminBean.getPassword() + "' and type='admin'");
			if (rSet.next()) {
				adminBean.setFullName(rSet.getString("name"));
				adminBean.setNumber(rSet.getString("number"));
				adminBean.setAddress(rSet.getString("address"));
				adminBean.setCity(rSet.getString("city"));
				adminBean.setState(rSet.getString("state"));
				adminBean.setZip(rSet.getString("zip"));
				adminBean.setType(rSet.getString("type"));
				adminBean.setUser_id(rSet.getInt("user_id"));
				adminBean.setBalance(rSet.getDouble("balance"));
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

}
