package com.project.bean;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.validation.constraints.NotNull;

@ManagedBean(name = "managerBean")
public class ManagerBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6398883017887723456L;
	@NotNull(message = "Field can't be null")
	private String fullName;
	@NotNull(message = "Field can't be null")
	private String password;
	@NotNull(message = "Field can't be null")
	private String email;
	@NotNull(message = "Field can't be null")
	private String number;
	@NotNull(message = "Field can't be null")
	private String address;
	@NotNull(message = "Field can't be null")
	private String city;
	@NotNull(message = "Field can't be null")
	private String state;
	@NotNull(message = "Field can't be null")
	private String zip;
	@NotNull(message = "Field can't be null")
	private String type;
	private String status;
	private int user_id;
	private double balance;
	private double fees;

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

	public double getFees() {
		return fees;
	}

	public void setFees(double fees) {
		this.fees = fees;
	}

}
