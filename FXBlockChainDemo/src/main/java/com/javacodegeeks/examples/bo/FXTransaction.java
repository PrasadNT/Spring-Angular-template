package com.javacodegeeks.examples.bo;

public class FXTransaction {
	
	private String currencyPair;
	private String customerName;
	private String  baseCurrency;
	private double amount;
	
	public FXTransaction() {
		super();
	}
	
	public FXTransaction(String currencyPair, String customerName, String baseCurrency, double amount) {
		super();
		this.currencyPair = currencyPair;
		this.customerName = customerName;
		this.baseCurrency = baseCurrency;
		this.amount = amount;
	}

	public String getCurrencyPair() {
		return currencyPair;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public double getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return "FXTransaction [currencyPair=" + currencyPair + ", customerName=" + customerName + ", baseCurrency="
				+ baseCurrency + ", amount=" + amount + "]";
	}
	
	
	

}
