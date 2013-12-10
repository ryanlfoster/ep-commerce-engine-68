package com.elasticpath.sellingchannel.director.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shopper.impl.ShopperImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.impl.CartItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.WishListService;

/**
 * Tests the {@code CartDirectorImpl} in isolation.
 */
@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.TooManyMethods", "serial" })
public class CartDirectorImplTest {
	
	private static final String SHOPPING_ITEM_GUID = "shoppingItemGuid";
    private static final String SKU_CODE = "skuCode";
	//private static final String SKU_B = "SkuB";
	private static final String SKU_123 = "123";
	private static final int THREE = 3;
	private static final String SKU_A = "skuA";
	private static final String CURRENCY_CAD = "CAD";

	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
        setImposteriser(ClassImposteriser.INSTANCE);
			setThreadingPolicy(new Synchroniser());
		}
	};
    
	/**
	 * Tests that the {@code saveShoppingCart} method uses the shoppingCartService to persist.
	 */
	@Test
	public void testSaveShoppingCart() {
		CartDirectorImpl delegate = new CartDirectorImpl();
		
		final ShoppingCartService shoppingCartService = context.mock(ShoppingCartService.class);
		
		delegate.setShoppingCartService(shoppingCartService);
		
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final ShoppingCart updatedShoppingCart = context.mock(ShoppingCart.class, "updatedShoppingCart");
		
		context.checking(new Expectations() { { 
			oneOf(shoppingCartService).saveOrUpdate(shoppingCart); will(returnValue(updatedShoppingCart));
			allowing(shoppingCart).getCartItems();
		} });
		
		ShoppingCart actualShoppingCart = delegate.saveShoppingCart(shoppingCart);
		
		assertEquals("The persisted shopping cart should be returned to the client", updatedShoppingCart, actualShoppingCart);
	}
	
	
	/**
	 * Tests that the adding an item to a cart which already has an item with that sku and the product is not configurable
	 * will add to the quantity.
	 */
	@Test
	public void testAddToCartSkuAlreadyInCartNotConfigurable() {
		CartDirectorImpl delegate = new CartDirectorImpl() {
			@Override
			public ShoppingItem changeQuantityForCartItem(final ShoppingItem shoppingItem, final int quantity, 
					final ShoppingCart shoppingCart) {
				return null;
			}
			@Override
			protected void priceShoppingItem(final ShoppingItem shoppingItem,
					final Store store, final Shopper shopper,
					final Set<Long> ruleTracker) {
				// no-op
			}
		};
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Store store = context.mock(Store.class);
		final CartItem shoppingItem = context.mock(CartItem.class);
		final CartItem existingShoppingItem = context.mock(CartItem.class, "existing shopping item");
		final PriceLookupFacade priceLookupFacade = context.mock(PriceLookupFacade.class);
		delegate.setPriceLookupFacade(priceLookupFacade);
		delegate.setShoppingItemAssembler(new ShoppingItemAssemblerImpl());
		final ArrayList<ShoppingItem> items = new ArrayList<ShoppingItem>();
		items.add(existingShoppingItem);
		
		final ProductSku productSku = context.mock(ProductSku.class);
		
		context.checking(new Expectations() { { 
			allowing(shoppingItem).getProductSku(); will(returnValue(productSku));
			allowing(productSku).getSkuCode(); will(returnValue(SKU_123));
			allowing(shoppingCart).getCartItem(SKU_123); will(returnValue(existingShoppingItem));
			allowing(shoppingCart).getCartItems(SKU_123);
			allowing(shoppingCart).getShopper();
			allowing(shoppingCart).getStore();
			allowing(shoppingCart).getAppliedRules();
			allowing(shoppingItem).isBundle(); will(returnValue(false));
			allowing(existingShoppingItem).isBundle(); will(returnValue(false));
			
			allowing(shoppingItem).getQuantity(); will(returnValue(1));
			allowing(shoppingItem).isConfigurable(); will(returnValue(false));
		
			allowing(shoppingCart).getStore(); will(returnValue(store));
			allowing(shoppingCart).addShoppingCartItem(shoppingItem);
			
			allowing(shoppingItem).getLowestUnitPrice(); will(returnValue(
					MoneyFactory.createMoney(BigDecimal.ZERO, Currency.getInstance(CURRENCY_CAD))));
			allowing(shoppingCart).getCartItems(); will(returnValue(items));
			allowing(existingShoppingItem).isBundle(); will(returnValue(false));
			allowing(existingShoppingItem).getBundleItems(); will(returnValue(Collections.emptyList()));
			allowing(existingShoppingItem).getDependentItems(); will(returnValue(Collections.emptyList()));
			allowing(shoppingItem).getPrice(); will(returnValue(new PriceImpl()));
		} });
		
		delegate.addToCart(shoppingItem, shoppingCart, null);
		
	}
	
	/**
	 * Tests that the adding an item to a cart which already has an item with that sku and the product *is* configurable
	 * will add the new cart item.
	 */
	@Test
	public void testAddToCartSkuAlreadyInCartConfigurable() {
		CartDirectorImpl delegate = new CartDirectorImpl() {			
			@Override
			protected void priceShoppingItem(final ShoppingItem shoppingItem,
					final Store store, final Shopper shopper,
					final Set<Long> ruleTracker) {
				// no-op
			}
		};
		
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);
		final ShoppingItem existingShoppingItem = context.mock(ShoppingItem.class, "existing shopping item");
		final PriceLookupFacade priceLookupFacade = context.mock(PriceLookupFacade.class);
		delegate.setPriceLookupFacade(priceLookupFacade);
		
		final ProductSku productSku = context.mock(ProductSku.class);
		
		context.checking(new Expectations() { { 
			allowing(shoppingItem).getProductSku(); will(returnValue(productSku));
			allowing(productSku).getSkuCode(); will(returnValue(SKU_123));
			allowing(shoppingCart).getCartItem(SKU_123); will(returnValue(existingShoppingItem));
			allowing(shoppingCart).getCartItems(SKU_123);
			allowing(shoppingCart).getShopper();
			allowing(shoppingCart).getStore();
			allowing(shoppingCart).getAppliedRules();
			
			allowing(shoppingItem).getQuantity(); will(returnValue(1));
			allowing(shoppingItem).isConfigurable(); will(returnValue(true));

			allowing(shoppingItem).isBundle(); will(returnValue(false));
			allowing(existingShoppingItem).isBundle(); will(returnValue(false));

			
			
			oneOf(shoppingCart).addShoppingCartItem(shoppingItem);
			
			allowing(shoppingItem).getLowestUnitPrice(); will(returnValue(
					MoneyFactory.createMoney(BigDecimal.ZERO, Currency.getInstance(CURRENCY_CAD))));
			allowing(shoppingCart).getCartItems();
		} });
		
		delegate.addToCart(shoppingItem, shoppingCart, null);
		
	}
	
	/**
	 * Tests that updateCartItem() finds the existing cart item and sets sku, price and quantity. 
	 */
	@Test
	public void testUpdateCartItem() {
		final long itemUid = 5;
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final ShoppingItemDto dto = new ShoppingItemDto(SKU_A, 1);
		final ShoppingItemAssembler shoppingItemAssembler = context.mock(ShoppingItemAssembler.class);
		final CustomerSession customerSession = context.mock(CustomerSession.class);
		final Store store = context.mock(Store.class);
		final Catalog catalog = context.mock(Catalog.class);
		final CartItem existingShoppingItem = context.mock(CartItem.class, "existing");
		final CartItem updatedShoppingItem = context.mock(CartItem.class, "updated");
		final ProductSku productSku = context.mock(ProductSku.class);
        
		context.checking(new Expectations() { {
			oneOf(shoppingCart).removeCartItem(itemUid); // make sure the existing shopping item's removed from the cart
			allowing(shoppingCart).getCartItems();
			allowing(shoppingCart).getShopper(); will(returnValue(customerSession));
			allowing(shoppingCart).getAppliedRules();
			allowing(existingShoppingItem).getProductSku(); will(returnValue(productSku));
			allowing(existingShoppingItem).isBundle();
			allowing(existingShoppingItem).getBundleItems();
			allowing(existingShoppingItem).getDependentItems(); will(returnValue(Collections.emptyList()));
			allowing(existingShoppingItem).getGuid(); will(returnValue(SHOPPING_ITEM_GUID));
			allowing(productSku).getProduct();
			allowing(productSku).getUidPk();
			
			allowing(shoppingCart).getStore(); will(returnValue(store));
			allowing(store).getCatalog(); will(returnValue(catalog));
			allowing(catalog).getCode(); will(returnValue("CAT"));
			allowing(shoppingItemAssembler).validateShoppingItemDto(with(any(ShoppingItemDto.class)));
			oneOf(shoppingItemAssembler).createShoppingItem(dto); will(returnValue(updatedShoppingItem));
			allowing(existingShoppingItem).getOrdering(); will(returnValue(1));
			allowing(existingShoppingItem).isConfigurable(); will(returnValue(false));
			allowing(updatedShoppingItem).setOrdering(1);
			oneOf(updatedShoppingItem).setGuid(SHOPPING_ITEM_GUID);
		} });
		
		
		CartDirectorImpl director = new CartDirectorImpl() {
			@Override
			public ShoppingItem addToCart(final ShoppingItem shoppingItem, final ShoppingCart shoppingCart, final ShoppingItem parentItem) {
				return updatedShoppingItem;
			}
			@Override
			protected boolean isProductPurchasableInStore(final Store store, final Product product, final boolean isDependent, final long skuUid) {
				return true;
			}
			@Override
			protected void priceShoppingItem(final ShoppingItem shoppingItem, final Store store, final Shopper shopper,
					final Set<Long> ruleTracker) {
				//This would be too difficult to test in this manner.
			}
			@Override
			protected ShoppingItem getCartItem(final ShoppingCart shoppingCart, final long itemId) {
				return existingShoppingItem;
			}
		};
		director.setShoppingItemAssembler(shoppingItemAssembler);
		assertSame(updatedShoppingItem, director.updateCartItem(shoppingCart, itemUid, dto));
	}

	/**
	 * Test that the pricing functor does its job.
	 */
	@Test
	public void testPriceShoppingItemsWithTraverser() {
		CartDirectorImpl director = new CartDirectorImpl();
		final ShoppingItem shoppingItem = context.mock(ShoppingItem.class);
		final Price unitPrice = new PriceImpl();
		unitPrice.setListPrice(MoneyFactory.createMoney(BigDecimal.ONE, Currency.getInstance("USD")));
		final PriceLookupFacade priceLookupFacade = context.mock(PriceLookupFacade.class);
		final ProductBundle bundle = context.mock(ProductBundle.class);
		final ProductSku sku = context.mock(ProductSku.class);
		context.checking(new Expectations() { {
			allowing(shoppingItem).getQuantity(); will(returnValue(THREE));
			allowing(shoppingItem).getProductSku(); will(returnValue(sku));
			allowing(shoppingItem).getChildren(); will(returnValue(Collections.emptyList()));
			oneOf(shoppingItem).setPrice(THREE, unitPrice);
			allowing(shoppingItem).isBundle(); will(returnValue(true));
			allowing(sku).getProduct(); will(returnValue(bundle));
			allowing(bundle).getConstituents(); will(returnValue(Collections.emptyList()));
			allowing(priceLookupFacade).getShoppingItemPrice(shoppingItem, null, null, null); will(returnValue(unitPrice));
			allowing(sku).getSkuCode(); will(returnValue(SKU_A));

		} });
		director.setPriceLookupFacade(priceLookupFacade);
		director.priceShoppingItem(shoppingItem, null, null, null);
	}
	
	/**
	 * Test that the pricing functor does its job and prices child items.
	 */
	@Test
	public void testPriceChildShoppingItems() {
		CartDirectorImpl director = new CartDirectorImpl();
		final ShoppingItemImpl shoppingItemChild = new ShoppingItemImpl();
		final ShoppingItemImpl shoppingItemParent = new ShoppingItemImpl() {
			@Override
			public boolean isBundle() {
				return true;
			}
		};
		shoppingItemParent.addChild(shoppingItemChild);
		
		shoppingItemChild.setProductSku(new ProductSkuImpl());
		shoppingItemChild.setQuantity(THREE);
		shoppingItemParent.setProductSku(new ProductSkuImpl());
		shoppingItemParent.setQuantity(THREE);
		
		final Price unitPrice = context.mock(Price.class);
		final Currency currency = Currency.getInstance(CURRENCY_CAD);
		final Money twoBucks = MoneyFactory.createMoney(new BigDecimal("2"), currency);
		final PriceLookupFacade priceLookupFacade = context.mock(PriceLookupFacade.class);
		context.checking(new Expectations() { {
			allowing(unitPrice).getCurrency(); will(returnValue(currency));
			allowing(unitPrice).getSalePrice(THREE);
			will(returnValue(twoBucks));
			allowing(unitPrice).getListPrice(THREE); will(returnValue(twoBucks));
			allowing(unitPrice).getComputedPrice(THREE); will(returnValue(twoBucks));
			allowing(priceLookupFacade).getShoppingItemPrice(shoppingItemParent, null, null, null); will(returnValue(unitPrice));
			
			allowing(unitPrice).getPricingScheme(); will(returnValue(null));
			
		} });
		director.setPriceLookupFacade(priceLookupFacade);
		director.priceShoppingItem(shoppingItemParent, null, null, null);
		assertEquals(shoppingItemChild.getPrice(), unitPrice);
		assertEquals(shoppingItemParent.getPrice(), unitPrice);
	}
	
	/**
	 * Test that the pricing functor does its job and prices child items, and director throws Exception if a null is found.
	 */
	@Test
	public void testPriceChildShoppingItemChecksNullPrice() {
		CartDirectorImpl director = new CartDirectorImpl();
		final ShoppingItemImpl shoppingItemChild = new ShoppingItemImpl();
		final ShoppingItemImpl shoppingItemParent = new ShoppingItemImpl() {
			@Override
			public boolean isBundle() {
				return true;
			}
		};
		shoppingItemParent.addChild(shoppingItemChild);
		
		shoppingItemChild.setProductSku(new ProductSkuImpl());
		shoppingItemChild.setQuantity(THREE);
		shoppingItemParent.setProductSku(new ProductSkuImpl());
		shoppingItemParent.setQuantity(THREE);
		
		final Price unitPrice = context.mock(Price.class);
		final Currency currency = Currency.getInstance(CURRENCY_CAD);
		final Money twoBucks = MoneyFactory.createMoney(new BigDecimal("2"), currency);
		final PriceLookupFacade priceLookupFacade = context.mock(PriceLookupFacade.class);
		context.checking(new Expectations() { {
			allowing(unitPrice).getCurrency(); will(returnValue(currency));
			allowing(unitPrice).getSalePrice(THREE);
			will(returnValue(twoBucks));
			allowing(unitPrice).getListPrice(THREE); will(returnValue(null));
			allowing(unitPrice).getComputedPrice(THREE); will(returnValue(twoBucks));
			allowing(priceLookupFacade).getShoppingItemPrice(shoppingItemParent, null, null, null); will(returnValue(unitPrice));
			
			allowing(unitPrice).getPricingScheme(); will(returnValue(null));
			
		} });
		director.setPriceLookupFacade(priceLookupFacade);
		director.priceShoppingItem(shoppingItemParent, null, null, null);
	}
	

	
	/**
	 * test moving item from wish list to shopping cart.
	 */
	@Test
	public void testMoveItemFromWishListToCart() {
		CartDirectorImpl director = new CartDirectorImpl() {
			@Override
			public ShoppingItem addItemToCart(final ShoppingCart shoppingCart, final ShoppingItemDto dto) {
				return null;
			}
			@Override
			protected boolean isSkuAllowedAddToCart(final String skuCode, final ShoppingCart shoppingCart) {
				return true;
			}
		};
		
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Shopper shopper = new ShopperImpl();
		final ShoppingItemDto dto = context.mock(ShoppingItemDto.class);
		final WishList wishList = context.mock(WishList.class);
		
		final WishListService wishListService = context.mock(WishListService.class);
		director.setWishListService(wishListService);
		final ProductSkuService productSkuService = context.mock(ProductSkuService.class);
		director.setProductSkuService(productSkuService);
		final ShoppingCartService shoppingCartService = context.mock(ShoppingCartService.class);
		director.setShoppingCartService(shoppingCartService);
		
		context.checking(new Expectations() { {
			allowing(shoppingCart).getShopper();
			will(returnValue(shopper));
			
			allowing(wishListService).findOrCreateWishListByShopper(shopper);
			will(returnValue(wishList));
			
			allowing(dto).getSkuCode();
			will(returnValue(SKU_CODE));
			
			allowing(wishList).removeItem(SKU_CODE);
			allowing(wishListService).save(wishList);
			
			allowing(dto).getQuantity(); will(returnValue(1));
			allowing(shoppingCart).getCartItems();
			
			allowing(shoppingCartService).saveOrUpdate(shoppingCart);
		} });
		
		director.moveItemFromWishListToCart(shoppingCart, dto);
	}
	/**
	 * test moving item with quantity >1 from wish list to shopping cart.
	 */
	@Test
	public void testMoveItemWithQuantityFromWishListToCart() {
		CartDirectorImpl director = new CartDirectorImpl() {
			@Override
			public ShoppingItem addItemToCart(final ShoppingCart shoppingCart, final ShoppingItemDto dto) {
				return null;
			}
			@Override
			protected boolean isSkuAllowedAddToCart(final String skuCode, final ShoppingCart shoppingCart) {
				return true;
			}
		};
		
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final Shopper shopper = context.mock(Shopper.class);
		final ShoppingItemDto dto = context.mock(ShoppingItemDto.class);
		final WishList wishList = context.mock(WishList.class);
		
		final WishListService wishListService = context.mock(WishListService.class);
		director.setWishListService(wishListService);
		final ProductSkuService productSkuService = context.mock(ProductSkuService.class);
		director.setProductSkuService(productSkuService);
		final ShoppingCartService shoppingCartService = context.mock(ShoppingCartService.class);
		director.setShoppingCartService(shoppingCartService);
		
		context.checking(new Expectations() { {
			ignoring(shoppingCart).getShopper();
			will(returnValue(shopper));
			
			ignoring(wishListService).findOrCreateWishListByShopper(shopper);
			will(returnValue(wishList));
			
			ignoring(dto).getSkuCode();
			will(returnValue(SKU_CODE));
			
			ignoring(wishList).removeItem(SKU_CODE);
			allowing(wishListService).save(wishList);
			
			allowing(dto).getQuantity(); will(returnValue(2));
			
			ignoring(shoppingCart).getCartItems(); 
			
			oneOf(shoppingCart).setItemWithNoTierOneFromWishList(true);
			
			allowing(shoppingCartService).saveOrUpdate(shoppingCart);
		} });
		
		director.moveItemFromWishListToCart(shoppingCart, dto);
	}
	
	/**
	 * Test if a sku is allowed to add to cart.
	 */
	@Test
	public void testIsSkuAllowedAddToCart() {
		CartDirectorImpl director = new CartDirectorImpl();
		final ShoppingCart shoppingCart = context.mock(ShoppingCart.class);
		final ProductSku sku = context.mock(ProductSku.class);
		final Product product = context.mock(Product.class);
		final Date currentTime = new Date();
		final Store store = context.mock(Store.class);
		final Catalog catalog = context.mock(Catalog.class);
		
		final ProductSkuService productSkuService = context.mock(ProductSkuService.class);
		director.setProductSkuService(productSkuService);
		final TimeService timeService = context.mock(TimeService.class);
		director.setTimeService(timeService);
		
		context.checking(new Expectations() { {
			oneOf(productSkuService).findBySkuCode(SKU_CODE);
			will(returnValue(sku));
			oneOf(sku).getProduct();
			will(returnValue(null));
		} });
		
		assertFalse("should return false if product cannot be found", director.isSkuAllowedAddToCart(SKU_CODE, shoppingCart));
		
		context.checking(new Expectations() { {
			oneOf(productSkuService).findBySkuCode(SKU_CODE);
			will(returnValue(sku));
			oneOf(sku).getProduct();
			will(returnValue(product));
			oneOf(product).isHidden();
			will(returnValue(true));
		} });
		
		assertFalse("should return false if product is hidden", director.isSkuAllowedAddToCart(SKU_CODE, shoppingCart));
		
		context.checking(new Expectations() { {
			oneOf(productSkuService).findBySkuCode(SKU_CODE);
			will(returnValue(sku));
			oneOf(sku).getProduct();
			will(returnValue(product));
			oneOf(product).isHidden();
			will(returnValue(false));
			oneOf(timeService).getCurrentTime();
			will(returnValue(currentTime));
			oneOf(product).isWithinDateRange(currentTime);
			will(returnValue(false));
		} });
		
		assertFalse("should return false if product is not in date range", director.isSkuAllowedAddToCart(SKU_CODE, shoppingCart));
		
		context.checking(new Expectations() { {
			oneOf(productSkuService).findBySkuCode(SKU_CODE);
			will(returnValue(sku));
			oneOf(sku).getProduct();
			will(returnValue(product));
			oneOf(product).isHidden();
			will(returnValue(false));
			oneOf(timeService).getCurrentTime();
			will(returnValue(currentTime));
			oneOf(product).isWithinDateRange(currentTime);
			will(returnValue(true));
			oneOf(shoppingCart).getStore();
			will(returnValue(store));
			oneOf(store).getCatalog();
			will(returnValue(catalog));
			oneOf(product).isInCatalog(catalog);
			will(returnValue(false));
		} });
		
		assertFalse("should return false if the product catalog is not the store catalog", director.isSkuAllowedAddToCart(SKU_CODE, shoppingCart));
		
		context.checking(new Expectations() { {
			oneOf(productSkuService).findBySkuCode(SKU_CODE);
			will(returnValue(sku));
			oneOf(sku).getProduct();
			will(returnValue(product));
			oneOf(product).isHidden();
			will(returnValue(false));
			allowing(timeService).getCurrentTime();
			will(returnValue(currentTime));
			oneOf(product).isWithinDateRange(currentTime);
			will(returnValue(true));
			oneOf(shoppingCart).getStore();
			will(returnValue(store));
			oneOf(store).getCatalog();
			will(returnValue(catalog));
			oneOf(product).isInCatalog(catalog);
			will(returnValue(true));
			oneOf(sku).isWithinDateRange(currentTime);
			will(returnValue(false));
		} });
		
		assertFalse("should return false if the sku is not in date range", director.isSkuAllowedAddToCart(SKU_CODE, shoppingCart));
		
		context.checking(new Expectations() { {
			oneOf(productSkuService).findBySkuCode(SKU_CODE);
			will(returnValue(sku));
			oneOf(sku).getProduct();
			will(returnValue(product));
			oneOf(product).isHidden();
			will(returnValue(false));
			allowing(timeService).getCurrentTime();
			will(returnValue(currentTime));
			oneOf(product).isWithinDateRange(currentTime);
			will(returnValue(true));
			oneOf(shoppingCart).getStore();
			will(returnValue(store));
			oneOf(store).getCatalog();
			will(returnValue(catalog));
			oneOf(product).isInCatalog(catalog);
			will(returnValue(true));
			oneOf(sku).isWithinDateRange(currentTime);
			will(returnValue(true));
		} });
		
		assertTrue("should be true", director.isSkuAllowedAddToCart(SKU_CODE, shoppingCart));
	}
	
    /**
     * Tests {@link CartDirectorImpl#retainShoppingItemIdentity(ShoppingItem, ShoppingItem)} for a non-configurable shopping item.
     */
	@Test
	public void testRetainShoppingItemIdentity() {
	    final ShoppingItem existingItem = context.mock(ShoppingItem.class, "existing");
	    final ShoppingItem newItem = context.mock(ShoppingItem.class, "new");
	    
	    context.checking(new Expectations() {
	        {
	            allowing(existingItem).getGuid(); will(returnValue(SHOPPING_ITEM_GUID));
	            oneOf(newItem).setGuid(SHOPPING_ITEM_GUID);
	        }
	    });
	    
	    CartDirectorImpl cartDirector = new CartDirectorImpl();
	    cartDirector.retainShoppingItemIdentity(existingItem, newItem);
	}
	
	/**
	 * Tests that if a shopping cart contains a non-purchasable cart item, it gets removed from the cart.
	 */
	@Test
	public void testRemoveShoppingItemThatIsNotPurchasable() {
		String skuCode = "code";
		final ShoppingItem item = new ShoppingItemImpl();
		final ProductSku sku = createProductSku(skuCode);
		item.setProductSku(sku);
		final List<ShoppingItem> cartItems = new ArrayList<ShoppingItem>() { {
			add(item);
		} };
		
		ShoppingCart cart = new ShoppingCartImpl() {
			@Override
			public List<ShoppingItem> getCartItems() {
				return cartItems;
			}
			@Override
			public Store getStore() {
				return null;
			}
		};
		
		CartDirector cartDirector = new CartDirectorImpl() {
			@Override
			protected boolean isProductPurchasableInStore(final Store store, final Product product, final boolean isDependent, final long skuUid) {
				return false;
			}
			@Override
			protected void refreshShoppingItems(final List<ShoppingItem> items, final ShoppingCart shoppingCart) {
				//do nothing
			}
		};
		
		cartDirector.removeAnyNonPurchasableItems(cart.getCartItems(), cart);
		assertTrue("Shopping cart items should be empyt after the non purchasable item has been removed", cart.getCartItems().isEmpty());
		assertEquals("Shopping cart should contain a sku code for the removed shopping item", 
				skuCode, cart.getNotPurchasableCartItemSkus().iterator().next());
	}

	/**
	 * Tests that if a shopping cart contains an item with no price tiers associated.
	 */
	@Test
	public void testRemoveShoppingItemThatHasNoPriceTiersAssociated() {
		String skuCode = "code";
		final ShoppingItem item = new ShoppingItemImpl() {
			@Override
			public Price getPrice() {
				return new PriceImpl();
			}
		};
		final ProductSku sku = createProductSku(skuCode);
		item.setProductSku(sku);
		
		final List<ShoppingItem> cartItems = new ArrayList<ShoppingItem>() { {
			add(item);
		} };
		
		ShoppingCart cart = new ShoppingCartImpl() {
			@Override
			public List<ShoppingItem> getCartItems() {
				return cartItems;
			}
			@Override
			public Store getStore() {
				return null;
			}
		};
		
		CartDirector cartDirector = new CartDirectorImpl() {
			@Override
			protected boolean isProductPurchasableInStore(final Store store, final Product product, final boolean isDependent, final long skuUid) {
				return true;
			}
			@Override
			protected void refreshShoppingItems(final List<ShoppingItem> items, final ShoppingCart shoppingCart) {
				//do nothing
			}
		};
		
		cartDirector.removeAnyNonPurchasableItems(cart.getCartItems(), cart);
		assertTrue("Shopping cart items should be empyt after the item with no price tiers has been removed", cart.getCartItems().isEmpty());
		assertEquals("Shopping cart should contain a sku code for the removed shopping item", 
				skuCode, cart.getNotPurchasableCartItemSkus().iterator().next());
	}

	/**
	 * Tests that if a shopping cart contains an item with no price tiers associated it doesn't get removed.
	 */
	@Test
	public void testDoNotRemoveShoppingItemThatHasPriceTiersAssociatedAndIsPurchasable() {
		String skuCode = "code";
		final ShoppingItem item = new ShoppingItemImpl() {
			@Override
			public Price getPrice() {
				return new PriceImpl() {
					@Override
					public SortedMap<Integer, PriceTier> getPriceTiers() {
						SortedMap<Integer, PriceTier> priceTiers = new TreeMap<Integer, PriceTier>();
						priceTiers.put(1, new PriceTierImpl());
						return priceTiers;
					}
				};
			}
		};
		final ProductSku sku = createProductSku(skuCode);
		item.setProductSku(sku);
		
		final List<ShoppingItem> cartItems = new ArrayList<ShoppingItem>() { {
			add(item);
		} };
		
		ShoppingCart cart = new ShoppingCartImpl() {
			@Override
			public List<ShoppingItem> getCartItems() {
				return cartItems;
			}
			@Override
			public Store getStore() {
				return null;
			}
		};
		
		CartDirector cartDirector = new CartDirectorImpl() {
			@Override
			protected boolean isProductPurchasableInStore(final Store store, final Product product, final boolean isDependent, final long skuUid) {
				return true;
			}
			@Override
			protected void refreshShoppingItems(final List<ShoppingItem> items, final ShoppingCart shoppingCart) {
				//do nothing
			}
		};
		
		cartDirector.removeAnyNonPurchasableItems(cart.getCartItems(), cart);
		assertFalse("Shopping cart items should not be empty after the item with no price tiers has not been removed", 
				cart.getCartItems().isEmpty());
		assertTrue("Shopping cart should not contain a sku code for the shopping item that has not been removes", 
				cart.getNotPurchasableCartItemSkus().isEmpty());
	}
	
	private ProductSku createProductSku(final String skuCode) {
		ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode(skuCode);
		Product product = new ProductImpl();
		sku.setProduct(product);
		return sku;
	}
}
