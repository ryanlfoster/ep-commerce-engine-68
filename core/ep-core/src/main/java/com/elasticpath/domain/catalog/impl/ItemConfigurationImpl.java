package com.elasticpath.domain.catalog.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.ItemConfigurationValidationException;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.ItemConfigurationBuilder;
import com.elasticpath.service.catalog.ItemConfigurationValidationResult;
import com.elasticpath.service.catalog.ItemConfigurationValidationResult.ItemConfigurationValidationStatus;
import com.elasticpath.service.catalog.ItemConfigurationValidator;

/**
 * Default implementation of a {@link ItemConfiguration}.
 */
public class ItemConfigurationImpl implements ItemConfiguration, Cloneable {

	private boolean selected;
	private final String itemId;
	private final SortedMap<String, ItemConfiguration> children =
		new TreeMap<String, ItemConfiguration>(String.CASE_INSENSITIVE_ORDER);

	private String skuCode;
	@Override
	public List<ItemConfiguration> getChildren() {
		List<ItemConfiguration> values = new ArrayList<ItemConfiguration>(children.values());
		return Collections.unmodifiableList(values);
	}

	@Override
	public ItemConfiguration getChildById(final String childId) {
		return children.get(childId);
	}

	@Override
	public ItemConfiguration getChildByPath(final List<String> childPath) {
		ItemConfiguration current = this;
		for (String childId : childPath) {
			current = current.getChildById(childId);
			if (current == null) {
				return null;
			}
		}
		return current;
	}

	@Override
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * Gets a map of the children. Implemented to allow JSON de-serialization. Do *NOT* use in other contexts.
	 * @return the children, as an unmodifiable map.
	 */
	public Map<String, ItemConfiguration> getChildrenMap() {
		return Collections.unmodifiableMap(children);
	}

	/**
	 * Sets the SKU code.
	 *
	 * @param skuCode the new SKU code
	 */
	private void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * Constructor.
	 *
	 * @param skuCode the SKU code
	 * @param children the children
	 * @param selected whether this item is selected
	 * @param itemId this item's ID, bundle constituent's GUID in the case of a bundle constituent.
	 */
	public ItemConfigurationImpl(final String skuCode, final Map<String, ItemConfiguration> children,
			final boolean selected, final String itemId) {
		this.skuCode = skuCode;
		this.children.putAll(children);
		this.selected = selected;
		this.itemId = itemId;
	}

	private void setSelected(final boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	public String getItemId() {
		return itemId;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(skuCode)
			.append(selected)
			.append(itemId);
		for (Map.Entry<String, ItemConfiguration> entry : children.entrySet()) {
			builder.append(entry.getKey()).append(entry.getValue());
		}
		return builder.toHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ItemConfigurationImpl)) {
			return false;
		}
		ItemConfigurationImpl other = (ItemConfigurationImpl) obj;
		return new EqualsBuilder()
			.append(skuCode, other.skuCode)
			.append(selected, other.selected)
			.append(itemId, other.itemId)
			.append(children, other.children)
			.isEquals();
	}

	@SuppressWarnings("PMD.ProperCloneImplementation")
	@Override
	public ItemConfigurationImpl clone() throws CloneNotSupportedException {
		Map<String, ItemConfiguration> newChildren = new TreeMap<String, ItemConfiguration>(String.CASE_INSENSITIVE_ORDER);
		for (Map.Entry<String, ItemConfiguration> childEntry : children.entrySet()) {
			ItemConfigurationImpl child = (ItemConfigurationImpl) childEntry.getValue();
			newChildren.put(childEntry.getKey(), child.clone());
		}
		return new ItemConfigurationImpl(skuCode, newChildren, selected, itemId);
	}


	/**
	 * Builds {@link ItemConigurationImpl}s.
	 */
	public static class Builder implements ItemConfigurationBuilder {

		private ItemConfigurationImpl rootItem;
		private final ItemConfigurationValidator validator;

		private ItemConfigurationValidationResult validationResult = null;

		private boolean dirty = false;

		/**
		 * Instantiates a new builder.
		 *
		 * @param rootItem the root item
		 * @param validator the validator
		 */
		public Builder(final ItemConfigurationImpl rootItem, final ItemConfigurationValidator validator) {
			this.rootItem = rootItem;
			this.validator = validator;
		}

		private void markDirty() {
			validationResult = null;
			if (!dirty) {
				dirty = true;
				try {
					rootItem = rootItem.clone();
				} catch (CloneNotSupportedException e) {
					throw new EpServiceException("could not clone ItemConfigurationBuilderImpl", e);
				}
			}
		}

		private void markClean() {
			dirty = false;
		}

		@Override
		public ItemConfiguration build() {
			ItemConfigurationValidationStatus status = validate().getStatus();
			if (status.isSuccessful()) {
				markClean();
				return rootItem;
			}
			throw new ItemConfigurationValidationException("Could not validate the item configuration because of: " + status);
		}

		@Override
		public ItemConfigurationBuilder select(final List<String> path, final String skuCode) {
			markDirty();
			getChildAt(path, true).setSkuCode(skuCode);
			return this;
		}

		@Override
		public ItemConfigurationBuilder deselect(final List<String> path) {
			markDirty();
			getChildAt(path, false).setSelected(false);
			return this;
		}

		private ItemConfigurationImpl getChildAt(final List<String> path, final boolean setSelected) {
			ItemConfigurationImpl currentItem = rootItem;
			int index = 0;
			for (String pathSegment : path) {
				ItemConfigurationImpl child = (ItemConfigurationImpl) currentItem.getChildById(pathSegment);
				if (child == null) {
					throw new ItemConfigurationValidationException(String.format("Invalid path segment %s at index %d. The passed path was %s.",
							pathSegment, index, path));
				}
				if (setSelected) {
					child.setSelected(true);
				}
				currentItem = child;
				++index;
			}
			return currentItem;
		}

		@Override
		public ItemConfigurationValidationResult validate() {
			if (validationResult == null) {
				validationResult = validator.validate(rootItem);
			}
			return validationResult;
		}
	}
}
