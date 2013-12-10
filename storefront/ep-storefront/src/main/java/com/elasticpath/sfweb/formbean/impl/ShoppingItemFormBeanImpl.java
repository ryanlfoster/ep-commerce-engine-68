package com.elasticpath.sfweb.formbean.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.sellingchannel.presentation.impl.FilteredSkuOptionDisplay;
import com.elasticpath.sfweb.formbean.GiftCertificateFormBean;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;

/**
 * Form bean for collecting information from the ShoppingCartController.
 * Note that only quantity is required at present because shipping estimation and
 * code application use different mechanisms.
 */
public class ShoppingItemFormBeanImpl implements ShoppingItemFormBean {

	private static final long serialVersionUID = 1L;

	private int quantity;

	private boolean dependent;

	private StoreProduct storeProduct;

	private int level;

	private String skuCode;

	private long updateShoppingItemUid;

	private String path;

	private ProductSku productSku;

	private Price price;

	private Money total;

	private boolean fixedSku;

	private boolean calculatedBundle;

	private boolean calculatedBundleItem;

	@SuppressWarnings("unchecked")
	private final List<ShoppingItemFormBean> constituents = LazyList.decorate(
			new ArrayList<ShoppingItemFormBean>(), FactoryUtils
			.instantiateFactory(ShoppingItemFormBeanImpl.class));

	private boolean selected;

	private final GiftCertificateFormBean giftCertificateFormBean = new GiftCertificateFormBeanImpl();

	private PriceAdjustment priceAdjustment;

	private int selectionRule;

	private ShoppingItemFormBean parent;

	private int minQty;



	/**
	 * Default constructor.
	 */
	public ShoppingItemFormBeanImpl() {
		minQty = 1;
		quantity = 0;
		path = "";
		updateShoppingItemUid = 0;
	}

	/**
	 * Sets the quantity.
	 * @param quantity The quantity to set.
	 */
	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}

	@Override
	public boolean isDependent() {
		return dependent;
	}

	@Override
	public void setDependent(final boolean dependent) {
		this.dependent = dependent;
	}

	@Override
	public int getQuantity() {
		return quantity;
	}

	@Override
	public void addConstituent(final ShoppingItemFormBean constituentFormBean) {
		constituents.add(constituentFormBean);
	}

	@Override
	public List<ShoppingItemFormBean> getConstituents() {
		return Collections.unmodifiableList(constituents);
	}

	@Override
	public StoreProduct getProduct() {
		return storeProduct;
	}

	@Override
	public void setProduct(final StoreProduct product) {
		storeProduct = product;
	}

	@Override
	public void setPath(final String path) {
		this.path = path;
	}

	@Override
	public void setProductSku(final ProductSku productSku) {
		this.productSku = productSku;
	}

	@Override
	public void setLevel(final int level) {
		this.level = level;
	}

	@Override
	public int getLevel() {
		return level;
	}

	@Override
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	@Override
	public String getSkuCode() {
		return skuCode;
	}

	@Override
	public boolean isForUpdate() {
		return updateShoppingItemUid != 0;
	}

	@Override
	public long getUpdateShoppingItemUid() {
		return updateShoppingItemUid;
	}

	@Override
	public void setUpdateShoppingItemUid(final long updateCartItemUid) {
		updateShoppingItemUid = updateCartItemUid;
	}

	@Override
	public String getPath() {
		return path;
	}


	@Override
	public ProductSku getProductSku() {
		return productSku;
	}

	@Override
	public Price getPrice() {
		return price;
	}

	@Override
	public Money getTotal() {
		return total;
	}

	@Override
	public void setPrice(final Price price) {
		this.price = price;
	}

	@Override
	public void setTotal(final Money total) {
		this.total = total;
	}


	/**
	 * 
	 * @param selected if true then this item has been selected by the user.
	 */
	public void setSelected(final boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return true if this item has been selected by the user.
	 */
	public boolean isSelected() {
		return selected;
	}

	@Override
	public GiftCertificateFormBean getGiftCertificateFields() {
		return giftCertificateFormBean;
	}

	@Override
	public PriceAdjustment getPriceAdjustment() {
		return priceAdjustment;
	}

	@Override
	public void setPriceAdjustment(final PriceAdjustment priceAdjustment) {
		this.priceAdjustment = priceAdjustment;
	}

	@Override
	public boolean isFixedSku() {
		return fixedSku;
	}

	@Override
	public void setFixedSku(final boolean fixedSku) {
		this.fixedSku = fixedSku;
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
	public int getSelectionRule() {
		return selectionRule;
	}

	@Override
	public void setSelectionRule(final int selectionRule) {
		this.selectionRule = selectionRule;
	}

	/**
	 * @return the parent
	 */
	public ShoppingItemFormBean getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(final ShoppingItemFormBean parent) {
		this.parent = parent;
	}

	/**
	 * 
	 * @return the quantity accumulated from the ancestors til root.
	 */
	public int getAccumulatedQuantity() {
		int quantity = getQuantity();
		ShoppingItemFormBean parentItem = getParent();
		while (parentItem != null) {
			quantity *= parentItem.getQuantity();
			parentItem = parentItem.getParent();
		}
		return quantity;
	}

	@Override
	public int getMinQty() {
		return minQty;
	}

	@Override
	public void setMinQty(final int qty) {
		minQty = qty;
	}

	@Override
	public String getFilteredSkuOptionValues(final Locale locale) {
		final FilteredSkuOptionDisplay display = new FilteredSkuOptionDisplay();
		return display.getFilteredSkuDisplay(getProductSku(), locale);
	}
}