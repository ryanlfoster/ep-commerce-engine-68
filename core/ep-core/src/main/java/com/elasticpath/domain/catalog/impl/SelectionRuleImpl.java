package com.elasticpath.domain.catalog.impl;

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

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * SelectionRule represents selection rule policy for each bundle. E.g SELECT_ALL, SELECT_ONE, SELECT_MULTIPLE.
 */
@Entity
@Table(name = SelectionRuleImpl.TABLE_NAME)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = { @FetchAttribute(name = SelectionRuleImpl.FETCH_PARAMETER) }),
	@FetchGroup(name = FetchGroupConstants.SHOPPING_ITEM_CHILD_ITEMS, attributes = { @FetchAttribute(name = SelectionRuleImpl.FETCH_PARAMETER) }),
	@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT, attributes = { @FetchAttribute(name = SelectionRuleImpl.FETCH_PARAMETER) }),
	@FetchGroup(name = FetchGroupConstants.BUNDLE_CONSTITUENTS, attributes = { @FetchAttribute(name = SelectionRuleImpl.FETCH_PARAMETER) })
})
public class SelectionRuleImpl extends AbstractEntityImpl implements SelectionRule {

	private static final long serialVersionUID = 3977098238092263030L;

	/**
	 * Table name for bundle selection rules.
	 */
	public static final String TABLE_NAME = "TBUNDLESELECTIONRULE";
	
	/**
	 * Field name for attribute parameter.
	 */
	protected static final String FETCH_PARAMETER = "parameter";

	private long uidPk;
	private int parameter;
	private ProductBundle bundle;

	private String guid;
	
	/**
	 * Default constructor.
	 * It creates a SELECT_ALL rule, by default SELECT_ALL has no argument (parameter = 0). 
	 */
	public SelectionRuleImpl() {
		this.parameter = 0;
	}
	
	/**
	 * A constructor that initialize the rule with given number.
	 * This constructor is meant to use for SELECT_MULTIPLE.
	 * @param parameter the quantity
	 */
	public SelectionRuleImpl(final int parameter) {
		this.parameter = parameter;
	}
	
	/**
	 * Gets the guid.
	 *
	 * @return the GUID.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	/**
	 * Sets the guid.
	 *
	 * @param guid the GUID to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}
	
	@Override
	@Basic
	@Column(name = "PARAMETER")
	public int getParameter() {
		return parameter;
	}

	/**
	 * A setter for JPA.
	 * @param param the quantity
	 */
	public void setParameter(final int param) {
		this.parameter = param;
	}

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	
    /**
     * Gets the bundle.
     *
     * @return the bundle product in the association
     */
    @OneToOne(targetEntity = ProductBundleImpl.class,
              optional = false,
              fetch = FetchType.EAGER,
              cascade = { CascadeType.REFRESH, CascadeType.MERGE }
    )
    @JoinColumn(name = "BUNDLE_UID")
    @ForeignKey
    public ProductBundle getBundle() {
        return this.bundle;
    }
    

    /**
     * Sets the bundle product in the association.
     *
     * @param bundle the bundle to be set
     */
    public void setBundle(final ProductBundle bundle) {
        this.bundle = bundle;
    }

	/**
	 * Generate hashcode from bundle and parameter.
	 * 
	 * @return the hashcode
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = ObjectUtils.hashCode(bundle);
		result = prime * result + parameter;
		return result;
	}

	/**
	 * Two rules are equal if the bundle and selection rule are equal.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof SelectionRuleImpl)) {
			return false;
		}
		SelectionRuleImpl other = (SelectionRuleImpl) obj;
		return (parameter == other.parameter && ObjectUtils.equals(bundle, other.bundle));
	}	
}
