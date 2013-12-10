package com.elasticpath.sfweb.ajax.bean.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elasticpath.sfweb.ajax.bean.JsonBundleItemBean;
import com.elasticpath.sfweb.ajax.bean.PriceTierBean;

/**
 * 
 * @author mren
 *
 */
public class JsonBundleItemBeanImpl implements JsonBundleItemBean {
	
	private String path = "";
	private String productCode = "";
	private String skuCode = "";

	private int quantity = 0;
	private BigDecimal price = BigDecimal.ZERO;
	private boolean selected = true;
	private int selectionRule = 0;
	private List<JsonBundleItemBean> constituents = new ArrayList<JsonBundleItemBean>();
	private boolean calculatedBundle;
	private boolean calculatedBundleItem;
	
	private List<PriceTierBean> priceTiers = new ArrayList<PriceTierBean>();
	
	private BigDecimal priceAdjustment = BigDecimal.ZERO;
	
	private BigDecimal recurringPrice = BigDecimal.ZERO;
	private String paymentSchedule = "";
	
	private List<PriceTierBean> recurringPriceTiers = new ArrayList<PriceTierBean>();
	private List<AggregatedPrice> aggregatedPrices = new ArrayList<AggregatedPrice>();
	
	@Override
	public String getProductCode() {
		return productCode;
	}

	@Override
	public void setProductCode(final String productCode) {
		this.productCode = productCode;
	}

	/**
	 * Default constructor.
	 */
	public JsonBundleItemBeanImpl() {
		super();
	}

	

	@Override
	public void addConstituent(final JsonBundleItemBean child) {
		constituents.add(child);
	}
	
	@Override
	public List<JsonBundleItemBean> getConstituents() {
		return Collections.unmodifiableList(constituents);
	}

	@Override
	public void setConstituents(final List<JsonBundleItemBean> constituents) {
		this.constituents = constituents;
	}
	
	@Override
	public String getSkuCode() {
		return this.skuCode;
	}
	
	@Override
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	@Override
	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}
	
	@Override
	public int getQuantity() {
		return this.quantity;
	}	

	@Override
	public void setSelected(final boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public int getSelectionRule() {
		return selectionRule;
	}

	@Override
	public void setSelectionRule(final int selectionRule) {
		this.selectionRule = selectionRule;
	}

	@Override
	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public void setPrice(final BigDecimal price) {
		this.price = price;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public void setPath(final String path) {
		this.path = path;
	}

	@Override
	public boolean isCalculatedBundle() {
		return calculatedBundle;
	}

	@Override
	public void setCalculatedBundle(final boolean calculatedBundle) {
		this.calculatedBundle = calculatedBundle;		
	}

	@Override
	public String toString() {
		return "JsonBundleItemDto [skuCode=" + skuCode + "]";
	}

	@Override
	public boolean isCalculatedBundleItem() {
		return calculatedBundleItem;
	}

	@Override
	public void setCalculatedBundleItem(final boolean calculatedItem) {
		this.calculatedBundleItem = calculatedItem;
	}

	@Override
	public List<PriceTierBean> getPriceTiers() {
		return priceTiers;
	}

	@Override
	public void setPriceTiers(final List<PriceTierBean> priceTiers) {
		this.priceTiers = priceTiers;
	}	
	
	@Override
	public BigDecimal getPriceAdjustment() {
		return priceAdjustment;
	}

	@Override
	public void setPriceAdjustment(final BigDecimal adjustmentAmount) {
		this.priceAdjustment = adjustmentAmount;		
	}	
	
	@Override
	public BigDecimal getRecurringPrice() {
		return recurringPrice;
	}

	@Override
	public void setRecurringPrice(final BigDecimal recurringPrice) {
		this.recurringPrice = recurringPrice;
	}

	@Override
	public String getPaymentSchedule() {
		return paymentSchedule;
	}

	@Override
	public void setPaymentSchedule(final String paymentSchedule) {
		this.paymentSchedule = paymentSchedule;
	}
	
	/**
	 * @return the recurringPriceTiers
	 */
	public List<PriceTierBean> getRecurringPriceTiers() {
		return recurringPriceTiers;
	}

	@Override
	public void setRecurringPriceTiers(final List<PriceTierBean> priceTiers) {
		this.recurringPriceTiers = priceTiers;
	}
	
	@Override
	public List<AggregatedPrice> getAggregatedPrices() {
		return aggregatedPrices;
	}

	@Override
	public void setAggregatedPrices(final List<AggregatedPrice> aggregatedPrices2) {
		this.aggregatedPrices = aggregatedPrices2;
	}
}
