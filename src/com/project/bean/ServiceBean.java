package com.project.bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

@ManagedBean(name = "serviceBean")
@SessionScoped
public class ServiceBean {
	public String getListOfStock() {
		return "listAllStocks";
	}

	public String getAccountDetails() {
		return "account";
	}

	public String getUpdateProfile() {
		return "updateprofile";
	}

	public String getContactManager() {
		return "contactManager";
	}

	public String getUserHome() {
		return "userHome";
	}

	public String getSelectManager() {
		return "selectManager";
	}

	public static void setSessionMessage(String message) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(message));
	}

	public static Object getRequestParameter(String name) {
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(name);
	}

	public String getHome() {
		return "userHome?faces-redirect=true";
	}

	public String getStockSell() {
		return "sellStock";
	}

}
