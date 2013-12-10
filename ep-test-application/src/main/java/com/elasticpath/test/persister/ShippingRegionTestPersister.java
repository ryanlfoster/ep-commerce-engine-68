package com.elasticpath.test.persister;

import java.util.List;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.impl.ShippingRegionImpl;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.shipping.ShippingRegionExistException;
import com.elasticpath.service.shipping.ShippingRegionService;

/**
 * Allow to create, save, update ShippingRegions into database.
 */
public class ShippingRegionTestPersister {
	
	private final BeanFactory beanFactory;
	
	private final ShippingRegionService shippingRegionService;
	
	/**
	 * Construct DynamicContentDeliveryTestPersister.
	 * @param beanFactory - elastic path bean factory
	 */
	public ShippingRegionTestPersister(final BeanFactory beanFactory) {
		
		this.beanFactory = beanFactory;
		shippingRegionService = this.beanFactory.getBean(ContextIdNames.SHIPPING_REGION_SERVICE);
	}
	
	/**
	 * delete any existing data.
	 * there is some data loaded into ShippingRegions by default when the database is created.
	 * for the purpose of fit testing we want the database table to be empty.
	 * @throws EpServiceException if unable to delete ShippingRegions
	 */
	public void clearExistingData() throws EpServiceException {
		List <ShippingRegion> allRegions = shippingRegionService.list();
		for (ShippingRegion current : allRegions) {
			shippingRegionService.remove(current);
		}
	}

	/**
	 * Persist ShippingRegion into database.
	 * @param name name (region/country name)
	 * @param regionStr should look like[$countryCode($region1,$region2)] all should be 2 chars length
	 * @return persistent instance of ShippingRegion 
	 * @throws ShippingRegionExistException if the shipping region already exist
	 */
	public ShippingRegion persistShippingRegion(final String guid, final String name, final String regionStr) 
		throws ShippingRegionExistException
	{
		
		final ShippingRegion shippingRegion = this.beanFactory.getBean(ContextIdNames.SHIPPING_REGION);
		shippingRegion.setGuid(guid);
		shippingRegion.setName(name);
		//regionStr should look like[$countryCode($region1,$region2)]
		//e.g [CA(AB,BC,MB etc ... )] - all should be 2 chars length
		//as this is only for testing we can take a liberty here and ignore the interface spec
		//and brute force this cast ... 
		((ShippingRegionImpl)shippingRegion).setRegionStr((regionStr != null ? regionStr : ""));
		
		return shippingRegionService.add(shippingRegion);
		
	}

	/**
	 * Persist ShippingRegion into database.
	 * @param guid guid
	 * @param name name (region/country name)
	 * @param regions region map
	 * @return peresisted shipping region
	 */
	public final ShippingRegion persistShippingRegion(final String guid, final String name, final Map<String, Region> regions) {
		final ShippingRegion shippingRegion = this.beanFactory.getBean(ContextIdNames.SHIPPING_REGION);
		shippingRegion.setGuid(guid);
		shippingRegion.setName(name);
		shippingRegion.setRegionMap(regions);
		
		return shippingRegionService.add(shippingRegion);
	}
	
	/**
	 * Updates shipping region.
	 * @param region shipping region to update
	 * @return updated shipping region
	 */
	public final ShippingRegion updateShippingRegion(final ShippingRegion region) {
		return shippingRegionService.update(region);
	}
	
}
