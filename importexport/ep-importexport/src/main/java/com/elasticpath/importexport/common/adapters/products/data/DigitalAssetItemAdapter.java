package com.elasticpath.importexport.common.adapters.products.data;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.products.DigitalAssetItemDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between <code>ProductSku</code>
 * and <code>DigitalAssetItemDTO</code> objects.
 */
public class DigitalAssetItemAdapter extends AbstractDomainAdapterImpl<ProductSku, DigitalAssetItemDTO> {
	
	@Override
	public void populateDomain(final DigitalAssetItemDTO source, final ProductSku target) {
		
		target.setDigital(source.isEnabled());
		
		final boolean downloadable = StringUtils.isNotBlank(source.getFileName());
		
		if (source.isEnabled() && downloadable) {
				
			DigitalAsset digitalAsset = createDigitalAsset(target);

			checkDownloadLimit(source.getMaxDownloadTimes());
			checkDownloadExpiry(source.getExpiryDays());

			digitalAsset.setFileName(source.getFileName());
			digitalAsset.setExpiryDays(source.getExpiryDays());
			digitalAsset.setMaxDownloadTimes(source.getMaxDownloadTimes());
			
			target.setDigitalAsset(digitalAsset);	
					
		} else {
			
			target.setDigitalAsset(null); // Downloadable is implemented as "digitalAsset != null"
			
		}
	}

	/**
	 * Creates DigitalAsset instance if productSku does not contain it.
	 * 
	 * @param productSku the ProductSku instance.
	 * @return DigitalAsset instance
	 */
	DigitalAsset createDigitalAsset(final ProductSku productSku) {
		DigitalAsset digitalAsset = productSku.getDigitalAsset();
		if (digitalAsset == null) {
			digitalAsset = getBean(ContextIdNames.DIGITAL_ASSET);
		}
		return digitalAsset;
	}

	/**
	 * Checks if maxDownloadTimes is negative.
	 * 
	 * @param maxDownloadTimes the maxDownloadTimes parameter
	 */
	void checkDownloadLimit(final Integer maxDownloadTimes) {
		if (maxDownloadTimes == null || maxDownloadTimes < 0) {
			throw new PopulationRuntimeException("IE-10307");
		}
	}

	/**
	 * Checks if expiryDays is negative.
	 * 
	 * @param expiryDays the expiryDays parameter
	 */
	void checkDownloadExpiry(final Integer expiryDays) {
		if (expiryDays == null || expiryDays < 0) {
			throw new PopulationRuntimeException("IE-10308");
		}
	}

	@Override
	public void populateDTO(final ProductSku source, final DigitalAssetItemDTO target) {
		target.setEnabled(source.isDigital());
		
		DigitalAsset digitalAsset = source.getDigitalAsset();
		if (digitalAsset != null) {
			target.setFileName(digitalAsset.getFileName());
			target.setExpiryDays(digitalAsset.getExpiryDays());
			target.setMaxDownloadTimes(digitalAsset.getMaxDownloadTimes());
		}
	}
}
