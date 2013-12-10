package com.elasticpath.domain.catalog.impl;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.elasticpath.domain.catalog.ItemConfiguration;

/**
 * This class is an annotation mix-in to allow marshalling the {@link ItemConfigurationImpl}.
 */
@SuppressWarnings("PMD.AbstractNaming")
@JsonPropertyOrder({ "itemId", "skuCode", "selected", "children" })
public abstract class ItemConfigurationImplJacksonMixIn extends ItemConfigurationImpl {

	/**
	 * Constructor.
	 *
	 * @param skuCode the sku code
	 * @param children the children
	 * @param selected the selected
	 * @param itemId the item id
	 */
	public ItemConfigurationImplJacksonMixIn(@JsonProperty("skuCode") final String skuCode,
			@JsonProperty("children") @JsonDeserialize(contentAs = ItemConfigurationImpl.class)
			final Map<String, ItemConfiguration> children,
			@JsonProperty("selected") final boolean selected,
			@JsonProperty("itemId") final String itemId) {
		super(skuCode, children, selected, itemId);
	}

	@JsonIgnore
	@Override
	public abstract List<ItemConfiguration> getChildren();

	@JsonIgnore
	@Override
	public abstract ItemConfiguration getChildById(final String childId);

	@JsonIgnore
	@Override
	public abstract ItemConfiguration getChildByPath(final List<String> childPath);

	@JsonProperty("skuCode")
	@Override
	public abstract String getSkuCode();

	/**
	 * Gets a map of the children. Implemented to allow JSON de-serialization. Do *NOT* use in other contexts.
	 * @return the children, as an unmodifiable map.
	 */
	@JsonProperty("children")
	@Override
	public abstract Map<String, ItemConfiguration> getChildrenMap();

	@Override
	@JsonProperty("selected")
	public abstract boolean isSelected();

	@JsonProperty("itemId")
	@Override
	public abstract String getItemId();
}
