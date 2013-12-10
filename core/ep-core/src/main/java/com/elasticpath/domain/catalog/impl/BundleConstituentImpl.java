/*
 * Copyright (c) Elastic Path Software Inc., 2009.
 */

package com.elasticpath.domain.catalog.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductConstituent;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductSkuConstituent;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.domain.pricing.impl.PriceAdjustmentImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Implementation of ProductBundleConstituent interface.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals" })
@Entity
@Table(name = BundleConstituentImpl.TABLE_NAME)
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.BUNDLE_CONSTITUENTS, attributes = {
				@FetchAttribute(name = "productConstituent", recursionDepth = -1),
				@FetchAttribute(name = "skuConstituent", recursionDepth = -1),
				@FetchAttribute(name = "priceAdjustments", recursionDepth = -1) }, 
				postLoad = true),
		@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = {
				@FetchAttribute(name = "productConstituent", recursionDepth = -1),
				@FetchAttribute(name = "skuConstituent", recursionDepth = -1),
				@FetchAttribute(name = "priceAdjustments", recursionDepth = -1),
				@FetchAttribute(name = "quantity", recursionDepth = -1)
				},
				postLoad = true),
		@FetchGroup(name = FetchGroupConstants.SHOPPING_ITEM_CHILD_ITEMS, attributes = {
				@FetchAttribute(name = "productConstituent", recursionDepth = -1),
				@FetchAttribute(name = "skuConstituent", recursionDepth = -1),
				@FetchAttribute(name = "priceAdjustments", recursionDepth = -1) },
				postLoad = true),
		@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT, attributes = {
				@FetchAttribute(name = "productConstituent", recursionDepth = -1),
				@FetchAttribute(name = "skuConstituent", recursionDepth = -1),
				@FetchAttribute(name = "priceAdjustments", recursionDepth = -1) },
				postLoad = true) })
public class BundleConstituentImpl extends AbstractLegacyEntityImpl implements Serializable, BundleConstituent {

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5000000001L;

    /**
     * The name of the table & generator to use for persistence.
     */
    public static final String TABLE_NAME = "TBUNDLECONSTITUENTX";
    
    /** number 17. */
    protected static final int PRIME_NUMBER_SEVENTEEN = 17;
    
    /** number 37. */
    protected static final int PRIME_NUMBER_THIRTY_SEVEN = 37;

    private long uidPk;
    private Product productConstituent;
    private ProductSku skuConstituent;
    private Integer quantity;
    private Integer ordering;
    private List<PriceAdjustment> priceAdjustments = new ArrayList<PriceAdjustment>();
    private ConstituentItem constituentItem;

	private String guid;
    
    /**
     * @return the constituent product in the association
     */
    @OneToOne(targetEntity = ProductImpl.class,
              fetch = FetchType.EAGER,
              cascade = { CascadeType.REFRESH, CascadeType.MERGE }
    )
    @JoinColumn(name = "CONSTITUENT_UID")
    @ForeignKey
    public Product getProductConstituent() {
        return this.productConstituent;
    }

    /**
     * Sets the constituent product in the association.
     *
     * @param constituent the constituent to be set
     */
    public void setProductConstituent(final Product constituent) {
        this.productConstituent = constituent;
    }

    /**
     * @return the constituent sku in the association
     */
    @OneToOne(targetEntity = ProductSkuImpl.class,
    		fetch = FetchType.EAGER,
    		cascade = { CascadeType.REFRESH, CascadeType.MERGE }
    )
    @JoinColumn(name = "CONSTITUENT_SKU_UID")
    @ForeignKey
    public ProductSku getSkuConstituent() {
    	return this.skuConstituent;
    }
    
    /**
     * Sets the constituent sku in the association.
     *
     * @param constituent the constituent to be set
     */
    public void setSkuConstituent(final ProductSku constituent) {
    	this.skuConstituent = constituent;
    }

    /**
     * @return the quantity of the constituent item
     */
    @Basic
    @Column(name = "QUANTITY")
    public Integer getQuantity() {
        return this.quantity;
    }

    /**
     * Sets the quantity of the constituent item.
     *
     * @param quantity the integer quantity to be set
     */
    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the price adjustments assigned to this bundle constituent
     */
	@OneToMany(targetEntity = PriceAdjustmentImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "CONSTITUENT_GUID", referencedColumnName = "GUID", nullable = false)
	@ElementForeignKey
	@ElementDependent
    public List<PriceAdjustment> getPriceAdjustments() {
        return this.priceAdjustments;
    }
    
	@Override
	public void addPriceAdjustment(final PriceAdjustment adjustment) {
		getPriceAdjustments().add(adjustment);
	}

	@Override
	public void removePriceAdjustment(final PriceAdjustment adjustment) {
		getPriceAdjustments().remove(adjustment);
	}

	/**
     * @param priceAdjustments the price adjustments to be assigned to this bundle constituent
     */
    protected void setPriceAdjustments(final List<PriceAdjustment> priceAdjustments) {
    	this.priceAdjustments = priceAdjustments;
    }
    
    /**
     * @return the UIDPK of the object
     */
    @Id
    @Column(name = "UIDPK")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
    @TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
    public long getUidPk() {
        return this.uidPk;
    }

    /**
     * Sets the UIDPK of the object.
     *
     * @param uidPk the new unique identifier.
     */
    public void setUidPk(final long uidPk) {
        this.uidPk = uidPk;
    }

	@Override
    public boolean equals(final Object other) {
        if (other instanceof BundleConstituentImpl) {
        	return super.equals(other);
        }

        return false;
    }
    
    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public int hashCode() {
    	return super.hashCode();
    }
    
	/**
	 * @return the GUID.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	/**
	 * @param guid the GUID to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	@Override
	@Basic
	@Column(name = "ORDERING")
	public Integer getOrdering() {
		return this.ordering;
	}

	@Override
	public void setOrdering(final Integer ordering) {
		this.ordering = ordering;
	}

	@Override
	@Transient
	public ConstituentItem getConstituent() {
		return constituentItem;
	}

	@Override
	public void setConstituent(final Product product) {
		setProductConstituent(product);
		setSkuConstituent(null);
		constituentItem = getBean("productConstituent");
		((ProductConstituent) constituentItem).setProduct(product);
	}
	
	@Override
	public void setConstituent(final ProductSku productSku) {
		setProductConstituent(null);
		setSkuConstituent(productSku);
		constituentItem = getBean("productSkuConstituent");
		((ProductSkuConstituent) constituentItem).setProductSku(productSku);
	}
	
	/**
	 * Ensure after load that the constituent item is set correctly.
	 */
	@PostLoad
	public void setConstituentItem() {
		if (productConstituent != null) {
			constituentItem = getBean("productConstituent");
			((ProductConstituent) constituentItem).setProduct(productConstituent);
			return;
		} 
		
		if (skuConstituent != null) {
			constituentItem = getBean("productSkuConstituent");
			((ProductSkuConstituent) constituentItem).setProductSku(skuConstituent);
		}
	}
	
	@Override
	@Transient
	public PriceAdjustment getPriceAdjustmentForPriceList(final String plGuid) {
		for (PriceAdjustment adjustment : getPriceAdjustments()) {
			if (ObjectUtils.equals(plGuid, adjustment.getPriceListGuid())) {
				return adjustment;
			}
		}
		return null;
	}
	
	
	
}
