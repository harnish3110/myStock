package com.project.bean;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;

import com.project.dao.UserDao;
import com.project.utils.DBConnection;

@ManagedBean
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
	private int manager_id;
	private int selectedQty;
	private boolean loggedIn;

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public int getSelectedQty() {
		return selectedQty;
	}

	public void setSelectedQty(int selectedQty) {
		this.selectedQty = selectedQty;
	}

	public int getManager_id() {
		return manager_id;
	}

	public void setManager_id(int manager_id) {
		this.manager_id = manager_id;
	}

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
		this.balance = balance;
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
			loggedIn = true;
			facesContext.getExternalContext().getSession(true);
			facesContext.getExternalContext().getSessionMap().put("fullName", this.getFullName());
			facesContext.getExternalContext().getSessionMap().put("uid", this.getUser_id());
			facesContext.getExternalContext().getSessionMap().put("user", this);

			if (type.equals("student"))
				return "userHome?faces-redirect=true";
			else
				return "managerHome?faces-redirect=true";
		} else
			return "register?faces-redirect=true";
	}

	public String login() {
		System.out.println("in login");
		FacesContext facesContext = FacesContext.getCurrentInstance();
		UserDao dao = new UserDao();
		if (dao.loginUser(this)) {
			loggedIn = true;
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
		loggedIn = false;
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
		if (type.equals("student"))
			return "userHome?faces-redirect=true";
		else
			return "managerHome?faces-redirect=true";
	}

	public List<StockBean> populateStocks() {
		List<StockBean> stocks = new ArrayList<StockBean>();
		try {
			this.setSelectedQty(0);

			DBConnection data = DBConnection.getInstance();
			Connection con = data.createConnection();
			Statement statement = con.createStatement();
			ResultSet rSet = statement.executeQuery("select * from watch_lists where user_id=" + this.getUser_id());
			while (rSet.next()) {
				StockBean stock = new StockBean();
				stock.setStockSymbol(rSet.getString("stock_symbol"));
				stock.setStockName(rSet.getString("stock_name"));
				stocks.add(stock);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return stocks;
	}

	public String sendMsgToManager(String stockName, String stockSymbol, int qty) {
		try {
			System.out.println("qty " + qty);
			DBConnection data = DBConnection.getInstance();
			Connection connection = data.createConnection();
			PreparedStatement statement = connection.prepareStatement(
					"insert into communication (`user_id`,`manager_id`,`message`,`message_for`)VALUES(?,?,?,?)");
			UserBean user = this;
			System.out.println(user.getFullName() + " " + user.getUser_id() + " " + user.getManager_id());
			statement.setInt(1, (int) ServiceBean.getRequestParameter("uid"));
			statement.setInt(2, this.getManager_id());
			statement.setString(3,
					stockName + "_" + stockSymbol + "_" + qty + "_" + this.getFullName() + "_" + "purchase");
			statement.setString(4, "manager");
			statement.executeUpdate();
			ServiceBean.setSessionMessage("Request has been sent to Manager for Buying " + qty + " of " + stockName);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "userHome";
	}

	public List<StockBean> populateWatchList() {
		List<StockBean> stocks = new ArrayList<StockBean>();
		Connection connection;
		Statement statement;
		ResultSet rSet;
		try {
			DBConnection dataConnect = DBConnection.getInstance();
			int userId = (int) ServiceBean.getRequestParameter("uid");
			connection = dataConnect.createConnection();
			statement = connection.createStatement();
			rSet = statement.executeQuery("select * from watch_lists where user_id=" + userId);
			if (rSet.next()) {
				rSet.previous();
				while (rSet.next()) {
					StockBean stock = new StockBean();
					stock.setStockName(rSet.getString("stock_name"));
					stock.setStockSymbol(rSet.getString("stock_symbol"));
					stocks.add(stock);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return stocks;
	}

	public String removeStock(String symbol) {
		Connection connection;
		Statement statement;
		try {
			DBConnection dataConnect = DBConnection.getInstance();
			int userId = (int) ServiceBean.getRequestParameter("uid");
			connection = dataConnect.createConnection();
			statement = connection.createStatement();
			statement
					.executeUpdate("delete from watch_lists where stock_symbol='" + symbol + "' and user_id=" + userId);
			ServiceBean.setSessionMessage("Stock has been removed from Watch List");

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "account";
	}

	public List<StockApi> populateHistory() {
		List<StockApi> stockHistory = new ArrayList<StockApi>();
		Connection connection;
		Statement statement;
		ResultSet rSet;
		try {
			DBConnection dataConnect = DBConnection.getInstance();
			int userId = (int) ServiceBean.getRequestParameter("uid");
			connection = dataConnect.createConnection();
			statement = connection.createStatement();
			rSet = statement.executeQuery("select * from purchase where user_id=" + userId);
			if (rSet.next()) {
				rSet.previous();
				while (rSet.next()) {
					StockApi stock = new StockApi();
					stock.setSymbolName(rSet.getString("stock_name"));
					stock.setSymbol(rSet.getString("stock_symbol"));
					stock.setPrice(rSet.getDouble("stock_price"));
					stock.setQty(rSet.getInt("quantity"));
					stock.setAmt(rSet.getDouble("total"));
					stock.setPurchasedBy(rSet.getString("purchased_by"));

					stockHistory.add(stock);

				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return stockHistory;
	}

	public List<StockApi> populateSoldHistory() {
		List<StockApi> stockHistory = new ArrayList<StockApi>();
		Connection connection;
		Statement statement;
		ResultSet rSet;
		try {
			DBConnection dataConnect = DBConnection.getInstance();
			int userId = (int) ServiceBean.getRequestParameter("uid");
			connection = dataConnect.createConnection();
			statement = connection.createStatement();
			rSet = statement.executeQuery("select * from sell where user_id=" + userId);
			if (rSet.next()) {
				rSet.previous();
				while (rSet.next()) {
					StockApi stock = new StockApi();
					stock.setSymbolName(rSet.getString("stock_name"));
					stock.setSymbol(rSet.getString("stock_symbol"));
					stock.setPrice(rSet.getDouble("stock_price"));
					stock.setQty(rSet.getInt("quantity"));
					stock.setAmt(rSet.getDouble("total"));
					stock.setPurchasedBy(rSet.getString("purchased_by"));

					stockHistory.add(stock);

				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return stockHistory;
	}
}
