package com.elasticpath.service.pricing.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.service.pricing.dao.BaseAmountDao;
import com.elasticpath.service.pricing.dao.PriceListDescriptorDao;

/**
 * Service for managing PriceListDescriptors.
 * Descriptors of price lists are managed separately from {@link com.elasticpath.domain.pricing.BaseAmount BaseAmounts}
 */
public class PriceListDescriptorServiceImpl implements PriceListDescriptorService {

	/** PriceListDescriptorDao for database operations. */
	private PriceListDescriptorDao priceListDescriptorDao;
	/** BaseAmount Dao. */
	private BaseAmountDao baseAmountDao;

	@Override
	public void delete(final PriceListDescriptor priceListDescriptor) {
		if (priceListDescriptor == null) {
			throw new EpServiceException("Attempt to delete a null PriceListDescriptor.");
		}
		try {
			baseAmountDao.delete(priceListDescriptor.getGuid());
			priceListDescriptorDao.delete(priceListDescriptor);
		} catch (Exception e) {
			throw new EpServiceException("Failed to delete PriceListDescriptor, GUID: [" 
					+ priceListDescriptor.getGuid() + "].", e);
		}
	}

	@Override
	public PriceListDescriptor findByGuid(final String priceListDescriptorGuid) {
		try {
			return priceListDescriptorDao.findByGuid(priceListDescriptorGuid);
		} catch (EpPersistenceException ex) {
			throw new EpServiceException("Unable to get the descriptor with GUID " + priceListDescriptorGuid, ex);
		}
	}

	/**
	 * Use the priceListDescriptorDao to add the descriptor.
	 * GUID should already be assigned by this point.
	 * 
	 * @param priceListDescriptor the object to save or update
	 * @return persisted instance after save
	 * @throws EpServiceException if no GUID assigned.
	 */
	public PriceListDescriptor add(final PriceListDescriptor priceListDescriptor) {
		if (StringUtils.isEmpty(priceListDescriptor.getGuid())) {
			throw new EpServiceException("No GUID assigned");
		}
		return priceListDescriptorDao.add(priceListDescriptor);
	}
	
	/**
	 * Use the priceListDescriptorDao to update the descriptor.
	 * 
	 * @param priceListDescriptor the object to save or update
	 * @return persisted instance after save
	 * @throws EpServiceException if GUID is null or doesn't correspond to any persisted descriptor
	 */
	public PriceListDescriptor update(final PriceListDescriptor priceListDescriptor) {
		PriceListDescriptor stored = this.findByGuid(priceListDescriptor.getGuid());
		if (stored == null) {
			throw new EpServiceException("Could not find price list descriptor" + priceListDescriptor.getGuid());
		}
		copyDataFields(priceListDescriptor, stored);
		return priceListDescriptorDao.update(stored);
	}

	/**
	 * Copy fields from one descriptor to another. Ignoring any persistence related fields.
	 * 
	 * @param fromDto the descriptor to copy from
	 * @param toDto the descriptor to copy to
	 */
	protected void copyDataFields(final PriceListDescriptor fromDto,
			final PriceListDescriptor toDto) {
		toDto.setCurrencyCode(fromDto.getCurrencyCode());
		toDto.setName(fromDto.getName());
		toDto.setDescription(fromDto.getDescription());
	}

	@Override
	public List<PriceListDescriptor> getPriceListDescriptors() {
		return priceListDescriptorDao.getPriceListDescriptors(false);
	}

	@Override
	public List<PriceListDescriptor> getPriceListDescriptors(final boolean includeHidden) {
		return priceListDescriptorDao.getPriceListDescriptors(includeHidden);
	}

	@Override
	public List<PriceListDescriptor> getPriceListDescriptors(final Collection<String> priceListDescriptorsGuids) {
			return priceListDescriptorDao.getPriceListDescriptors(priceListDescriptorsGuids);
	}
	
	/**
	 * @return the DAO
	 */
	public PriceListDescriptorDao getPriceListDescriptorDao() {
		return priceListDescriptorDao;
	}

	/**
	 * @param descriptorDao <code>PriceListDescriptorDao</code> to use
	 */
	public void setPriceListDescriptorDao(final PriceListDescriptorDao descriptorDao) {
		this.priceListDescriptorDao = descriptorDao;
	}

	/**
	 * @param baseAmountDao the baseAmountDao to set
	 */
	public void setBaseAmountDao(final BaseAmountDao baseAmountDao) {
		this.baseAmountDao = baseAmountDao;
	}

	/**
	 * Get the PriceListDescriptor instance with the given name. Returns null if not found.
	 *
	 * @param name name of the PriceListDescriptor to be found
	 * @return PriceListDescriptor if found, or null.
	 */
	public PriceListDescriptor findByName(final String name) {
		return priceListDescriptorDao.findByName(name);
	}
	
	@Override
	public boolean isPriceListNameUnique(final String guid, final String name) {
		PriceListDescriptor priceListDescriptorPersistent = findByName(name);
		if (priceListDescriptorPersistent == null) {
			return true;
		}
		if (priceListDescriptorPersistent.getGuid().equals(guid)) {
			return true;
		}
		return false;
	}
}
