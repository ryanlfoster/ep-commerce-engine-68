package com.elasticpath.domain.contentspace.impl;


import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.Unique;

import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Defines an area where DynamicContent can be displayed.
 * This implementation specifies JPA persistence annotations.
 */
@Entity
@Table(name = ContentSpaceImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ContentSpaceImpl extends AbstractEntityImpl implements ContentSpace {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20090127L;
	
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCSCONTENTSPACE";
	
	private long uidPk;
	
	private String targetId;
	private String description;


	private String guid;
	
	
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
	@Basic
	@Column(name = "TARGET_ID", unique = true, nullable = false)
	@Unique(name = "TCSCONTENTSPACE_UNIQUE_TARGET")
	public String getTargetId() {
		return targetId;
	}

	@Override
	public void setTargetId(final String targetId) {
		this.targetId = targetId;
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
	
    /**
     * @return the guid.
     */
    @Override
    @Basic
    @Column(name = "GUID")
    @Unique(name = "TCSCONTENTSPACE_UNIQUE")
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
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof ContentSpaceImpl)) {
			return false;
		}
		
		ContentSpaceImpl space = (ContentSpaceImpl) other;
		return ObjectUtils.equals(targetId, space.targetId);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(targetId);
	}

}
