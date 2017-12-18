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
import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.project.utils.DBConnection;

@ManagedBean(name = "stockDetailsBean")
@SessionScoped
public class StockDetailsBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3142800199115920172L;
	private String selectedStock;
	private String selectedStockName;
	private String quantity;
	private String stockDetailTable;
	private String stockHistoryTable;
	private String selectedInterval;
	private String sellStockDetailsTable;
	static final String API_key = "8N6HGF0UYVVVVZSV";
	static final String api_url_intrady = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY";
	private double amount;
	private double currentPrice;

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getSelectedInterval() {
		if (getRequestParameter("interval") != null)
			return (String) getRequestParameter("interval");
		return "60min";
	}

	public void setSelectedInterval(String selectedInterval) {
		this.selectedInterval = selectedInterval;
	}

	public String getSelectedStock() {
		String symbol = "";
		if (getRequestParameter("symbol") != null) {
			symbol = (String) getRequestParameter("symbol");
		}
		return symbol;
	}

	public void setSelectedStock(String selectedStock) {
		this.selectedStock = selectedStock;
	}

	public String getSelectedStockName() {
		String stock = "";
		if (getRequestParameter("stockName") != null) {
			stock = (String) getRequestParameter("stockName");
		}
		return stock;
	}

	public void setSelectedStockName(String selectedStockName) {
		this.selectedStockName = selectedStockName;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public String getStockDetailTable() {
		return stockDetailTable;
	}

	public void setStockDetailTable(String stockDetailTable) {
		this.stockDetailTable = stockDetailTable;
	}

	public String getStockHistoryTable() {
		return stockHistoryTable;
	}

	public void setStockHistoryTable(String stockHistoryTable) {
		this.stockHistoryTable = stockHistoryTable;
	}

	public String getSellStockDetailsTable() {
		return sellStockDetailsTable;
	}

	public void setSellStockDetailsTable(String sellStockDetailsTable) {
		this.sellStockDetailsTable = sellStockDetailsTable;
	}

	private Object getRequestParameter(String name) {
		return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
				.getParameter(name);
	}

	public String getStockInfo(String stock, String interval) {
		selectedStock = stock;
		selectedInterval = interval;
		try {
			DBConnection dataConnect = DBConnection.getInstance();
			Connection connection = dataConnect.createConnection();
			Statement statement = connection.createStatement();
			ResultSet rSet = statement.executeQuery("select * from watch_lists where stock_symbol = '" + stock + "'");
			if (rSet.first())
				selectedStockName = rSet.getString("stock_name");
			else
				selectedStockName = stock;
			statement.close();
			rSet.close();
			connection.close();
			return "stockDetails";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "userHome";
		}

	}

	// @PostConstruct
	public void getStockDetails() {
		try {
			this.setStockDetailTable("");
			this.setStockHistoryTable("");
			if (selectedStock == null)
				selectedStock = this.getSelectedStock();
			if (selectedInterval == null)
				selectedInterval = this.getSelectedInterval();
			if (selectedStockName == null)
				selectedStockName = this.getSelectedStockName();
			System.out.println("Get Stock Details " + selectedStock);
			String url = api_url_intrady + "&interval=" + selectedInterval + "&symbol=" + selectedStock + "&apikey="
					+ API_key;
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
				if (key.equals("Meta Data")) {

					JSONObject data = output.getJSONObject(key);
					this.stockDetailTable = "<table class='table table-hover'>";
					this.stockDetailTable += "<tr><td><strong>Information</strong></td><td>"
							+ data.getString("1. Information") + "</td></tr>";
					this.stockDetailTable += "<tr><td>Stock Name</td><td>" + selectedStockName + "</td></tr>";
					this.stockDetailTable += "<tr><td>Symbol</td><td><p id='stockSymbol'>" + data.getString("2. Symbol")
							+ "</p></td></tr>";
					this.stockDetailTable += "<tr><td>Last Refreshed</td><td>" + data.getString("3. Last Refreshed")
							+ "</td></tr>";
					this.stockDetailTable += "<tr><td>Interval</td><td>" + data.getString("4. Interval") + "</td></tr>";
					this.stockDetailTable += "<tr><td>Time Zone</td><td>" + data.getString("6. Time Zone")
							+ "</td></tr>";
					this.stockDetailTable += "</table>";
					FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedSymbol",
							data.getString("2. Symbol"));
					FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("selectedStockName",
							selectedStockName);

				} else {
					Iterator<?> timeSeries = output.getJSONObject(key).keys();
					System.out.println(output.get(key));
					int flag = 1;
					this.stockHistoryTable = "<table class='table table-hover'>";
					this.stockHistoryTable += "<thead><tr><th>Timestamp</th><th>Open</th><th>High</th><th>Low</th><th>Close</th><th>Volume</th></tr></thead>";
					this.stockHistoryTable += "<tbody>";
					while (timeSeries.hasNext()) {
						String historyKey = (String) timeSeries.next();
						JSONObject history = output.getJSONObject(key).getJSONObject(historyKey);
						this.stockHistoryTable += "<tr>" + "<td>" + historyKey + "</td>" + "<td>"
								+ history.getString("1. open") + "</td>" + "<td>" + history.getString("2. high")
								+ "</td>" + "<td>" + history.getString("3. low") + "</td>" + "<td>"
								+ history.getString("4. close") + "</td>" + "<td>" + history.getString("5. volume")
								+ "</td>";

						this.stockHistoryTable += "</tr>";
						if (flag == 1) {
							FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("stockPrice",
									Double.parseDouble(history.getString("4. close")));
						}

					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public String purchaseStock(String symbol, String stockName, double price, int qty, double total) {
		try {
			DBConnection dataConnect = DBConnection.getInstance();
			UserBean user = (UserBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
					.get("user");
			System.out.println("city =" + user.getCity());
			System.out.println(user.getBalance());
			double balance = user.getBalance();
			if (balance > total) {
				Connection connection = dataConnect.createConnection();
				PreparedStatement statement = connection.prepareStatement(
						"insert into purchase(`user_id`,`stock_symbol`,`stock_name`,`stock_price`,`quantity`,`total`,`purchased_by`)VALUES(?,?,?,?,?,?,?)");
				statement.setInt(1, user.getUser_id());
				statement.setString(2, symbol);
				statement.setString(3, stockName);
				statement.setDouble(4, price);
				statement.setInt(5, qty);
				statement.setDouble(6, total);
				statement.setString(7, "Self");

				statement.executeUpdate();
				double left = balance - total;
				connection.createStatement()
						.executeUpdate("update user SET balance=" + left + " where user_id=" + user.getUser_id());
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Stock Bought Successfullyt", ""));
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Not enoughr Credit available", ""));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "userHome";
	}

	public void getCurrentStockPrice() {
		// TODO Auto-generated method stub
		try {
			String sellSymbol = (String) ServiceBean.getRequestParameter("sellStockSymbol");
			String url = api_url_intrady + "&interval=60min&symbol="
					+ (String) ServiceBean.getRequestParameter("sellStockSymbol") + "&apikey=" + API_key;
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
				if (key.equals("Meta Data")) {

					JSONObject data = output.getJSONObject(key);
					this.sellStockDetailsTable = "<table class='table table-hover'>";
					this.sellStockDetailsTable += "<tr><td><strong>Information</strong></td><td>"
							+ data.getString("1. Information") + "</td></tr>";
					this.sellStockDetailsTable += "<tr><td>Stock Name</td><td>"
							+ (String) ServiceBean.getRequestParameter("sellStockName") + "</td></tr>";
					this.sellStockDetailsTable += "<tr><td>Symbol</td><td><p id='stockSymbol'>"
							+ data.getString("2. Symbol") + "</p></td></tr>";
					this.sellStockDetailsTable += "<tr><td>Last Refreshed</td><td>"
							+ data.getString("3. Last Refreshed") + "</td></tr>";
					this.sellStockDetailsTable += "<tr><td>Interval</td><td>" + data.getString("4. Interval")
							+ "</td></tr>";
					this.sellStockDetailsTable += "<tr><td>Available Qty</td><td>"
							+ (int) ServiceBean.getRequestParameter("sellStockQty") + "</td></tr>";
					this.sellStockDetailsTable += "<tr><td>Time Zone</td><td>" + data.getString("6. Time Zone")
							+ "</td></tr>";
					this.sellStockDetailsTable += "</table>";
				} else {
					int flag = 1;
					Iterator<?> timeSeries = output.getJSONObject(key).keys();
					while (timeSeries.hasNext()) {
						if (flag == 1) {
							String historyKey = (String) timeSeries.next();
							JSONObject history = output.getJSONObject(key).getJSONObject(historyKey);
							FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
									.put("currentStockPrice", Double.parseDouble(history.getString("4. close")));
							this.setCurrentPrice(Double.parseDouble(history.getString("4. close")));
						}
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public String sellStock(int sellQty) {
		int availQty = (int) ServiceBean.getRequestParameter("sellStockQty");
		UserBean user = (UserBean) ServiceBean.getRequestParameter("user");
		if (sellQty > 0 && sellQty <= sellQty) {
			try {
				DBConnection dataConnect = DBConnection.getInstance();
				Connection connection = dataConnect.createConnection();
				double total = currentPrice * sellQty;
				double balance = total + user.getBalance();
				PreparedStatement statement = connection.prepareStatement(
						"insert into sell(`user_id`,`stock_symbol`,`stock_name`,`stock_price`,`quantity`,`total`,`purchased_by`)VALUES(?,?,?,?,?,?,?)");
				statement.setInt(1, (int) ServiceBean.getRequestParameter("uid"));
				statement.setString(2, (String) ServiceBean.getRequestParameter("sellStockSymbol"));
				statement.setString(3, (String) ServiceBean.getRequestParameter("sellStockName"));
				statement.setDouble(4, currentPrice);
				statement.setInt(5, sellQty);
				statement.setDouble(6, total);
				statement.setString(7, "Self");

				statement.executeUpdate();

				Statement stmt = connection.createStatement();
				stmt.executeUpdate("update user SET balance=" + balance + " where user_id=" + user.getUser_id());
				Statement stmt2 = connection.createStatement();
				stmt2.executeUpdate("update purchase SET quantity = " + (availQty - sellQty) + " where stock_symbol='"
						+ (String) ServiceBean.getRequestParameter("sellStockSymbol") + "' and user_id="
						+ (int) ServiceBean.getRequestParameter("uid"));
				ServiceBean.setSessionMessage("Stock has been sold.");
				return "userHome";
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			ServiceBean.setSessionMessage("Quantity should be between 0 and " + availQty);
		}

		return "sellStock";
	}

	public String sellManagerMsg(int sellQty) {
		try {
			DBConnection data = DBConnection.getInstance();
			Connection connection = data.createConnection();
			PreparedStatement statement = connection.prepareStatement(
					"insert into communication (`user_id`,`manager_id`,`message`,`message_for`)VALUES(?,?,?,?)");
			UserBean user = (UserBean) ServiceBean.getRequestParameter("user");
			System.out.println(user.getFullName() + " " + user.getUser_id() + " " + user.getManager_id());
			statement.setInt(1, (int) ServiceBean.getRequestParameter("uid"));
			statement.setInt(2, user.getManager_id());
			statement.setString(3,
					(String) ServiceBean.getRequestParameter("sellStockName") + "_"
							+ (String) ServiceBean.getRequestParameter("sellStockSymbol") + "_" + sellQty + "_"
							+ user.getFullName() + "_" + "sell");
			statement.setString(4, "manager");
			statement.executeUpdate();
			ServiceBean.setSessionMessage("Request has been sent to Manager for Selling " + sellQty + " of "
					+ (String) ServiceBean.getRequestParameter("sellStockName"));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "userHome";
	}

}
