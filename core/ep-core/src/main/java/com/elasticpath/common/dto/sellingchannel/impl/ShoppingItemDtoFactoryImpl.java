package com.elasticpath.common.dto.sellingchannel.impl;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.impl.BundleIdentifierImpl;

/**
 * Shopping Item Dto Factory implementation.
 *
 */
public class ShoppingItemDtoFactoryImpl implements ShoppingItemDtoFactory {

	private final BundleIdentifier bundleIdentifier;

	/**
	 * Constructor.
	 */
	public ShoppingItemDtoFactoryImpl() {
		bundleIdentifier = new BundleIdentifierImpl();
	}

	@Override
	public ShoppingItemDto createDto(final String skuCode, final int qty) {
		return new ShoppingItemDto(skuCode, qty);
	}

	@Override
	public ShoppingItemDto createDto(final Product product, final int qty) {
		ShoppingItemDto dto = createDto(product.getDefaultSku().getSkuCode(), qty);
		dto.setProductCode(product.getCode());
		return createShoppingItemDtoTree(product, dto, qty, null);
	}

	/**
	 * Creates a subtree of ShoppingItemDto objects given a Product and a parent DTO.
	 * Where a Product is a multi-sku product, the product's default sku will be used to create the DTO.
	 *
	 * @param product the product represented by the DTO
	 * @param parentDto the parent item (must not be null)
	 * @param qty the quantity of the product to be specified on the dto
	 * @param itemConfiguration The ItemConfiguration for the given product. May be null in which case the defaults are chosen.
	 * @return the subtree
	 */
	private ShoppingItemDto createShoppingItemDtoTree(
			final Product product, final ShoppingItemDto parentDto, final int qty, final ItemConfiguration itemConfiguration) {
		if (getBundleIdentifier().isBundle(product)) {
			ShoppingItemDto childDto = null;
			ProductBundle bundle = (ProductBundle) product;

			for (BundleConstituent bundleConstituent : bundle.getConstituents()) {
				ConstituentItem constituent = bundleConstituent.getConstituent();
				final ItemConfiguration childItemConfiguration;
				if (itemConfiguration == null) {
					childItemConfiguration = null;
				} else {
					childItemConfiguration = itemConfiguration.getChildById(bundleConstituent.getGuid());
				}

				if (childItemConfiguration == null) {
					childDto = createDto(constituent.getProductSku().getSkuCode(), qty);
					childDto.setSelected(false);
				} else {
					childDto = createDto(childItemConfiguration.getSkuCode(), qty);
					childDto.setSelected(childItemConfiguration.isSelected());
				}
				childDto.setProductCode(constituent.getProduct().getCode());
				childDto.setProductSkuConstituent(bundleConstituent.getConstituent().isProductSku());
				parentDto.addConstituent(childDto);

				if (constituent.isBundle()) {
					createShoppingItemDtoTree(constituent.getProduct(), childDto, qty, childItemConfiguration);
				}
			}
		}
		return parentDto;
	}

	@Override
	public ShoppingItemDto createDto(final Product product, final int qty, final ItemConfiguration itemConfiguration) {
		if (getBundleIdentifier().isBundle(product)) {
			ShoppingItemDto dto = createDto(itemConfiguration.getSkuCode(), qty);
			dto.setProductCode(product.getCode());
			return createShoppingItemDtoTree(product, dto, qty, itemConfiguration);
		}

		return createDto(itemConfiguration.getSkuCode(), qty);
	}

	private BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}
}
