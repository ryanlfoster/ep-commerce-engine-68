package com.elasticpath.service.asset.impl;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.domain.asset.ImageMap;
import com.elasticpath.domain.asset.ImageMapWithAbsolutePath;
import com.elasticpath.domain.asset.MutableImageMap;
import com.elasticpath.domain.asset.MutableImageMapWithAbsolutePath;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.service.asset.ImageDiscoveryService;

/**
 * Abstract image discovery service which will get the images from a catalog object's attributes.
 *
 * @param <T> the generic type of the catalog object
 */
public abstract class AbstractImageDiscoveryServiceImpl<T> implements ImageDiscoveryService<T> {

	private BeanFactory beanFactory;
	private AssetRepository assetRepository;

	/**
	 * Gets the image map by code.
	 *
	 * @param catalogObjectCode the catalog object code
	 * @return the image map by code
	 */
	@Override
	public ImageMap getImageMapByCode(final String catalogObjectCode) {
		T catalogObject = loadByCode(catalogObjectCode);
		if (catalogObject == null) {
			return getBeanFactory().getBean("imageMap");
		}
		return getImageMap(catalogObject);
	}
	
	/**
	 * Gets the image map.
	 *
	 * @param catalogObject the catalog object
	 * @return the image map
	 */
	@Override
	public ImageMap getImageMap(final T catalogObject) {
		MutableImageMap imageMap = getBeanFactory().getBean(ContextIdNames.IMAGE_MAP);
		addDefaultImage(catalogObject, imageMap);
		Map<String, AttributeValue> attributeValueMap = getAttributeValueMap(catalogObject);
		addAttributeImages(imageMap, attributeValueMap);
		return imageMap;
	}

	@Override
	public ImageMapWithAbsolutePath absolutePathsForImageMap(final ImageMap imageMap, final String storeCode) {
		MutableImageMapWithAbsolutePath imageMapWithAbsolutePath = getBeanFactory().getBean(ContextIdNames.IMAGE_MAP_WITH_ABSOLUTE_PATH);
		imageMapWithAbsolutePath.setRelativeImageMap(imageMap);
		imageMapWithAbsolutePath.setPathPrefix(getAssetRepository().getAssetServerImagesUrl(storeCode));
		return imageMapWithAbsolutePath;
	}
	
	/**
	 * Adds the default image of the catalog object to the map if one exists.
	 *
	 * @param catalogObject the catalog object
	 * @param imageMap the image map
	 */
	protected void addDefaultImage(final T catalogObject, final MutableImageMap imageMap) {
		String defaultImage = getDefaultImage(catalogObject);
		addImagePath(imageMap, getDefaultImageKey(), defaultImage);
	}

	/**
	 * Adds an image with the given key and path to the image map, iff the path is not empty.
	 *
	 * @param imageMap the image map
	 * @param key the key
	 * @param path the path
	 */
	protected void addImagePath(final MutableImageMap imageMap, final String key, final String path) {
		if (StringUtils.isNotEmpty(path)) {
			imageMap.addImage(key, path);
		}
	}

	/**
	 * Gets the default image key.
	 *
	 * @return the default image key
	 */
	protected abstract String getDefaultImageKey();

	/**
	 * Gets the attribute value map.
	 *
	 * @param catalogObject the catalog object
	 * @return the attribute value map
	 */
	protected abstract Map<String, AttributeValue> getAttributeValueMap(T catalogObject);

	/**
	 * Gets the default image.
	 *
	 * @param catalogObject the catalog object
	 * @return the default image
	 */
	protected abstract String getDefaultImage(T catalogObject);
	
	/**
	 * Load by code.
	 *
	 * @param catalogObjectCode the catalog object code
	 * @return the t
	 */
	protected abstract T loadByCode(String catalogObjectCode);

	/**
	 * Adds the attribute images.
	 *
	 * @param imageMap the image map
	 * @param attributeValueMap the attribute value map
	 */
	protected void addAttributeImages(final MutableImageMap imageMap, final Map<String, AttributeValue> attributeValueMap) {
		if (MapUtils.isEmpty(attributeValueMap)) {
			return;
		}
		for (Map.Entry<String, AttributeValue> entry : attributeValueMap.entrySet()) {
			AttributeValue value = entry.getValue();
			if (AttributeType.IMAGE.equals(value.getAttributeType())) {
				Attribute attribute = value.getAttribute();
				String key = attribute.getKey();
				Locale locale = null;
				if (attribute.isLocaleDependant()) {
					String localizedAttributeKey = entry.getKey();
					String localeString = StringUtils.removeStart(localizedAttributeKey, key + "_");
					locale = LocaleUtils.toLocale(localeString);
				}
				String relativePath = value.getStringValue();
				imageMap.addImage(key, relativePath, locale);
			}
		}
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected AssetRepository getAssetRepository() {
		return assetRepository;
	}

	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}
}
