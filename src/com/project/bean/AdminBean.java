package com.project.bean;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.project.dao.UserDao;
import com.project.utils.DBConnection;

@ManagedBean(name = "adminBean")
@SessionScoped
public class AdminBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7186718491744195365L;
	private String fullName;
	private String password;
	private String email;
	private String number;
	private String address;
	private String city;
	private String state;
	private String zip;
	private String type;
	private String status;
	private int user_id;
	private double balance;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public String validateAdminLogin() {
		UserDao dao = new UserDao();
		if (dao.validateAdminLogin(this)) {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("admminId", this.getUser_id());
		}
		return "adminHome?faces-redirect=true";
	}

	public List<ManagerBean> populateManager() {
		List<ManagerBean> managers = new ArrayList<ManagerBean>();
		try {
			DBConnection data = DBConnection.getInstance();
			Connection con = data.createConnection();
			Statement statement = con.createStatement();
			ResultSet rSet = statement.executeQuery("select * from user where type='manager' and status='pending'");
			while (rSet.next()) {
				ManagerBean manager = new ManagerBean();
				manager.setFullName(rSet.getString("name"));
				manager.setEmail(rSet.getString("email"));
				manager.setFees(rSet.getDouble("fees"));
				manager.setUser_id(rSet.getInt("user_id"));
				managers.add(manager);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return managers;
	}

	public String updateManagerRequest(int id, String status) {
		try {
			DBConnection data = DBConnection.getInstance();
			Connection con = data.createConnection();
			Statement statement = con.createStatement();
			statement.executeUpdate("update user SET status='" + status + "' where user_id=" + id);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Manager has beed +" + status));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "adminHome";
	}

}
