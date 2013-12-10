package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;
import java.util.Currency;

import org.apache.commons.lang.StringUtils;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceScheduleImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.impl.ShoppingItemRecurringPriceAssemblerImpl;

/**
 * Test cases for <code>ShoppingCartDiscountItemContainerImpl</code>.
 */
public class ShoppingCartDiscountItemContainerImplTest {
	
	private static final long ACTION_UID = 123L;
	private static final long RULE_UID = 456L;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private static final BigDecimal BIG_D_30 = new BigDecimal(30);
	private static final BigDecimal BIG_D_20 = new BigDecimal(20);

	
	/**
	 * Test case for DiscountItemContainer.recordRuleApplied(ruleId) method.
	 */
	@Test
	public void testDiscountItemContainerRecordRuleApplied() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final long ruleId = 1L;
		context.checking(new Expectations() {
			{
				oneOf(shoppingCart).ruleApplied(ruleId, 0, null, null, 0);
			}
		});
				
		ShoppingCartDiscountItemContainerImpl container = 
			new ShoppingCartDiscountItemContainerImpl();
		container.setShoppingCart(shoppingCart);
		container.recordRuleApplied(ruleId, 0L, null, null, 0);		
		
	}
	
	/**
	 * Test case for DiscountItemContainer.applySubtotalDiscount(discountAmount) method.
	 */
	@Test
	public void testDiscountItemContainerApplySubtotalDiscount() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final BigDecimal discountAmount = BigDecimal.TEN;
		context.checking(new Expectations() {
			{
				oneOf(shoppingCart).setSubtotalDiscount(discountAmount, RULE_UID, ACTION_UID);
			}
		});
				
		ShoppingCartDiscountItemContainerImpl container = 
			new ShoppingCartDiscountItemContainerImpl();
		container.setShoppingCart(shoppingCart);
		container.applySubtotalDiscount(discountAmount, RULE_UID, ACTION_UID);		
		
	}

	/**
	 * Test case for DiscountItemContainer.getCatalog() method.
	 */
	@Test
	public void testDiscountItemGetCatalog() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Store store = context.mock(Store.class);
		final Catalog catalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				oneOf(shoppingCart).getStore();
				will(returnValue(store));
				
				oneOf(store).getCatalog();
				will(returnValue(catalog));
			}
		});
				
		ShoppingCartDiscountItemContainerImpl container = 
			new ShoppingCartDiscountItemContainerImpl();
		container.setShoppingCart(shoppingCart);
		Catalog catalog2 = container.getCatalog();
		Assert.assertEquals("catalog returned does not match expectation", catalog, catalog2);
	}
	
	/**
	 * Test case for DiscountItemContainer.addCartItem(skuCode, numItems) method.

	@Test
	public void testDiscountItemContainerAddCartItem() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Store store = context.mock(Store.class);
		final String skuCode = "skuCode";
		final int numItems = 2;
		final ElasticPath elasticPath = context.mock(ElasticPath.class);
		final ShoppingItem cartItem = context.mock(ShoppingItem.class);
		final CartDirector director = context.mock(CartDirector.class);
		final List<ShoppingItem> list = new ArrayList<ShoppingItem>();
		list.add(cartItem);
		
		
		context.checking(new Expectations() {
			{
				oneOf(shoppingCart).getCartItem(skuCode);
				will(returnValue(null));
				oneOf(shoppingCart).isCartItemRemoved(skuCode);
				will(returnValue(false));
				
				oneOf(shoppingCart).getStore();
				will(returnValue(store));

				oneOf(elasticPath).getBean("cartDirector");
				will(returnValue(director));

				oneOf(shoppingCart).getCustomerSession();
				will(returnValue(null));
				
				oneOf(shoppingCart).getAppliedRules();
				will(returnValue(null));
				
				//allowing(director).getElasticPath().getBeanFactory("shoppingItemDtoFactory");
			}
		});
				
		ShoppingCartDiscountItemContainerImpl container = 
			new ShoppingCartDiscountItemContainerImpl() {
			@Override
			public ElasticPath getElasticPath() {
				return elasticPath;
			}
		};
		container.setShoppingCart(shoppingCart);
		ShoppingItem item2 = container.addCartItem(skuCode, numItems);
		Assert.assertEquals("cart item added does not match expected value", cartItem, item2);

	}
*/
	
	/**
	 * Test case for DiscountItemContainer.getCartItemProductCode(cartItem) and getCartItemSkuCode(cartItem) method.
	 */
	@Test
	public void testDiscountItemGetCartItemCode() {
		final String skuCode = "skuCode";
		final String productCode = "productCode";
		final ShoppingItem cartItem = context.mock(ShoppingItem.class);
		final ProductSku sku = context.mock(ProductSku.class);
		final Product product = context.mock(Product.class);
		context.checking(new Expectations() {
			{
				oneOf(cartItem).getProductSku();
				will(returnValue(sku));				
				oneOf(sku).getSkuCode();
				will(returnValue(skuCode));
				
				oneOf(cartItem).getProductSku();
				will(returnValue(sku));				
				oneOf(sku).getProduct();
				will(returnValue(product));
				oneOf(product).getCode();
				will(returnValue(productCode));
			}
		});
				
		ShoppingCartDiscountItemContainerImpl container = 
			new ShoppingCartDiscountItemContainerImpl();
		
		String code = container.getCartItemSkuCode(cartItem);		
		Assert.assertEquals("sku code does not match expected value", skuCode, code);		
		code = container.getCartItemProductCode(cartItem);
		Assert.assertEquals("product code does not match expected value", productCode, code);		

	}
	
	/**
	 * Test case for DiscountItemContainer.getPriceAmount(cartItem) method.
	 */
	@Test
	public void testDiscountItemGetPriceAmount() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Money money = context.mock(Money.class);		
		final ShoppingItem cartItem = context.mock(ShoppingItem.class);

		context.checking(new Expectations() {
			{
				allowing(money).getAmount(); will(returnValue(BigDecimal.TEN));
				oneOf(cartItem).getTotal();	will(returnValue(money));
				oneOf(cartItem).getQuantity(); will(returnValue(1));
			}
		});
				
		ShoppingCartDiscountItemContainerImpl container = new ShoppingCartDiscountItemContainerImpl();
		container.setShoppingCart(shoppingCart);
		
		BigDecimal amount2 = container.getPriceAmount(cartItem);
		Assert.assertEquals("price amount code does not match expected value", BigDecimal.TEN.compareTo(amount2), 0);

	}
	
	/**
	 * Test case for DiscountItemContainer.getPriceAmount(cartItem) method when a discount is set.
	 */
	@Test
	public void testGetPriceAmountWithDiscountApplied() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final ShoppingCartDiscountItemContainerImpl container = new ShoppingCartDiscountItemContainerImpl();
		container.setShoppingCart(shoppingCart);
		final ShoppingItem cartItem = createShoppingItem();
		
		cartItem.setPrice(1, createPrice(BIG_D_30, BIG_D_20, Currency.getInstance("USD"), 1));
		cartItem.applyDiscount(BigDecimal.TEN);
	
		Assert.assertEquals("container.getPriceAmount(cartItem) does not match expected value after discount applied", 
				BigDecimal.TEN.compareTo(container.getPriceAmount(cartItem)), 0);
	}
	
	/**
	 * Test case for DiscountItemContainer.getPriceAmount(cartItem) method when no discount is set.
	 */
	@Test
	public void testGetPriceAmountWithNoDiscountApplied() {
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final ShoppingCartDiscountItemContainerImpl container = new ShoppingCartDiscountItemContainerImpl();
		container.setShoppingCart(shoppingCart);
		final ShoppingItem cartItem = createShoppingItem();
		
		cartItem.setPrice(1, createPrice(BIG_D_30, BIG_D_20, Currency.getInstance("USD"), 1));
		
		Assert.assertEquals("container.getPriceAmount(cartItem) does not match expected value", 
				BIG_D_20.compareTo(container.getPriceAmount(cartItem)), 0);
	}
	
	/**
	 * Creates a new <code>Price</code> using the passed in data.
	 *
	 * @param listPrice listPrice
	 * @param salePrice salePrice
	 * @param currency currency
	 * @param quantity quantity
	 * @return the created Price
	 */
	private Price createPrice(final BigDecimal listPrice, final BigDecimal salePrice, final Currency currency, final int quantity) {
		final Price price = new PriceImpl();
		price.setCurrency(currency);
		price.setListPrice(MoneyFactory.createMoney(listPrice, currency), quantity);
		price.setSalePrice(MoneyFactory.createMoney(salePrice, currency), quantity);
		
		return price;
	}
	
	/**
	 * Creates a <code>ShoppingItemImpl</code> for test purposes.
	 * Overwrites getBean to remove dependency on ElasticPathImpl
	 *
	 * @return the <code>ShoppingItemImpl</code>
	 */
	private ShoppingItem createShoppingItem() {
		return new ShoppingItemImpl() {
			private static final long serialVersionUID = 4012495566232629310L;

			@SuppressWarnings("unchecked")
			protected Object getBean(final String beanName) {
				if (StringUtils.equals(beanName, ContextIdNames.PRICE)) {
					return new PriceImpl();
				} else if (StringUtils.equals(beanName, ContextIdNames.PRICE_SCHEDULE)) {
					return new PriceScheduleImpl();
				} else if (StringUtils.equals(beanName, ContextIdNames.SHOPPING_ITEM_RECURRING_PRICE_ASSEMBLER)) {
					return new ShoppingItemRecurringPriceAssemblerImpl();
				}
				return null;
			}
			
			public boolean isDiscountable() {
				return true;
			}
		};
	}
	
}
