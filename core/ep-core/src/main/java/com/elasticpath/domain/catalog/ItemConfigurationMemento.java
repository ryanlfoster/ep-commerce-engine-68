package com.elasticpath.domain.catalog;

import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.persistence.api.Entity;

/**
 * Represents the root of an {@link ItemConfiguration}, containing its ID.
 */
public interface ItemConfigurationMemento extends Entity {

	/**
	 * Gets the root item representation.
	 *
	 * @return the root item representation
	 */
	String getItemRepresentation();

	/**
	 * Sets the item representation.
	 *
	 * @param representation the new item representation
	 */
	void setItemRepresentation(String representation);

	/**
	 * Gets the identifier for the item.
	 *
	 * @return the identifier
	 */
	ItemConfigurationId getId();

	/**
	 * Sets the identifier for the item.
	 *
	 * @param itemConfigurationId the new id
	 */
	void setId(ItemConfigurationId itemConfigurationId);

	/**
	 * Represents the identifier of an item configuration.
	 */
	class ItemConfigurationId {
		private final String value;

		/**
		 * Instantiates a new item configuration ID.
		 *
		 * @param value the value of the ID
		 */
		public ItemConfigurationId(final String value) {
			this.value = value;
		}

		/**
		 * Gets the identifier value.
		 *
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		@Override
		public int hashCode() {
			return value.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof ItemConfigurationId) {
				return ObjectUtils.equals(value, ((ItemConfigurationId) obj).value);
			}
			return false;
		}
	}

}
