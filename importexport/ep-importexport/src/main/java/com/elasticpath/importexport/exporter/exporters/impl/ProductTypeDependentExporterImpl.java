package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.ProductTypeDTO;
import com.elasticpath.service.catalog.ProductTypeService;

/**
 * This class is responsible for exporting {@link ProductType}.
 */
public class ProductTypeDependentExporterImpl extends AbstractDependentExporterImpl<ProductType, ProductTypeDTO, CatalogDTO> {
	private ProductTypeService productTypeService;

	/** {@inheritDoc} */
	public List<ProductType> findDependentObjects(final long primaryObjectUid) {
		if (getFilter().isFiltered(primaryObjectUid)) {
			return getByCatalog(primaryObjectUid);
		}

		List<ProductType> resultList = new ArrayList<ProductType>();

		Iterator<Long> iter = getContext().getDependencyRegistry().getDependentUids(ProductType.class).iterator();
		while (iter.hasNext()) {
			final Long uid = iter.next();
			ProductType productType = (ProductType) productTypeService.getObject(uid);
			addAttributeAndSkuOptionDependencies(primaryObjectUid, productType);

			if (productType.getCatalog().getUidPk() == primaryObjectUid) {
				resultList.add(productType);
				iter.remove();
			}
		}
		return resultList;
	}

	private void addAttributeAndSkuOptionDependencies(final long primaryObjectUid, final ProductType productType) {
		Set<AttributeGroupAttribute> productAttributeGroupAttributes = productType.getProductAttributeGroupAttributes();

		Set<Long> attributeUidsSet = new HashSet<Long>();
		for (AttributeGroupAttribute attributeGroupAttribute : productAttributeGroupAttributes) {
			Attribute attribute = attributeGroupAttribute.getAttribute();
			if (attribute.getCatalog().getUidPk() == primaryObjectUid || attribute.isGlobal()) {
				attributeUidsSet.add(attribute.getUidPk());
			}
		}

		Set<AttributeGroupAttribute> skuAttributeGroupAttributes = productType.getSkuAttributeGroup().getAttributeGroupAttributes();
		for (AttributeGroupAttribute attributeGroupAttribute : skuAttributeGroupAttributes) {
			Attribute attribute = attributeGroupAttribute.getAttribute();
			if (attribute.getCatalog().getUidPk() == primaryObjectUid || attribute.isGlobal()) {
				attributeUidsSet.add(attribute.getUidPk());
			}
		}

		getContext().getDependencyRegistry().addUidDependencies(Attribute.class, attributeUidsSet);

		Set<SkuOption> skuOptions = productType.getSkuOptions();

		Set<Long> skuOptionUidsSet = new HashSet<Long>();
		for (SkuOption skuOption : skuOptions) {
			skuOptionUidsSet.add(skuOption.getUidPk());
		}

		getContext().getDependencyRegistry().addUidDependencies(SkuOption.class, skuOptionUidsSet);
	}

	private List<ProductType> getByCatalog(final long primaryObjectUid) {
		return productTypeService.findAllProductTypeFromCatalog(primaryObjectUid);
	}

	/** {@inheritDoc} */
	public void bindWithPrimaryObject(final List<ProductTypeDTO> dependentDtoObjects, final CatalogDTO primaryDtoObject) {
		primaryDtoObject.setProductTypes(dependentDtoObjects);
	}

	public void setProductTypeService(final ProductTypeService productTypeService) {
		this.productTypeService = productTypeService;
	}
}