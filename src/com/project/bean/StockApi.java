package com.project.bean;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.project.utils.DBConnection;

/**
 * Session Bean implementation class StockApi
 */
@ManagedBean(name = "stockApi")
@SessionScoped
public class StockApi implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1522911258283628844L;
	private String symbol;
	private double price;
	private int qty;
	private double amt;
	private String selectedSymbol;
	private List<SelectItem> allSymbols;
	private String allStockTable = "";
	private List<SelectItem> watchList;
	private List<SelectItem> availableIntervals;
	private String selectedInterval;

	/**
	 * Default constructor.
	 */

	public StockApi() {
		// TODO Auto-generated constructor stub
	}

	public String getSelectedInterval() {
		return selectedInterval;
	}

	public void setSelectedInterval(String selectedInterval) {
		this.selectedInterval = selectedInterval;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

	public double getAmt() {
		return amt;
	}

	public void setAmt(double amt) {
		this.amt = amt;
	}

	public String getSelectedSymbol() {
		return selectedSymbol;
	}

	public void setSelectedSymbol(String selectedSymbol) {
		this.selectedSymbol = selectedSymbol;
	}

	public List<SelectItem> getAllSymbols() {
		return allSymbols;
	}

	public void setAllSymbols(List<SelectItem> allSymbols) {
		this.allSymbols = allSymbols;
	}

	public String getAllStockTable() {
		return allStockTable;
	}

	public List<SelectItem> getWatchList() {
		return watchList;
	}

	public void setWatchList(List<SelectItem> watchList) {
		this.watchList = watchList;
	}

	public void setAllStockTable(String allStockTable) {
		this.allStockTable = allStockTable;
	}

	private Object getRequestParameter(String name) {
		return (Object) (FacesContext.getCurrentInstance().getExternalContext().getSessionMap()).get(name);
	}

	public List<SelectItem> getAvailableIntervals() {
		return availableIntervals;
	}

	public void setAvailableIntervals(List<SelectItem> availableIntervals) {
		this.availableIntervals = availableIntervals;
	}

	@PostConstruct
	public void initStocks() {
		try {
			File file = new File(StockApi.class.getResource("../resources/stockList.json").getFile());
			String content = FileUtils.readFileToString(file, "utf-8");
			// String path =
			// FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();

			JSONArray stocks = new JSONArray(content);
			allStockTable = "<table id='allStocksList' class=\"table table-striped table-bordered\" cellspacing=\"0\" width=\"100%\">";
			allStockTable += " <h:form><thead>\r\n" + "<tr>\r\n" + "<th>Name</th>\r\n" + "<th>Symbol</th>\r\n"
					+ "<th>Sector</th>\r\n" + "<th>Industry</th>\r\n" + "<th>Action</th>\r\n" + "</tr>\r\n"
					+ "</thead><tbody>";
			for (int i = 0; i < 20; i++) {
				JSONObject stock = stocks.getJSONObject(new Random().nextInt(stocks.length()));
				allStockTable += "<tr>\r\n" + "<td>" + stock.getString("Name") + "</td>\r\n" + "<td>"
						+ stock.getString("Symbol") + "</td>\r\n" + "<td>" + stock.getString("Sector") + "</td>\r\n"
						+ "<td>" + stock.getString("industry") + "</td>\r\n"
						+ "<td><a class='btn btn-primary' href='stockDetails.xhtml?symbol=" + stock.getString("Symbol")
						+ "&stockName=" + stock.getString("Name") + "&interval=60min'>Show Details</a></td>\r\n"
						+ "</tr>";
			}
			allStockTable += "</tbody></h:form></table>";

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@PostConstruct
	public String addToWatchList() {
		Connection connection;
		Statement statement;
		ResultSet rSet;
		try {
			DBConnection dataConnect = DBConnection.getInstance();
			int userId = (int) this.getRequestParameter("uid");
			String symbol = (String) this.getRequestParameter("selectedSymbol");
			String stockName = (String) this.getRequestParameter("selectedStockName");
			connection = dataConnect.createConnection();
			statement = connection.createStatement();
			rSet = statement.executeQuery(
					"select * from watch_lists where user_id=" + userId + " and stock_symbol='" + symbol + "'");
			if (rSet.next()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Stock Already added to Watch List", ""));
				statement.close();
				rSet.close();
				return "stockDetails";
			} else {
				statement = connection.createStatement();
				statement.executeUpdate("insert into watch_lists (`user_id`,`stock_symbol`,`stock_name`)VALUES("
						+ userId + ",'" + symbol + "','" + stockName + "')");
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Stock added to Watch List", ""));
				statement.close();
				rSet.close();
				connection.close();
				return "userHome";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "listAllStocks";
	}

	public void listWatchList() {
		Connection connection;
		Statement statement;
		ResultSet rSet;
		try {
			DBConnection dataConnect = DBConnection.getInstance();
			int userId = (int) this.getRequestParameter("uid");
			connection = dataConnect.createConnection();
			statement = connection.createStatement();
			rSet = statement.executeQuery("select * from watch_lists where user_id=" + userId);
			watchList = new ArrayList<SelectItem>();
			if (rSet.next()) {
				rSet.previous();
				while (rSet.next()) {
					watchList.add(new SelectItem(rSet.getString("stock_symbol"), rSet.getString("stock_name")));
				}
				availableIntervals = new ArrayList<SelectItem>();
				availableIntervals.add(new SelectItem("1min", "1min"));
				availableIntervals.add(new SelectItem("5min", "5min"));
				availableIntervals.add(new SelectItem("15min", "15min"));
				availableIntervals.add(new SelectItem("30min", "30min"));
				availableIntervals.add(new SelectItem("60min", "60min"));
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "No Stocks added to Watch List", ""));
			}

			statement.close();
			rSet.close();
			connection.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}



}
