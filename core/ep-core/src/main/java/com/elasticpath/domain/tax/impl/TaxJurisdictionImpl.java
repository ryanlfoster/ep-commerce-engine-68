/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.tax.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * A TaxJurisdiction represents a geographic area that has it's own distinct set of <code>TaxCategory</code>s and <code>TaxValue</code>s ie a
 * Country, a State, a City, a Municipal, or a County.
 */
@Entity
@Table(name = TaxJurisdictionImpl.TABLE_NAME)
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.STORE_FOR_EDIT, attributes = {
				@FetchAttribute(name = "guid")
		})
})
@DataCache(enabled = true)
public class TaxJurisdictionImpl extends AbstractEntityImpl implements TaxJurisdiction {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TTAXJURISDICTION";

	private String regionCode;

	private Boolean priceCalculationMethod;

	private Set<TaxCategory> taxCategorySet = new HashSet<TaxCategory>();

	private long uidPk;

	private String guid;

	/**
	 * The constructor.
	 */
	public TaxJurisdictionImpl() {
		// default constructor.
	}

	/**
	 * The copy constructor. Does shallow copy of existing jurisdiction.
	 * 
	 * @param taxJurisdiction taxJurisdiction to be shallow cloned
	 */
	public TaxJurisdictionImpl(final TaxJurisdiction taxJurisdiction) {
		this.regionCode = taxJurisdiction.getRegionCode();
		this.priceCalculationMethod = taxJurisdiction.getPriceCalculationMethod();
	}

	@Override
	@Basic
	@Column(name = "REGION_CODE")
	public String getRegionCode() {
		return regionCode;
	}

	@Override
	public void setRegionCode(final String regionCode) {
		this.regionCode = regionCode;
	}

	@Override
	@Basic
	@Column(name = "PRICE_CALCULATION_METH")
	public Boolean getPriceCalculationMethod() {
		return this.priceCalculationMethod;
	}

	@Override
	public void setPriceCalculationMethod(final Boolean priceCalculationMethod) {
		this.priceCalculationMethod = priceCalculationMethod;
	}

	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID", nullable = false, length = GUID_LENGTH)
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 * 
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	@Override
	@OneToMany(targetEntity = TaxCategoryImpl.class, cascade = { CascadeType.ALL },
			fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "TAX_JURISDICTION_UID")
	@ElementForeignKey
	@ElementDependent
	public Set<TaxCategory> getTaxCategorySet() {
		return taxCategorySet;
	}

	@Override
	public void setTaxCategorySet(final Set<TaxCategory> taxCategorySet) {
		this.taxCategorySet = taxCategorySet;
	}

	@Override
	public void addTaxCategory(final TaxCategory taxCategory) throws EpDomainException {
		Set<TaxCategory> taxCategorySet = getTaxCategorySet();
		if (taxCategorySet.contains(taxCategory)) {
			throw new EpDomainException("The category of the same name " + taxCategory.getName() + " already exist.");
		}

		taxCategorySet.add(taxCategory);
	}

	@Override
	public TaxCategory getTaxCategory(final String taxCategoryName) {
		for (TaxCategory taxCategory : getTaxCategorySet()) {
			if (taxCategory.getName().equals(taxCategoryName)) {
				return taxCategory;
			}
		}
		return null;
	}

	@Override
	public TaxCategory removeTaxCategory(final String taxCategoryName) {
		for (TaxCategory taxCategory : getTaxCategorySet()) {
			if (taxCategory.getName().equals(taxCategoryName)) {
				getTaxCategorySet().remove(taxCategory);
				return taxCategory;
			}
		}
		return null;
	}

	@Override
	public TaxCategory removeTaxCategory(final TaxCategory taxCategory) {
		return removeTaxCategory(taxCategory.getName());
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	
	@Override
	public int hashCode() {
    	return new HashCodeBuilder().
        	append(getGuid()).
        	toHashCode();
	}

    @Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		
		TaxJurisdictionImpl other = (TaxJurisdictionImpl) obj;
		
		return new EqualsBuilder().
        	append(getGuid(), other.getGuid()).
        	isEquals();
	}

	/**
     * String representation of this object.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
            final StringBuffer sbf = new StringBuffer();
            sbf.append("TaxJuristdiction -> Region Code: ").append(this.getRegionCode());
            sbf.append(" Tax Categories: ").append(this.getTaxCategorySet());
            return sbf.toString();
    }
	
}
