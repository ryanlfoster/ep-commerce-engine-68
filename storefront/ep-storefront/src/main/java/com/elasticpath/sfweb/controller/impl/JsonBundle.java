package com.elasticpath.sfweb.controller.impl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.catalog.impl.BundleIteratorImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;



/**
 * 
 * Provide a JSON 'object' that includes bundle pricing for use on the client.
 * 
 * Override any of the protected methods to change the contents of the returned JSON string.
 * 
 * @author shallinan
 *
 */
public class JsonBundle {
	
	private static final String CLOSING_CONSTITUENT = "]}";

	private static final String COMMA = ",";
	
	private static final String CONSTITUENT_OPEN = ", 'constituents' : [";

	private ProductBundle bundle;

	private ShoppingItemFormBean formBean;
	
	private SortedSet<Integer> priceTierLevels;
	
	private ShoppingCart shoppingCart;
	
	private PriceLookupFacade priceLookupFacade;
	
	private ProductSkuService productSkuService;
	


	/**
	 * Set the bundle.
	 * 
	 * @param bundle the bundle
	 */
	public void setBundle(final ProductBundle bundle) {
		this.bundle = bundle;
	}


	/**
	 * 
	 * Set the form bean.
	 * 
	 * @param formBean the formbean
	 */
	public void setFormBean(final ShoppingItemFormBean formBean) {
		this.formBean = formBean;
	}


	/**
	 * Convenience inner class for use with an integer out parameter.
	 * 
	 * 
	 * 
	 *
	 */
	public final class IntHolder {
	     
		private int value;
	     
		/**
		 * 
		 * @return the value
		 */
	    public int getValue() {
			return value;
		}

	    /**
	     * 
	     * @param value the value
	     */
		public void setValue(final int value) {
			this.value = value;
		}

		/**
		 * 
		 * @param initial the initial value
		 */
		public IntHolder(final int initial) {
	        value = initial;
	    }
	    
	    
	    
	  }
	
	
	/**
	 * 
	 * Transform the bundle and form bean objects into a json usable string.
	 * 
	 * 
	 * 
	 * 
	 * @return the object as a json string
	 */
	public String toJsonString() {
		
		// re-instantiate the price tier levels
		
		priceTierLevels = new TreeSet<Integer>();
		
		StringBuffer buffer = new StringBuffer("{ ");		
		
		appendSelectionRule(buffer, bundle);
		
		appendIsCalculatedBundle(buffer, bundle);
		
		buffer.append(CONSTITUENT_OPEN);
			
		int index = 0;
		
		String previousPath = StringUtils.EMPTY;
	
		Iterable<BundleConstituent> iterable = new BundleIteratorImpl(bundle);
		
		
		IntHolder intHolder = new IntHolder(index);
		
		for (BundleConstituent bundleConstituent : iterable) {
			
			previousPath = processBundleConstituent(bundleConstituent, intHolder, buffer, previousPath);

		}

		index = intHolder.getValue();
		
		if (index != 0) {
			
			addClosing(buffer, index);
		}
		
		closeConstituents(buffer);
		
		return buffer.toString().replace(" ", "");
	}
	
	
	/**
	 * Append a clause to indicate if the bundle is calculated or not. 
	 * 
	 * @param buffer the string buffer
	 * @param bundle the bundle
	 */
	protected void appendIsCalculatedBundle(final StringBuffer buffer, final ProductBundle bundle) {
		
		
		buffer.append(", 'isCalculatedBundle' : ");
		
		if (bundle.isCalculated() == null) {
			buffer.append(false);
		} else {
			buffer.append(bundle.isCalculated());
		}
		
		
	}


	/**
	 * Append the contents for the bundle constituent.
	 * 
	 * @param previousPath the previous path value
	 * @param buffer the string buffer for the json string
	 * @param intHolder the holder of the index value
	 * @param bundleConstituent the bundle constituent
	 * @return the updated path value
	 */
	protected String processBundleConstituent(final BundleConstituent bundleConstituent, final IntHolder intHolder, 
			final StringBuffer buffer, final String previousPath) {
		
	
		ShoppingItemFormBean shoppingItemFormBean = formBean.getConstituents().get(intHolder.getValue());
	
		intHolder.setValue(intHolder.getValue() + 1);
		
		
		
		String path = shoppingItemFormBean.getPath();
		
		determineIfConstituentsOrLevelShouldBeClosed(previousPath, path, buffer);

		appendSelectionSetting(shoppingItemFormBean, path, buffer);
		
		appendSkuPriceAndQuantity(shoppingItemFormBean, bundleConstituent, buffer);
		
		appendAdditionalConstituentInformation(shoppingItemFormBean, buffer);
		
		appendPriceAdjustment(shoppingItemFormBean, buffer);
		
		if (bundleConstituent.getConstituent().isBundle()) {
			
			buffer.append(COMMA);
			
			appendSelectionRule(buffer, (ProductBundle) bundleConstituent.getConstituent().getProduct());
		}
		
		buffer.append(CONSTITUENT_OPEN);
		
		
		// return the updated previous path.
		return path;
	
		
	}

	/**
	 * 
	 * Override this method to embed additional information within the json string.
	 * 
	 * @param shoppingItemFormBean the form bean. 
	 * @param buffer the string buffer.
	 */
	protected void appendAdditionalConstituentInformation(final ShoppingItemFormBean shoppingItemFormBean, final StringBuffer buffer) {
		// do nothing
		
	}


	/**
	 * 
	 * Append the sku price and quantity, for a calculated bundle item.
	 * 
	 * If a constituent item is not a bundle, then it is a product or a fixed sku, and we will append the price and quantity.
	 * 
	 * @param shoppingItemFormBean the form bean.
	 * @param bundleConstituent the bundle constituent 
	 * @param buffer the string buffer.
	 */
	protected void appendSkuPriceAndQuantity(final ShoppingItemFormBean shoppingItemFormBean, 
			final BundleConstituent bundleConstituent, final StringBuffer buffer) {
	
		if (shoppingItemFormBean.isCalculatedBundleItem()) {

			ConstituentItem constituentItem = bundleConstituent.getConstituent();
			
			if (!constituentItem.isBundle()) { // then it is a sku or a product
				
				appendSkuPrice(shoppingItemFormBean, buffer);
				
				int quantity = shoppingItemFormBean.getQuantity();
				
				String quantityStr = ", 'quantity' : " + quantity;
				
				buffer.append(quantityStr);
				
				
			}
			
		
		}
		
	
	}


	/**
	 * Append the sku price, which is the lowest price of the price attached to the form bean.
	 * 
	 * @param shoppingItemFormBean the form bean
	 * @param buffer the json buffer
	 */
	@SuppressWarnings("deprecation")
	protected void appendSkuPrice(final ShoppingItemFormBean shoppingItemFormBean, final StringBuffer buffer) {
		

		Price itemPrice = null;
		
		if (productSkuService == null) {
			
			itemPrice = shoppingItemFormBean.getPrice();
			
			
		
		} else {
			
			final String skuCode = shoppingItemFormBean.getSkuCode();
			final ProductSku productSku = productSkuService.findBySkuCode(skuCode);

			final Store store = shoppingCart.getStore();
			final Shopper shopper = shoppingCart.getShopper();
			
			final Set<Long> ruleTracker = shoppingCart.getAppliedRules();

			itemPrice = priceLookupFacade.getPromotedPriceForSku(productSku, store, shopper, ruleTracker);
			
		}
		
		
		
		if (itemPrice != null) {
			
			Money lowestPrice = itemPrice.getLowestPrice(shoppingItemFormBean.getQuantity());
			
			if (lowestPrice != null) {
				
				BigDecimal amount = lowestPrice.getAmount();
				
				String priceStr = ", 'price' : " + amount;
				
				buffer.append(priceStr);
			}
			
			// now append the price tiers
			
			Map<Integer, PriceTier> priceTiers = itemPrice.getPriceTiers();
			
			Set<Integer> keySet = priceTiers.keySet();
			
			for (Integer priceTierQuantity : keySet) {
				
				// add the quantity to the price tier levels set.
				priceTierLevels.add(priceTierQuantity);
				
				PriceTier tier = priceTiers.get(priceTierQuantity);
				
				BigDecimal priceTierAmount = tier.getLowestPrice();
				
				String priceTierStr = ", 'priceTier" + priceTierQuantity + "' : " + priceTierAmount;
				
				buffer.append(priceTierStr);
			}
			
			
		}
	}


	/**
	 * 
	 * Append the price adjustment.
	 * 
	 * @param shoppingItemFormBean the form bean. 
	 * @param buffer the string buffer.
	 */
	protected void appendPriceAdjustment(final ShoppingItemFormBean shoppingItemFormBean, final StringBuffer buffer) {
		if (shoppingItemFormBean.getPriceAdjustment() != null) {
			String adjStr = ", 'adjustment' : " + shoppingItemFormBean.getPriceAdjustment().getAdjustmentAmount();
			buffer.append(adjStr);
		}
	}
	
	
	/**
	 * 
	 * Append the selection setting.
	 * 
	 * @param shoppingItemFormBean the shopping item form bean
	 * @param path the path value
	 * @param buffer the json buffer
	 */
	protected void appendSelectionSetting(final ShoppingItemFormBean shoppingItemFormBean, 
			final String path, final StringBuffer buffer) {
		
		buffer.append(" { 'path' : '" + path + "','selected' : " + shoppingItemFormBean.isSelected());
		
	}
	
	
	/**
	 * 
	 * Append the selection rule.
	 * 
	 * @param buffer the buffer object
	 * @param bundle the bundle object
	 */
	protected void appendSelectionRule(final StringBuffer buffer, final ProductBundle bundle) {
		SelectionRule selectionRule = bundle.getSelectionRule();
		int ruleParam = 0;
		if (selectionRule != null) {
			ruleParam = selectionRule.getParameter();
		}
		buffer.append("'selectionRule' : ");
		buffer.append(ruleParam);
	}


	private void determineIfConstituentsOrLevelShouldBeClosed(final String previousPath, final String path, final StringBuffer buffer) {
		
		if (!isSteppingIn(previousPath, path) && !isSteppingOut(previousPath, path)) {
			closeConstituents(buffer);
			buffer.append(COMMA);
		}
		
		if (isSteppingOut(previousPath, path)) {
			closeLevel(buffer, previousPath, path);
		}
		
	}


	private void closeLevel(final StringBuffer buffer, final String previousPath, final String path) {
		int pathsDifference = StringUtils.countMatches(previousPath, "[") - StringUtils.countMatches(path, "[");
		while (pathsDifference-- > -1) {
			closeConstituents(buffer);
		}
		buffer.append(COMMA);
	} 
	
	/*
	 * Close number of times according to level of nesting.
	 */
	private void addClosing(final StringBuffer buffer, final int index) {
		String path = formBean.getConstituents().get(index - 1).getPath();
		int pathLength = StringUtils.countMatches(path, "[");
		while (pathLength-- > 0) {
			closeConstituents(buffer);
		}
	}

	
	
	private void closeConstituents(final StringBuffer buffer) {
		buffer.append(CLOSING_CONSTITUENT);
	}

	/*
	 * Determine if a new entry is being created.
	 * 
	 */
	private boolean isSteppingIn(final String previousPath, final String path) {
		if (StringUtils.isEmpty(previousPath)) {
			return true;
		}
		int lastIndexOf = path.lastIndexOf('[');
		int lastIndexOf2 = previousPath.lastIndexOf('[');
		return path.substring(0, lastIndexOf).length() > previousPath.substring(0, lastIndexOf2).length();
	}

	
	/*
	 * Determine if a new entry is being closed out.
	 * 
	 */
	private boolean isSteppingOut(final String previousPath, final String path) {
		if (StringUtils.isEmpty(previousPath)) {
			return false;
		}
		int lastIndexOf = path.lastIndexOf('[');
		int lastIndexOf2 = previousPath.lastIndexOf('[');
		return path.substring(0, lastIndexOf).length() < previousPath.substring(0, lastIndexOf2).length();
	}
	

	/**
	 * 
	 * @return the closing constituent clause
	 */
	public static String getClosingConstituent() {
		return CLOSING_CONSTITUENT;
	}


	/**
	 * 
	 * @return a comma.
	 */
	public static String getComma() {
		return COMMA;
	}


	/**
	 * 
	 * @return the constituent open clause
	 */
	public static String getConstituentOpen() {
		return CONSTITUENT_OPEN;
	}


	/**
	 * 
	 * @return the bundle
	 */
	public ProductBundle getBundle() {
		return bundle;
	}


	@Override
	public String toString() {
		
		return this.toJsonString();
	}


	/**
	 * 
	 * @return the form bean
	 */
	public ShoppingItemFormBean getFormBean() {
		return formBean;
	}
	
	/**
	 * 
	 * @return the price tier levels
	 */
	public Set<Integer> getPriceTierLevels() {
		return priceTierLevels;
	}


	/**
	 * 
	 * @return the shopping cart
	 */
	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}


	/**
	 * 
	 * @param shoppingCart the shopping cart
	 */
	public void setShoppingCart(final ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
	}


	/**
	 * 
	 * @return the price look up facade
	 */
	public PriceLookupFacade getPriceLookupFacade() {
		return priceLookupFacade;
	}


	/**
	 * 
	 * @param priceLookupFacade the price lookup facade
	 */
	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}


	/**
	 * 
	 * @return the product sku service
	 */
	public ProductSkuService getProductSkuService() {
		return productSkuService;
	}


	/**
	 * 
	 * @param productSkuService the product sku service
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

}
