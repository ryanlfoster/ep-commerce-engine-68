package com.elasticpath.service.shoppingcart.impl;

import static org.hamcrest.Matchers.equalTo;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sellingchannel.director.CartDirector;

/**
 * Test case for {@link ShoppingCartMergerImpl}.
 */
@SuppressWarnings("PMD.NonStaticInitializer")
public class ShoppingCartMergerImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final ShoppingCartMergerImpl shoppingCartMerger = new ShoppingCartMergerImpl();

	private CartDirector cartDirector;

	/** Test initialization. */
	@Before
	public void setUp() {
		cartDirector = context.mock(CartDirector.class);
		shoppingCartMerger.setCartDirector(cartDirector);
	}

	/** When merging carts, the cmclient user ID should also be merged. */
	@Test
	@SuppressWarnings("unchecked")
	public void testMergeCmUserId() {
		final ShoppingCart donor = context.mock(ShoppingCart.class, "donorCart");
		final ShoppingCart recipient = context.mock(ShoppingCart.class, "recipientCart");

		final Long cmuserid = 4414L;
		context.checking(new Expectations() {
			{
				allowing(donor).getCartItems();
				allowing(donor).getNumItems();
				allowing(donor).getPromotionCodes();
				allowing(donor).getAppliedGiftCertificates();
				allowing(recipient).getCartItems();
				allowing(recipient).getNumItems();
				allowing(cartDirector).removeAnyNonPurchasableItems(with(any(List.class)),
						with(equalTo(recipient)));

				allowing(donor).getCmUserUID();
				will(returnValue(cmuserid));
				one(recipient).setCmUserUID(cmuserid);
			}
		});

		shoppingCartMerger.merge(recipient, donor);
	}
}
