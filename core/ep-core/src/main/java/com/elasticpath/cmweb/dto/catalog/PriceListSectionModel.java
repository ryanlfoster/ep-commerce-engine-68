package com.elasticpath.cmweb.dto.catalog;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.domain.catalog.Catalog;

/**
 * Backing object for price list sections in the pricing page of the product
 * editor.
 */
public class PriceListSectionModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private final PriceListDescriptorDTO priceListDescriptorDTO;

	private final Set<Catalog> catalogs = new TreeSet<Catalog>(new CatalogNameComparator());

	/**
	 * Constructor.
	 * 
	 * @param priceListDescriptorDTO
	 *            price list descriptor that will be displayed in the section
	 * @param catalog
	 *            one of the catalogs that is connected to the
	 *            priceListDescriptorDTO
	 */
	public PriceListSectionModel(
			final PriceListDescriptorDTO priceListDescriptorDTO,
			final Catalog catalog) {
		this.priceListDescriptorDTO = priceListDescriptorDTO;
		catalogs.add(catalog);
	}

	/**
	 * @return Set of catalogs that are connected to the priceListDescriptor.
	 */
	public Set<Catalog> getCatalogs() {
		return catalogs;
	}

	/**
	 * @return priceListDescriptorDTO object
	 */
	public PriceListDescriptorDTO getPriceListDescriptorDTO() {
		return priceListDescriptorDTO;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof PriceListSectionModel) {
			return ObjectUtils.equals(priceListDescriptorDTO.getGuid(),
					((PriceListSectionModel) obj).getPriceListDescriptorDTO()
							.getGuid());
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int base = 17;
		final int prime = 37;
		int result = base;
		result = prime * result
				+ ObjectUtils.hashCode(priceListDescriptorDTO.getGuid());
		return result;
	}

	/**
	 * @return Comparator that performs comparison of the
	 *         {@link PriceListSectionModel} object by their
	 *         {@link PriceListDescriptorDTO} names
	 */
	public static Comparator<PriceListSectionModel> getDescriptorNameComparator() {
		return new PriceListSectionModelNameComparator();
	}
}
