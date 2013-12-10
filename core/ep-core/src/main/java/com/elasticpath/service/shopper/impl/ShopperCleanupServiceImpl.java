/**
 *
 */
package com.elasticpath.service.shopper.impl;

import java.util.List;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.service.customer.CustomerSessionCleanupService;
import com.elasticpath.service.shopper.ShopperCleanupService;
import com.elasticpath.service.shopper.ShopperDependencyCleanupService;
import com.elasticpath.service.shopper.dao.ShopperDao;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.WishListService;

/**
 * Service methods used for cleaning up old shopper records. Delegates to a Dao.
 */
public class ShopperCleanupServiceImpl implements ShopperCleanupService, ShopperDependencyCleanupService {

	private ShopperDao shopperDao;
	private WishListService wishListService;
	private ShoppingCartService shoppingCartService;
	private CustomerSessionCleanupService customerSessionCleanupService;
	private BeanFactory beanFactory;

	@Override
	public int removeShoppersByCustomer(final Customer customer) {
		List<Long> mementoUidsToRemove = shopperDao.findUidsByCustomer(customer);
		return removeShoppersByUidListAndTheirDependents(mementoUidsToRemove);
	}

	@Override
	public int removeShoppersByUidListAndTheirDependents(final List<Long> mementoUidsToRemove) {
		//the following method call will run in a new transaction.
		getShopperDependencyCleanupService().removeDependantsFromShopper(mementoUidsToRemove);
		return shopperDao.removeShoppersByUidList(mementoUidsToRemove);
	}

	@Override
	public void removeDependantsFromShopper(final List<Long> mementoUidsToRemove) {
		customerSessionCleanupService.deleteByShopperUids(mementoUidsToRemove);
		wishListService.deleteAllWishListsByShopperUids(mementoUidsToRemove);
		shoppingCartService.deleteAllShoppingCartsByShopperUids(mementoUidsToRemove);
	}

	@Override
	public List<ShopperMemento> findShoppersOrphanedFromCustomerSessions(final int maxResults) {
		return shopperDao.findShoppersOrphanedFromCustomerSessions(maxResults);
	}

	public void setCustomerSessionCleanupService(final CustomerSessionCleanupService customerSessionCleanupService) {
		this.customerSessionCleanupService = customerSessionCleanupService;
	}

	@Override
	public int removeShoppersByUidList(final List<Long> shopperUids) {
	    return shopperDao.removeNonDependantShoppersByUidList(shopperUids);
	}

	public void setWishListService(final WishListService wishListService) {
		this.wishListService = wishListService;
	}

	public void setShopperDao(final ShopperDao shoppingContextDao) {
		shopperDao = shoppingContextDao;
	}

	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	@Override
	public List<Long> findShoppersByCustomer(final Customer customer) {
		return shopperDao.findUidsByCustomer(customer);
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	protected ShopperDependencyCleanupService getShopperDependencyCleanupService() {
		return getBeanFactory().getBean(ContextIdNames.SHOPPER_CLEANUP_SERVICE);
	}

}
