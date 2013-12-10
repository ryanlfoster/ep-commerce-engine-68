package com.elasticpath.domain.pricing.impl;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.sellingcontext.impl.SellingContextImpl;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/** @see com.elasticpath.domain.pricing.PriceListAssignment */
@Entity
@Table(name = PriceListAssignmentImpl.TABLE_NAME)
public class PriceListAssignmentImpl extends AbstractEntityImpl implements
		PriceListAssignment {

	private static final long serialVersionUID = 88128686384224929L;

	/** Database Table. */
	public static final String TABLE_NAME = "TPRICELISTASSIGNMENT";

	private long uidPk;

	private String name;

	private String description;

	private int priority;

	private Catalog catalog;

	private PriceListDescriptor priceListDescriptor;

	private SellingContext sellingContext;

	private boolean hidden;

	private String guid;
	
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@Basic
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	@Basic
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	@Basic
	@Column(name = "PRIORITY")
	public int getPriority() {
		return priority;
	}

	@Override
	public void setPriority(final int priority) {
		this.priority = priority;
	}

	@Override
	@OneToOne(targetEntity = CatalogImpl.class, cascade = {
			CascadeType.REFRESH, CascadeType.MERGE }, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "CATALOG_UID", nullable = false)
	@ForeignKey(name = "CATPLA_FK")
	public Catalog getCatalog() {
		return catalog;
	}

	@Override
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	@Override
	@OneToOne(targetEntity = PriceListDescriptorImpl.class, cascade = {
			CascadeType.REFRESH, CascadeType.MERGE }, optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "PRLISTDSCR_UID", nullable = false)
	@ForeignKey(name = "PLDPLA_FK")
	public PriceListDescriptor getPriceListDescriptor() {
		return priceListDescriptor;
	}

	@Override
	public void setPriceListDescriptor(
			final PriceListDescriptor priceListDescriptor) {
		this.priceListDescriptor = priceListDescriptor;
	}

	@Override
	@OneToOne(targetEntity = SellingContextImpl.class, cascade = {
		CascadeType.REFRESH, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.PERSIST }, 
			optional = true, fetch = FetchType.EAGER) //PLA can have no SC if unconditional
	@JoinColumn(name = "SELLING_CTX_UID", nullable = true) //so this column is optional and nullable
	@ForeignKey(name = "SCPLA_FK")
	public SellingContext getSellingContext() {
		return sellingContext;
	}

	@Override
	public void setSellingContext(final SellingContext sellingContext) {
		this.sellingContext = sellingContext;
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
	 * @param guid
	 *            the GUID to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	@Override
	@Basic
	@Column(name = "HIDDEN")
	public boolean isHidden() {
		return hidden;
	}

	@Override
	public void setHidden(final boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PriceListAssignment)) {
			return false;
		}

		return super.equals(obj);
	}

    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public int hashCode() {
    	return super.hashCode();
    }

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[PriceListAssignment: UIDPK=");
		buffer.append(getUidPk());
		buffer.append(" Name=");
		buffer.append(getName());
		if (getPriceListDescriptor() == null) {
			buffer.append("PriceListDescriptor is null");
		} else {
			buffer.append(" PriceListDescriptor GUID=");
			buffer.append(getPriceListDescriptor().getGuid());			
		}
		if (getCatalog() == null) {
			buffer.append(" Catalog is null");
		} else {
			buffer.append(" Catalog GUID=");
			buffer.append(getCatalog().getGuid());
		}
		buffer.append(']');
		return buffer.toString(); 
	}

}
