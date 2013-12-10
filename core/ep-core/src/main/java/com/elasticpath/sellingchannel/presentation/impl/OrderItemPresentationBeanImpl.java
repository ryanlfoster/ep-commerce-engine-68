/**
 * 
 */
package com.elasticpath.sellingchannel.presentation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.sellingchannel.presentation.OrderItemPresentationBean;

/**
 * Standard implementation of {@code OrderItemDocument}.
 */
@SuppressWarnings({ "PMD.TooManyFields" })
public class OrderItemPresentationBeanImpl implements OrderItemPresentationBean {
	private DigitalAsset digitalAsset;
	private String displayName;
	private String encryptedUidPk;
	private String image;
	private final List<OrderItemPresentationBean> children = new ArrayList<OrderItemPresentationBean>();
	private String displaySkuOptions;
	private Money dollarSavings;
	private Money listPrice;
	private Money unitPrice;
	private Price price;
	private boolean allocated;
	private boolean unitLessThanList;
	private Money total;
	private int level;
	private int quantity;
	private String skuCode;
	private ProductSku productSku;
	private Map<String, String> orderItemFields;
	private final Map<String, Boolean> viewFlags = new HashMap<String, Boolean>();
	private InventoryDto inventoryDto;
	private boolean calculatedBundle;
	private boolean calculatedBundleItem;
	
 	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public int getQuantity() {
		return this.quantity;
	}

	@Override
	public String getSkuCode() {
		return this.skuCode;
	}

	@Override
	public void setLevel(final int level) {
		this.level = level;
	}

	@Override
	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}

	@Override
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	@Override
	public DigitalAsset getDigitalAsset() {
		return digitalAsset;
	}

	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getEncryptedUidPk() {
		return encryptedUidPk;
	}

	@Override
	public String getImage() {
		return image;
	}

	@Override
	public void addChild(final OrderItemPresentationBean child) {
		children.add(child);		
	}

	@Override
	public List<OrderItemPresentationBean> getChildren() {
		return children;
	}

	@Override
	public void setProductSku(final ProductSku productSku) {
		this.productSku = productSku;
	}
	
	@Override
	public ProductSku getProductSku() {
		return this.productSku;
	}

	@Override
	public String getDisplaySkuOptions() {
		return displaySkuOptions;
	}

	@Override
	public Money getDollarSavingsMoney() {
		return dollarSavings;
	}

	@Override
	public Money getListPriceMoney() {
		return listPrice;
	}

	@Override
	public Money getUnitPriceMoney() {
		return unitPrice;
	}

	@Override
	public boolean isAllocated() {
		return allocated;
	}

	@Override
	public boolean isUnitLessThanList() {
		return unitLessThanList;
	}

	@Override
	public void setAllocated(final boolean allocated) {
		this.allocated = allocated;
	}

	@Override
	public void setDigitalAsset(final DigitalAsset digitalAsset) {
		this.digitalAsset = digitalAsset;
	}

	@Override
	public void setDisplaySkuOptions(final String displaySkuOptions) {
		this.displaySkuOptions = displaySkuOptions;
	}

	@Override
	public void setDollarSavingsMoney(final Money dollarSavings) {
		this.dollarSavings = dollarSavings;
	}

	@Override
	public void setEncryptedUidPk(final String encryptedUidPk) {
		this.encryptedUidPk = encryptedUidPk;
	}

	@Override
	public void setListPriceMoney(final Money listPrice) {
		this.listPrice = listPrice;
	}

	@Override
	public void setUnitLessThanList(final boolean unitLessThanList) {
		this.unitLessThanList = unitLessThanList;
	}

	@Override
	public void setUnitPriceMoney(final Money unitPrice) {
		this.unitPrice = unitPrice;
	}

	@Override
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	@Override
	public void setImage(final String image) {
		this.image = image;
	}

	@Override
	public Money getTotalMoney() {
		return total;
	}

	@Override
	public void setTotalMoney(final Money total) {
		this.total = total;
	}

	@Override
	public Map<String, String> getOrderItemFields() {
		return orderItemFields;
	}

	@Override
	public void setOrderItemFields(final Map<String, String> orderItemFields) {
		this.orderItemFields = orderItemFields;
	}

	@Override
	public void addViewFlag(final String flagName, final boolean value) {
		viewFlags.put(flagName, value);
	}

	@Override
	public boolean isViewFlagOn(final String flagName) {
		if (viewFlags.get(flagName) != null) {
			return viewFlags.get(flagName);
		}
		return false;
	}

	@Override
	public InventoryDto getInventory() {
		return inventoryDto;
	}

	@Override
	public void setInventory(final InventoryDto inventoryDto) {
		this.inventoryDto = inventoryDto;
	}

	
	@Override
	public Price getPrice() {
		return price;
	}

	@Override
	public void setPrice(final Price price) {
		this.price = price;
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
	public boolean isCalculatedBundleItem() {
		return calculatedBundleItem;
	}

	@Override
	public void setCalculatedBundleItem(final boolean calculatedBundleItem) {
		this.calculatedBundleItem = calculatedBundleItem;
	}
	
	@Override
	public String getFilteredSkuOptionValues(final Locale locale) {
		FilteredSkuOptionDisplay display = new FilteredSkuOptionDisplay();
		return display.getFilteredSkuDisplay(getProductSku(), locale);
	}
}