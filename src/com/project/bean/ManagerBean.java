package com.project.bean;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;

import org.json.JSONObject;

import com.project.utils.DBConnection;

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
	private String commTable;

	public String getCommTable() {
		return commTable;
	}

	public void setCommTable(String commTable) {
		this.commTable = commTable;
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

	public List<ManagerBean> populateManager() {
		List<ManagerBean> managers = new ArrayList<ManagerBean>();
		try {
			DBConnection data = DBConnection.getInstance();
			Connection con = data.createConnection();
			Statement statement = con.createStatement();
			ResultSet rSet = statement.executeQuery("select * from user where type='manager' and status='approved'");
			while (rSet.next()) {
				ManagerBean manager = new ManagerBean();
				manager.setFullName(rSet.getString("name"));
				manager.setEmail(rSet.getString("email"));
				manager.setFees(rSet.getDouble("fees"));
				manager.setUser_id(rSet.getInt("user_id"));
				manager.setNumber(rSet.getString("number"));
				managers.add(manager);

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return managers;
	}

	public String addtoUser(int mId) {
		try {
			int user_id = (int) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("uid");
			DBConnection data = DBConnection.getInstance();
			Connection connection = data.createConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate("update user SET manager_id=" + mId + " where user_id=" + user_id);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("managerId", mId);
			ServiceBean.setSessionMessage("Manager has been added to the Profile");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			ServiceBean.setSessionMessage("Some Error MB");
		}
		return "userHome";
	}

	public List<CommunicationBean> populateCommunication() {
		List<CommunicationBean> messages = new ArrayList<CommunicationBean>();
		try {
			UserBean manager = (UserBean) ServiceBean.getRequestParameter("user");
			DBConnection dataConnect = DBConnection.getInstance();
			Connection connection = dataConnect.createConnection();
			Statement statement = connection.createStatement();
			ResultSet rSet = statement.executeQuery(
					"select * from communication where message_for='manager' and manager_id=" + manager.getUser_id());
			while (rSet.next()) {
				CommunicationBean bean = new CommunicationBean();

				String message = rSet.getString("message");
				String data[] = message.split("_");
				bean.setQty(Integer.parseInt(data[2]));
				bean.setStockName(data[0]);
				bean.setStockSymbol(data[1]);
				bean.setStudentId(rSet.getInt("user_id"));
				bean.setStudentName(data[3]);
				bean.setTransaction(data[4]);
				bean.setId(rSet.getInt("communication_id"));

				messages.add(bean);

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return messages;
	}

	public String doAction(CommunicationBean bean) {
		System.out.println("hhhh");
		UserBean manager = (UserBean) ServiceBean.getRequestParameter("user");
		System.out.println(bean.getStudentName());
		double price = this.getCurStockPrice(bean.getStockSymbol());
		double totalAmt = price * bean.getQty();
		double commision = (totalAmt * manager.getFees()) / 100;
		double balance1 = manager.getBalance();
		int userId = manager.getUser_id();
		manager.setBalance(balance1+commision);
		try {
			DBConnection data = DBConnection.getInstance();
			Connection connection = data.createConnection();
			PreparedStatement statement = null;
			if (bean.getTransaction().equals("purchase")) {
				System.out.println("puchase");
				statement = connection.prepareStatement(
						"insert into purchase (`user_id`,`stock_symbol`,`stock_name`,`stock_price`,`quantity`,`total`,`purchased_by`)VALUES(?,?,?,?,?,?,?)");
			} else {
				System.out.println("sell");
				statement = connection.prepareStatement(
						"insert into sell (`user_id`,`stock_symbol`,`stock_name`,`stock_price`,`quantity`,`total`,`purchased_by`)VALUES(?,?,?,?,?,?,?)");
			}
			statement.setInt(1, bean.getStudentId());
			statement.setString(2, bean.getStockSymbol());
			statement.setString(3, bean.getStockName());
			statement.setDouble(4, price);
			statement.setInt(5, bean.getQty());
			statement.setDouble(6, totalAmt);
			statement.setString(7, "Manager");

			statement.executeUpdate();

			Statement stmt = connection.createStatement();
			stmt.executeUpdate("delete from communication where communication_id=" + bean.getId());
			stmt = connection.createStatement();
			stmt.executeUpdate("update user SET balance=" + (balance1 + commision) + " where user_id=" + userId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "managerHome";
	}

	public double getCurStockPrice(String symbol) {
		// TODO Auto-generated method stub
		try {
			String url = StockDetailsBean.api_url_intrady + "&interval=60min&symbol=" + symbol + "&apikey="
					+ StockDetailsBean.API_key;
			StringBuffer response = new StringBuffer();
			String inLine;
			JSONObject output;
			URL urlAdd = new URL(url);
			HttpURLConnection con = (HttpURLConnection) urlAdd.openConnection();
			con.setRequestMethod("GET");
			con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			while ((inLine = in.readLine()) != null) {
				response.append(inLine);
			}
			output = new JSONObject(response.toString());
			Iterator<?> keys = output.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				if (!key.equals("Meta Data")) {
					int flag = 1;
					Iterator<?> timeSeries = output.getJSONObject(key).keys();
					while (timeSeries.hasNext()) {
						if (flag == 1) {
							String historyKey = (String) timeSeries.next();
							JSONObject history = output.getJSONObject(key).getJSONObject(historyKey);
							return Double.parseDouble(history.getString("4. close"));
						}
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return 0.0;
	}

}
