package com.elasticpath.domain.catalog.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.Unique;

import com.elasticpath.domain.catalog.ItemConfigurationMemento;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Default implementation of {@link RootItemConfiguration}.
 */
@Entity
@Table(name = ItemConfigurationMementoImpl.TABLE_NAME)
@DataCache(enabled = true)
public class ItemConfigurationMementoImpl extends AbstractEntityImpl implements ItemConfigurationMemento {
	private static final long serialVersionUID = 1L;

	/** Allocation size for JPA_GENERATED_KEYS id. */
	private static final int ALLOCATION_SIZE = 1000;

	/** The table name. */
	public static final String TABLE_NAME = "TITEMCONFIGURATION";

	private String itemRepresentation;

	private long uidPk;

	private String guid;


	@Override
	@Transient
	public ItemConfigurationId getId() {
		return new ItemConfigurationId(getGuid());
	}

	/**
	 * Sets the id.
	 *
	 * @param itemConfigurationId the new id
	 */
	@Override
	public void setId(final ItemConfigurationId itemConfigurationId) {
		setGuid(itemConfigurationId.getValue());
	}

	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(name = "ITEM_REPRESENTATION")
	@Override
	public String getItemRepresentation() {
		return itemRepresentation;
	}

	@Override
	public void setItemRepresentation(final String itemRepresentation) {
		this.itemRepresentation = itemRepresentation;
	}

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(
			strategy = GenerationType.TABLE,
			generator = TABLE_NAME)
	@TableGenerator(
			name = TABLE_NAME,
			table = "JPA_GENERATED_KEYS",
			pkColumnName = "ID",
			valueColumnName = "LAST_VALUE",
			pkColumnValue = TABLE_NAME,
			allocationSize = ALLOCATION_SIZE)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Basic
	@Column(name = "GUID")
	@Unique
	@Override
	public String getGuid() {
		return guid;
	}

	@SuppressWarnings("PMD.UnnecessaryOverride")
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

}
