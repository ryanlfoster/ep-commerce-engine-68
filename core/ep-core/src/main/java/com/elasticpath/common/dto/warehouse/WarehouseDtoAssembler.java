package com.elasticpath.common.dto.warehouse;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.WarehouseAddress;

/**
 * DTO Assembler for Warehouses.
 */
public class WarehouseDtoAssembler extends AbstractDtoAssembler<WarehouseDTO, Warehouse> {

	private BeanFactory beanFactory;

	@Override
	public Warehouse getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.WAREHOUSE);
	}

	@Override
	public WarehouseDTO getDtoInstance() {
		return new WarehouseDTO();
	}

	/**
	 * Overridable factory method.
	 * 
	 * @return a WarehouseAddress from the Spring context.
	 */
	protected WarehouseAddress warehouseAddressDomainFactory() {
		return beanFactory.getBean(ContextIdNames.WAREHOUSE_ADDRESS);
	}

	@Override
	public void assembleDto(final Warehouse source, final WarehouseDTO target) {
		target.setCity(source.getAddress().getCity());
		target.setCode(source.getCode());
		target.setCountry(source.getAddress().getCountry());
		target.setName(source.getName());
		target.setPickDelay(source.getPickDelay());
		target.setStreet1(source.getAddress().getStreet1());
		target.setStreet2(source.getAddress().getStreet2());
		target.setSubCountry(source.getAddress().getSubCountry());
		target.setZipOrPostalCode(source.getAddress().getZipOrPostalCode());
	}

	@Override
	public void assembleDomain(final WarehouseDTO source, final Warehouse target) {
		WarehouseAddress address;

		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setPickDelay(source.getPickDelay());

		if (target.getAddress() != null && target.getAddress().isPersisted()) {
			address = target.getAddress();
		} else {
			address = warehouseAddressDomainFactory();
		}

		address.setCity(source.getCity());
		address.setCountry(source.getCountry());
		address.setStreet1(source.getStreet1());
		address.setStreet2(source.getStreet2());
		address.setSubCountry(source.getSubCountry());
		address.setZipOrPostalCode(source.getZipOrPostalCode());

		target.setAddress(address);

	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
