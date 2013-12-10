package com.elasticpath.domain.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.persistence.api.AbstractPersistableImpl;


/**
 * Abstract item data value object.
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SuppressWarnings({ "PMD.AbstractClassWithoutAbstractMethod" })
public abstract class AbstractItemData extends AbstractPersistableImpl {

	private static final long serialVersionUID = -975364266195148117L;

	private String key;
	private String value;
	
	/**
	 * Constructor.
	 * @param key the key
	 * @param value the value
	 */
	public AbstractItemData(final String key, final String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * @return the key
	 */
	@Basic
	@Column(name = "ITEM_KEY")
	@Lob
	public String getKey() {
		return key;
	}
	
	/**
	 * Set the key.
	 * @param key the key to set
	 */
	protected void setKey(final String key) {
		this.key = key;
	}
	
	/**
	 * @return the value
	 */
	@Basic
	@Column(name = "ITEM_VALUE")
	@Lob
	public String getValue() {
		return value;
	}
	
	/**
	 * Set the value.
	 * @param value the value to set
	 */
	protected void setValue(final String value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof AbstractItemData)) {
			return false;
		}
		
		AbstractItemData data = (AbstractItemData) other;
		return ObjectUtils.equals(this.key, data.key);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.key);
	}

}
