package com.elasticpath.importexport.common.adapters.products.data;

import java.math.BigDecimal;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.products.ShippableItemDTO;
import com.elasticpath.importexport.common.dto.products.UnitDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.settings.SettingsReader;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between <code>ProductSku</code>
 * and <code>ShippableItemDTO</code> objects.
 */
public class ShippableItemAdapter extends AbstractDomainAdapterImpl<ProductSku, ShippableItemDTO> {
	
	private SettingsReader settingsReader;

	@Override
	public void populateDomain(final ShippableItemDTO source, final ProductSku target) {
		target.setShippable(source.isEnabled());
		if (source.isEnabled()) {
			checkNegative(source.getWeight().getValue(), "Weight");
			checkNegative(source.getWidth().getValue(),  "Width");
			checkNegative(source.getLength().getValue(), "Length");
			checkNegative(source.getHeight().getValue(), "Height");
			
			target.setWeight(source.getWeight().getValue());
			target.setWidth(source.getWidth().getValue());
			target.setLength(source.getLength().getValue());
			target.setHeight(source.getHeight().getValue());
		}
	}

	/**
	 * Checks value with name to negativeness. 
	 * 
	 * @param value the decimal value
	 * @param name the name of the value
	 */
	void checkNegative(final BigDecimal value, final String name) {
		if (value.compareTo(BigDecimal.ZERO) < 0) {			
			throw new PopulationRuntimeException("IE-10317", name);
		}
	}

	@Override
	public void populateDTO(final ProductSku source, final ShippableItemDTO target) {
		target.setEnabled(source.isShippable());
		if (source.isShippable()) {
			final String unitsWeight = getSettingsReader().getSettingValue("COMMERCE/SYSTEM/UNITS/weight").getValue();
			final String unitsLength = getSettingsReader().getSettingValue("COMMERCE/SYSTEM/UNITS/length").getValue();
			target.setWeight(new UnitDTO(unitsWeight, source.getWeight()));
			target.setWidth(new UnitDTO(unitsLength, source.getWidth()));
			target.setLength(new UnitDTO(unitsLength, source.getLength()));
			target.setHeight(new UnitDTO(unitsLength, source.getHeight()));
		}
	}

	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}

	protected SettingsReader getSettingsReader() {
		return settingsReader;
	}
}
