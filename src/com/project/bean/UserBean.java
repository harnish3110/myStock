package com.project.bean;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;

import com.project.dao.UserDao;

@ManagedBean(name = "userBean")
@SessionScoped
public class UserBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3174507835881437311L;

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

	public double getFees() {
		return fees;
	}

	public void setFees(double fees) {
		this.fees = fees;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

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

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = 100000;
	}

	public String register() {
		System.out.println("in register");
		UserDao dao = new UserDao();
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (type.equals("student")) {
			this.setFees(0.0);
			this.setStatus("approved");
			this.setBalance(100000);
		} else {
			this.setBalance(0);
			this.setStatus("pending");
		}

		if (dao.registerUser(this)) {
			facesContext.getExternalContext().getSession(true);
			facesContext.getExternalContext().getSessionMap().put("fullName", this.getFullName());
			facesContext.getExternalContext().getSessionMap().put("uid", this.getUser_id());
			facesContext.getExternalContext().getSessionMap().put("user", this);
		
			if (type.equals("student"))
				return "userHome?faces-redirect=true";
			else
				return "managerHome?faces-redirect=true";
		}
		else
			return "register?faces-redirect=true";
	}

	public String login() {
		System.out.println("in login");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		UserDao dao = new UserDao();
		if (dao.loginUser(this)) {
			System.out.println("Balance =" + this.getBalance());
			facesContext.getExternalContext().getSession(true);
			facesContext.getExternalContext().getSessionMap().put("fullName", this.getFullName());
			facesContext.getExternalContext().getSessionMap().put("uid", this.getUser_id());
			facesContext.getExternalContext().getSessionMap().put("user", this);
			if (this.getType().equals("student"))
				return "userHome?faces-redirect=true";
			else {
				return "managerHome?faces-redirect=true";
			}

		}
		System.out.println("invalid");
		facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
				"Incorrect Username and Passowrd. Please enter correct username and Password", ""));
		return "login";
	}

	public void logout() throws IOException {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
	}

	public String userUpdate() {
		try {
			UserDao dao = new UserDao();
			if (dao.updateUser(fullName, number, address, city, state, zip, email)) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Profile Updated Succesfully"));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error"));
		}
		return "userHome";
	}

}
