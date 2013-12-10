package com.elasticpath.service.catalog.impl;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.jpa.JpaSystemException;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.ItemConfigurationMemento;
import com.elasticpath.domain.catalog.ItemConfigurationMemento.ItemConfigurationId;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ItemConfigurationImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.ItemConfigurationBuilder;
import com.elasticpath.service.catalog.ItemConfigurationFactory;
import com.elasticpath.service.catalog.ItemConfigurationMementoService;
import com.elasticpath.service.catalog.ItemConfigurationService;
import com.elasticpath.service.catalog.ItemConfigurationValidator;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * Provides CRUD operations for the {@link ItemConfiguration}.
 */
public class ItemConfigurationServiceImpl extends AbstractEpServiceImpl implements ItemConfigurationService {
	private static final Logger LOG = Logger.getLogger(ItemConfigurationServiceImpl.class);
	private static final String CANNOT_DESERIALIZE_ERROR = "cannot de-serialize item configuration";
	private static final String CANNOT_SERIALIZE_ERROR = "cannot serialize item configuration";

	private ItemConfigurationFactory itemConfigurationFactory;
	private ItemConfigurationMementoService itemConfigurationMementoService;
	private ItemConfigurationValidator itemConfigurationValidator;
	private ObjectMapper objectMapper;

	@Override
	public ItemConfigurationId getDefaultItemConfigurationId(final Product product, final Shopper shopper) {
		ItemConfiguration itemConfiguration = itemConfigurationFactory.createItemConfiguration(product, shopper);
		return saveConfiguredItem(itemConfiguration);
	}

	@Override
	public ItemConfigurationId saveConfiguredItem(final ItemConfiguration itemConfiguration) {
		ItemConfigurationMemento itemConfigurationMemento = populateMemento(itemConfiguration);
		if (!getItemConfigurationMementoService().itemConfigurationMementoExistsByGuid(itemConfigurationMemento.getGuid())) {
			try {
				getItemConfigurationMementoService().saveItemConfigurationMemento(itemConfigurationMemento);
			} catch (JpaSystemException e) {
				LOG.debug("Another thread has probably persisted the same item configuration. Keep calm and move on.", e);
			}
		}
		return itemConfigurationMemento.getId();
	}

	private ItemConfigurationMemento populateMemento(final ItemConfiguration itemConfiguration) {
		String representation = serializeItemConfiguration(itemConfiguration);
		if (LOG.isDebugEnabled()) {
			LOG.debug("item configuration representation: " + representation);
		}
		ItemConfigurationId identifier = generateIdentifer(representation);
		ItemConfigurationMemento persistedItemConfiguration = getBean(ContextIdNames.ITEM_CONFIGURATION_MEMENTO);
		persistedItemConfiguration.setItemRepresentation(representation);
		persistedItemConfiguration.setId(identifier);
		return persistedItemConfiguration;
	}

	/**
	 * Serializes an item configuration to String.
	 *
	 * @param itemConfiguration the item configuration
	 * @return the string representation
	 */
	protected String serializeItemConfiguration(final ItemConfiguration itemConfiguration) {
		if (!(itemConfiguration instanceof ItemConfigurationImpl)) {
			throw new UnsupportedOperationException("only instances of ChildItemConfigurationImpl are supported at this point.");
		}
		ItemConfigurationImpl item = (ItemConfigurationImpl) itemConfiguration;
		try {
			return getObjectMapper().writeValueAsString(item);
		} catch (IOException e) {
			LOG.error(CANNOT_SERIALIZE_ERROR, e);
			throw new EpServiceException(CANNOT_SERIALIZE_ERROR, e);
		}
	}

	/**
	 * De-serializes an item configuration from a String.
	 *
	 * @param representation the representation string
	 * @return the item configuration
	 */
	protected ItemConfigurationImpl deserializeItemConfiguration(final String representation) {
		try {
			return getObjectMapper().readValue(representation, ItemConfigurationImpl.class);
		} catch (IOException e) {
			LOG.error(CANNOT_DESERIALIZE_ERROR, e);
			throw new EpServiceException(CANNOT_DESERIALIZE_ERROR, e);
		}
	}

	/**
	 * Generates an identifier for an item.
	 *
	 * @param itemRepresentation the string representation of the item
	 * @return the ID
	 */
	protected ItemConfigurationId generateIdentifer(final String itemRepresentation) {
		String shaHex = DigestUtils.shaHex(itemRepresentation);
		return new ItemConfigurationId(shaHex);
	}

	@Override
	public ItemConfigurationImpl load(final ItemConfigurationId itemConfigurationId) {
		ItemConfigurationMemento persistedItemConfiguration = getItemConfigurationMementoService().findByGuid(itemConfigurationId.getValue());
		if (persistedItemConfiguration == null) {
			return null;
		}
		return deserializeItemConfiguration(persistedItemConfiguration.getItemRepresentation());
	}

	@Override
	public ItemConfigurationBuilder loadBuilder(final ItemConfigurationId itemConfigurationId) {
		ItemConfigurationImpl itemConfiguration = load(itemConfigurationId);
		if (itemConfiguration == null) {
			return null;
		}

		return new ItemConfigurationImpl.Builder(itemConfiguration, getItemConfigurationValidator());
	}

	public void setItemConfigurationFactory(final ItemConfigurationFactory itemConfigurationFactory) {
		this.itemConfigurationFactory = itemConfigurationFactory;
	}

	protected ItemConfigurationFactory getItemConfigurationFactory() {
		return itemConfigurationFactory;
	}

	public void setItemConfigurationMementoService(final ItemConfigurationMementoService itemConfigurationMementoService) {
		this.itemConfigurationMementoService = itemConfigurationMementoService;
	}

	protected ItemConfigurationMementoService getItemConfigurationMementoService() {
		return itemConfigurationMementoService;
	}

	public void setItemConfigurationValidator(final ItemConfigurationValidator itemConfigurationValidator) {
		this.itemConfigurationValidator = itemConfigurationValidator;
	}

	protected ItemConfigurationValidator getItemConfigurationValidator() {
		return itemConfigurationValidator;
	}

	public void setObjectMapper(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	protected ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
