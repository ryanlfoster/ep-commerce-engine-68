package com.elasticpath.importexport.common.adapters.products.data;

import java.util.Date;

import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.products.ProductAvailabilityDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.importexport.common.util.Message;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between <code>Product</code> and
 * <code>ProductAvailabilityDTO</code> objects.
 */
public class ProductAvailabilityAdapter extends AbstractDomainAdapterImpl<Product, ProductAvailabilityDTO> {
	
	private static final Logger LOG = Logger.getLogger(ProductAvailabilityAdapter.class);

	@Override
	public void populateDomain(final ProductAvailabilityDTO source, final Product target) {
		target.setAvailabilityCriteria(source.getAvailabilityCriteria());
		
		if (!isAvailabilityDatesCorrect(source.getStartDate(), source.getEndDate())) {
			throw new PopulationRuntimeException("IE-10319", target.getCode());
		}
		
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		
		populateExpectedReleaseDate(source, target);
		
		checkMinOrderQty(source.getMinOrderQty());
		checkOrderLimit(source.getPreOrBackOrderLimit());
		
		target.setMinOrderQty(source.getMinOrderQty());
		target.setPreOrBackOrderLimit(source.getPreOrBackOrderLimit());
		target.setHidden(!source.isStoreVisible());
		target.setNotSoldSeparately(source.isNotSoldSeparately());

	}

	/**
	 * Populates product with ExpectedReleaseDate using ProductAvailabilityDTO.
	 * Checks if AvailabilityCriteria is AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER or AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER
	 * 
	 * @param productAvailabilityDTO the DTO to populate from.
	 * @param product the Product to populate.
	 */
	void populateExpectedReleaseDate(final ProductAvailabilityDTO productAvailabilityDTO, final Product product) {
		final AvailabilityCriteria availabilityCriteria = productAvailabilityDTO.getAvailabilityCriteria();
		if (availabilityCriteria.equals(AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER)) {
			if (productAvailabilityDTO.getExpectedReleaseDate() == null) {
				throw new PopulationRuntimeException("IE-10311", product.getCode());
			} 
			
			product.setExpectedReleaseDate(productAvailabilityDTO.getExpectedReleaseDate());
		} else {
			if (productAvailabilityDTO.getExpectedReleaseDate() != null) {
				LOG.error(new Message("IE-10323", product.getCode()));
			}
		}
	}

	/**
	 * Checks if Order Limit is negative.
	 * 
	 * @param orderLimit the orderLimit parameter
	 */
	void checkOrderLimit(final int orderLimit) {
		if (orderLimit < 0) {
			throw new PopulationRuntimeException("IE-10312");
		}
	}

	/**
	 * Checks if Minimum Order Qty less than 1.
	 * 
	 * @param minOrderQty the minOrderQty parameter
	 */
	void checkMinOrderQty(final int minOrderQty) {
		if (minOrderQty < 1) {
			LOG.error(new Message("IE-10313"));
		}
	}
	
	/**
	 * Checks the correctness of availability dates.
	 * 
	 * @param enableDate the enable date
	 * @param disableDate the disable date
	 * @return true if dates are correct and false otherwise
	 */
	boolean isAvailabilityDatesCorrect(final Date enableDate, final Date disableDate) {
		if (disableDate == null) {
			return true;
		}
		return enableDate.compareTo(disableDate) < 0;
	}

	@Override
	public void populateDTO(final Product source, final ProductAvailabilityDTO target) {
		target.setAvailabilityCriteria(source.getAvailabilityCriteria());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
		target.setExpectedReleaseDate(source.getExpectedReleaseDate());
		target.setMinOrderQty(source.getMinOrderQty());
		target.setPreOrBackOrderLimit(source.getPreOrBackOrderLimit());
		target.setNotSoldSeparately(source.isNotSoldSeparately());
		target.setStorevisible(!source.isHidden());
	}
}
