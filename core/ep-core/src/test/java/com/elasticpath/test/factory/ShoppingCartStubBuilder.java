package com.elasticpath.test.factory;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;

/**
 * A builder for ShoppingCarts and its items.
 */

public final class ShoppingCartStubBuilder {

	private static final Currency CURRENCY_CAD = Currency.getInstance("CAD");

	/**
	 * Prevent subclassing.
	 */
	private ShoppingCartStubBuilder() {
		// Prevent subclassings, the inner classes should be extended instead
	}
	
	/**
	 * Simple workaround for providing unique mock id's.
	 */
	protected static final class Incrementer {
		private static int value;
		
		private Incrementer() {
			// prevent external Instantiation
		}
		
		/**
		 * @return the next incremental id.
		 */
		public static int nextId() {
			return value++;
		}
	}
	
	/**
	 * @param prefix a prefix to help identify the mock, eg. 'cart-item'.
	 * @return a unique-number prefixed by <code>prefix</code> and a hyphen.
	 */
	protected static String nextMockId(final String prefix) {
		return prefix + "-" + Incrementer.nextId();
	}
	
	
	/**
	 * @param context the Mockery being used by the test.
	 * @return a new builder.
	 */
	public static ShoppingCartBuilder aCart(final Mockery context) {
		return new ShoppingCartBuilder(context);
	}

	/** 
	 * Factory method for shopping items. 
	 * @param context the JMock context used to create the subs around.
	 * @return a ShoppingItemBuilder.
	 */
	public static ShoppingItemBuilder aShoppingItem(final Mockery context) {
		return new ShoppingItemBuilder(context);
	}
	
	/**
	 * A builder for stubbed shopping carts to ease test setup.
	 */
	/**
	 * @author ivanjensen
	 *
	 */
	public static class ShoppingCartBuilder {
		
		private final List<ShoppingItem> items = new ArrayList<ShoppingItem>();
		private final Mockery context;
		private final ShoppingCart cart;

		/**
		 * @param context the Mockery used by the test.
		 */
		public ShoppingCartBuilder(final Mockery context) {
			this.context = context;
			cart = context.mock(ShoppingCart.class, nextMockId("cart"));
		}
		
		/**
		 * @param currency the currency for the cart.
		 * @return this cart builder.
		 */
		public ShoppingCartBuilder withCurrency(final Currency currency) {
			context.checking(new Expectations() { {
				allowing(cart).getCurrency();
				will(returnValue(currency));
			} });
			return this;
		}
		
		/**
		 * Sets the cart's currency to Canadian Dollars, a convenience version of <code>withCurrency</code>.
		 * @return this cart builder.
		 */
		public ShoppingCartBuilder withCurrencyCAD() {
			withCurrency(CURRENCY_CAD);
			return this;
		}
				
		/**
		 * Add the result of the item builder.
		 * @param itemBuilder the builder for the shopping item
		 * @return this cart builder
		 */
		public ShoppingCartBuilder with(final ShoppingItemBuilder itemBuilder) {
			items.add(itemBuilder.build());
			return this;
		}
		
		/**
		 * Specify the store for the cart.
		 * @param store the store the cart is for
		 * @return this cart builder
		 */
		public ShoppingCartBuilder forStore(final Store store) {
			context.checking(new Expectations() { {
				allowing(cart).getStore();
				will(returnValue(store));
			} });
			return this;
		}
		
		/**
		 * Specify the cart subtotal.
		 * @param subTotal the cart's subtotal
		 * @return this cart builder
		 */
		public ShoppingCartBuilder withSubtotal(final BigDecimal subTotal) {
			context.checking(new Expectations() { {
				allowing(cart).getSubtotal();
				will(returnValue(subTotal));
			} });
			return this;
		}

		/**
		 * Specify the cart subtotal discount.
		 * @param subTotalDiscount the discount for the cart subtotal
		 * @return this cart builder
		 */
		public ShoppingCartBuilder withSubtotalDiscount(final BigDecimal subTotalDiscount) {
			context.checking(new Expectations() { {
				allowing(cart).getSubtotalDiscount();
				will(returnValue(subTotalDiscount));
			} });
			return this;
		}
		
		/**
		 * @return the built cart
		 */
		public ShoppingCart build() {
			
			context.checking(new Expectations() { {
				allowing(cart).getCartItems();
				will(returnValue(items));

				allowing(cart).getAllItems();
				will(returnValue(items));
			} });

			return cart;
		}

		protected Mockery getContext() {
			return context;
		}

		protected ShoppingCart getCart() {
			return cart;
		}

	}
	
	/**
	 * A builder for stub cart items for use JMock unit tests.
	 */
	/**
	 * @author ivanjensen
	 *
	 */
	public static class ShoppingItemBuilder {

		private final Mockery context;
		private final ShoppingItem item;

		private final ProductSku sku = new ProductSkuImpl();
		
		/**
		 * Create a builder for shopping items.
		 * @param context the mockery to register the item's expectations with.
		 */
		public ShoppingItemBuilder(final Mockery context) {
			this.context = context;
			this.item = context.mock(ShoppingItem.class, nextMockId("cartItem"));

			context.checking(new Expectations() { {
				allowing(item).getProductSku();
				will(returnValue(sku));
			} });
		}
		
		/**
		 * Call once, once all the other builder methods have been called.
		 * @return the built shopping item.
		 */
		public ShoppingItem build() {
			return item;
		}			
		
		/**
		 * @param itemCost the amount that the item should cost in CAD.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder costing(final BigDecimal itemCost) {
			final Price price = new PriceImpl();
			final Money money = MoneyFactory.createMoney(itemCost, CURRENCY_CAD);
			price.setListPrice(money);

			getContext().checking(new Expectations() { {
				allowing(item).getPrice();
				will(returnValue(price));
				
				allowing(item).getTotal();
				will(returnValue(money));
			} });
			return this;
		}
		
		/**
		 * Makes the item report that it is not shippable.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder thatsNotShippable() {
			getContext().checking(new Expectations() { {
				sku.setShippable(false);
			} });
			return this;
		}
		
		/**
		 * Makes the item report that it is shippable.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder thatsShippable() {
			sku.setShippable(true);
			return this;
		}
		
		/**
		 * Makes the item report that it has the specified quantity.
		 * @param quantity the quantity to be ordered.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder withQuantity(final int quantity) {
			getContext().checking(new Expectations() { {
				allowing(item).getQuantity(); 
				will(returnValue(quantity));
			} });
			return this;
		}

		/** 
		 * Makes the shopping item's sku the specified code.
		 * @param skuCode the code for the sku.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder withSkuCode(final String skuCode) {
			sku.setSkuCode(skuCode);
			return this;
		}

		/**
		 * Makes the shopping item report that it is for the specified product.
		 * @param product the product.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder withProduct(final Product product) {
			sku.setProduct(product);
			return this;
		}
		
		/**
		 * Makes the shopping item report that it is discountable.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder thatsDiscountable() {
			getContext().checking(new Expectations() { {
				allowing(item).isDiscountable();
				will(returnValue(true));
			} });
			return this;
		}
		
		/**
		 * Makes the shopping item report that it is not discountable.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder thatsNotDiscountable() {
			getContext().checking(new Expectations() { {
				allowing(item).isDiscountable();
				will(returnValue(true));
			} });
			return this;
		}

		/**
		 * Makes the shopping item's sku report that it weighs the specified amount.
		 * @param weight the weight of the sku.
		 * @return this shopping item builder.
		 */
		public ShoppingItemBuilder withWeight(final BigDecimal weight) {
			sku.setWeight(weight);
			return this;
		}

		protected Mockery getContext() {
			return context;
		}

		protected ShoppingItem getItem() {
			return item;
		}

	}
}

