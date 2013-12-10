/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.shoppingcart.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.impl.WishListImpl;
import com.elasticpath.service.shoppingcart.dao.WishListDao;
import com.elasticpath.test.factory.TestCustomerSessionFactory;


/** Test case for <code>WishListServiceImpl</code>. */
public class WishListServiceImplTest {
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private WishListServiceImpl wishListService;
	private BeanFactory beanFactory;
	private WishListDao wishListDao;
	
	/**
	 * The setup method.
	 */
	@Before
	public void setUp() {
		wishListService = new WishListServiceImpl();
		
		beanFactory = context.mock(BeanFactory.class);
		wishListService.setBeanFactory(beanFactory);
		
		wishListDao = context.mock(WishListDao.class);
		wishListService.setWishListDao(wishListDao);
	}
	
	/**
	 * Test creating a wish list.
	 */
	@Test
	public void testCreateWishList() {
		final CustomerSession customerSession = TestCustomerSessionFactory.getInstance().createNewCustomerSession();
		final Shopper shopper = customerSession.getShopper();
		
		context.checking(new Expectations() { {

			oneOf(beanFactory).getBean(ContextIdNames.WISH_LIST);
			will(returnValue(new WishListImpl() {
				private static final long serialVersionUID = -7785511152889149172L;

				public void initialize() {
					 //nothing to do ...
				 }
			}));
		} });
		
		WishList wishList = wishListService.createWishList(shopper);
		
		assertNotNull("the returned wish list should not be null", wishList);
		assertEquals("the shopping context in wish list is not expected one", 
				shopper, wishList.getShopper());
	}
}
