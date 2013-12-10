package com.elasticpath.sfweb.ajax.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.sf.ezmorph.Morpher;
import net.sf.ezmorph.MorpherRegistry;
import net.sf.ezmorph.bean.BeanMorpher;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.sfweb.ajax.bean.JsonBundleItemBean;
import com.elasticpath.sfweb.ajax.bean.impl.JsonBundleItemBeanImpl;
import com.elasticpath.sfweb.ajax.service.JsonBundleFactory;
import com.elasticpath.sfweb.formbean.ShoppingItemFormBean;

/**
 * Factory to create JsonBundleItemDto tree from bundle and shopping item.
 * @author mren
 *
 */
public class JsonBundleFactoryImpl implements JsonBundleFactory {
	
	private static final Logger LOG = Logger.getLogger(JsonBundleFactoryImpl.class);
	
	private PriceBuilder priceBuilder;
	private BeanFactory beanFactory;
	
	@Override
	public JsonBundleItemBean createJsonBundleFromShoppingItemFormBean(final ShoppingItemFormBean rootShoppingItem) {
		JsonBundleItemBean rootJsonBundleitemDto = createJsonBundleInternal(rootShoppingItem);
		// stack to remember parent of the current bundle item. 
		Stack<Pair<JsonBundleItemBean, ShoppingItemFormBean>> treepath = new Stack<Pair<JsonBundleItemBean, ShoppingItemFormBean>>();		
		// current level of bundle tree
		int currentlevel = 1;		
		// last visited bundle item, here is using a Pair to remember both converted 
		// JsonBundleItemBean and the original ShoppingItemFormBean
		Pair<JsonBundleItemBean, ShoppingItemFormBean> lastVisited = 
			new Pair<JsonBundleItemBean, ShoppingItemFormBean>(rootJsonBundleitemDto, rootShoppingItem);
		treepath.push(lastVisited);
		
		for (ShoppingItemFormBean shoppingItem : rootShoppingItem.getConstituents()) {
			// create a JsonBundleItemBean and populate properties. 
			JsonBundleItemBeanImpl currentJsonBundleItem = createJsonBundleInternal(shoppingItem);
			
			if (shoppingItem.getLevel() > currentlevel) {
				// we are going to a deeper level
				treepath.push(lastVisited);				
			} else if (shoppingItem.getLevel() < currentlevel) {
				// we are coming back from deeper level. We shall pop until the peek of stack is the right parent.
				Pair<JsonBundleItemBean, ShoppingItemFormBean> poppedItem = treepath.pop();
				while (poppedItem != null && poppedItem.getSecond().getLevel() > shoppingItem.getLevel()) {
					poppedItem = treepath.pop();
				}
				
			}
			// add to parent item.
			treepath.peek().getFirst().addConstituent(currentJsonBundleItem);
			// update traverse information.
			currentlevel = shoppingItem.getLevel();						
			lastVisited = new Pair<JsonBundleItemBean, ShoppingItemFormBean>(currentJsonBundleItem, shoppingItem);
		}
		
		return rootJsonBundleitemDto;
	}
	/**
	 * Convert shopping item form bean into JsonBundleItemDto.
	 * @param currentShoppingItem the current shopping item
	 * @return JsonBundleItemDto a dump of bundle.
	 */
	protected JsonBundleItemBeanImpl createJsonBundleInternal(final ShoppingItemFormBean currentShoppingItem) {
		
		JsonBundleItemBeanImpl bundleDto = beanFactory.getBean(ContextIdNames.JSON_BUNDLE);
		
		bundleDto.setSkuCode(currentShoppingItem.getSkuCode());
		
		bundleDto.setQuantity(currentShoppingItem.getQuantity());
		
		bundleDto.setPath(currentShoppingItem.getPath());
		
		setPriceAndPriceTiers(currentShoppingItem, bundleDto);
		
		
		if (currentShoppingItem.getProduct() != null) {
			bundleDto.setProductCode(currentShoppingItem.getProduct().getCode());
		}
		
		
		bundleDto.setSelected(currentShoppingItem.isSelected());
		bundleDto.setCalculatedBundle(currentShoppingItem.isCalculatedBundle());
		bundleDto.setCalculatedBundleItem(currentShoppingItem.isCalculatedBundleItem());
		bundleDto.setSelectionRule(currentShoppingItem.getSelectionRule());
		
		setPriceAdjustment(currentShoppingItem, bundleDto);
		
		return bundleDto;
	}
	
	

	/**
	 * Set the bundle price and price tiers based on the current shopping item price.
	 * 
	 * @param currentShoppingItem the shopping item
	 * @param bundleDto the bundle dto
	 */
	protected void setPriceAndPriceTiers(
			final ShoppingItemFormBean currentShoppingItem,
			final JsonBundleItemBean bundleDto) {
		
		Price itemPrice = currentShoppingItem.getPrice();
		
		if (itemPrice != null) {
			
			Money lowestPrice = itemPrice.getLowestPrice(currentShoppingItem.getQuantity());
			
			if (lowestPrice != null) {
				
				bundleDto.setPrice(lowestPrice.getAmount());
			}
			
			bundleDto.setPriceTiers(getPriceBuilder().getPriceTiers(itemPrice));
		}
	}
	
	
	/**
	 * Set the price adjustment amount on the bundle dto.
	 * 
	 * @param currentShoppingItem the shopping item
	 * @param bundleDto the bundle dto
	 */
	protected void setPriceAdjustment(
			final ShoppingItemFormBean currentShoppingItem,
			final JsonBundleItemBean bundleDto) {
		
		PriceAdjustment priceAdjustment = currentShoppingItem.getPriceAdjustment();
		
		if (priceAdjustment != null && priceAdjustment.getAdjustmentAmount() != null) {
			
			bundleDto.setPriceAdjustment(priceAdjustment.getAdjustmentAmount());
		}
	}
	
	@Override
	public String serialize(final JsonBundleItemBean jsonBundleDto) {
		JSONObject jsonObject = JSONObject.fromObject(jsonBundleDto);
		return jsonObject.toString();
	}
	
	@Override
	public JsonBundleItemBean deserialize(final String bundleText, final Class<? extends JsonBundleItemBean> jsonBundleClass) {		
		JSONObject jsonObject = JSONObject.fromObject(bundleText);
		Object bean = JSONObject.toBean(jsonObject);  
		
		MorpherRegistry morpherRegistry = JSONUtils.getMorpherRegistry();
	    Morpher dynaMorpher = new BeanMorpher(jsonBundleClass, morpherRegistry);  
	    morpherRegistry.registerMorpher(dynaMorpher);  
	    JsonBundleItemBean backBean = (JsonBundleItemBean) morpherRegistry.morph(jsonBundleClass, bean);
	    
	    try {
			Object[] contituents = ((List<?>) PropertyUtils.getProperty(bean, "constituents")).toArray();
			
	    	assembleConstituents(backBean, contituents, morpherRegistry, jsonBundleClass);
		
	    } catch (Exception e) {
			
	    	LOG.error(e.getMessage(), e);
		}
	    
		return backBean;
	}
	
	/**
	 * 
	 * @param currentItem current bean to check.
	 * @param contituents constituent items of this bean.
	 * @param morpherRegistry
	 */
	private void assembleConstituents(final JsonBundleItemBean currentItem, final Object[] contituents,
			final MorpherRegistry morpherRegistry, final Class<? extends JsonBundleItemBean> jsonBundleClass) {
		List<JsonBundleItemBean> rebuiltConstituents = new ArrayList<JsonBundleItemBean>();
		for (Object item : contituents) {
			
			JsonBundleItemBeanImpl itemBean = (JsonBundleItemBeanImpl) morpherRegistry.morph(jsonBundleClass, item);
			
			try {
				
				assembleConstituents(itemBean, ((List<?>) PropertyUtils.getProperty(itemBean, "constituents")).toArray(),
						morpherRegistry, jsonBundleClass);
			
			} catch (Exception e) {
				
				LOG.error(e.getMessage(), e);
			}
			
			rebuiltConstituents.add(itemBean);			
		}
		currentItem.setConstituents(rebuiltConstituents);
	}
	
	
	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the beanFactory
	 */
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setPriceBuilder(final PriceBuilder priceBuilder) {
		this.priceBuilder = priceBuilder;
	}

	protected PriceBuilder getPriceBuilder() {
		return priceBuilder;
	}
}
