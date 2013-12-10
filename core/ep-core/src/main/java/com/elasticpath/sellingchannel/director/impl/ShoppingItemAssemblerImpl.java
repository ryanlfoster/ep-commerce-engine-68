package com.elasticpath.sellingchannel.director.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.commons.exception.InvalidBundleSelectionException;
import com.elasticpath.commons.exception.InvalidBundleTreeStructureException;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.sellingchannel.ShoppingItemFactory;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;

/**
 * Maps data between a {@code ShoppingItemDto} and a {@code ShoppingItem} domain objects.
 */
public class ShoppingItemAssemblerImpl implements ShoppingItemAssembler {

	private static final Logger LOG = Logger.getLogger(ShoppingItemAssemblerImpl.class);

	private ProductSkuService productSkuService;

	private ProductService productService;

	private ShoppingItemFactory shoppingItemFactory;

	private ShoppingItemDtoFactory shoppingItemDtoFactory;

	@Override
	public ShoppingItem createShoppingItem(final ShoppingItemDto shoppingItemDto) {
		ProductSku rootSku = getProductSku(shoppingItemDto.getSkuCode());
		// special behaviour for the root node
		Product product = rootSku.getProduct();

		validateShoppingItemDtoWithProduct(shoppingItemDto, product);
		ShoppingItem root;
		if (product instanceof ProductBundle) {
			root = getShoppingItemFactory().createShoppingItem(rootSku, null, shoppingItemDto.getQuantity(), 0,
					shoppingItemDto.getItemFields());
			ProductBundle bundle = (ProductBundle) product;
			createShoppingItemTree(bundle, shoppingItemDto, root, shoppingItemDto.getQuantity());
		} else {
			root = getShoppingItemFactory().createShoppingItem(rootSku, null, Math.max(product.getMinOrderQty(), shoppingItemDto.getQuantity()), 0,
					shoppingItemDto.getItemFields());
		}
		return root;
	}

	@Override
	public void validateShoppingItemDto(final ShoppingItemDto shoppingItemDto) {
		final ProductSku rootSku = getProductSku(shoppingItemDto.getSkuCode());
		// special behaviour for the root node
		final Product product = rootSku.getProduct();
		validateShoppingItemDtoWithProduct(shoppingItemDto, product);
	}

	private void validateShoppingItemDtoWithProduct(final ShoppingItemDto shoppingItemDto, final Product product) {
		if (!verifyDtoStructureEqualsBundleStructure(product, shoppingItemDto)) {
			throw new InvalidBundleTreeStructureException("DTO structure does not correspond to bundle structure.");
		}
		if (!verifySelectionRulesFollowed(product, shoppingItemDto)) {
			throw new InvalidBundleSelectionException((ProductBundle) product, shoppingItemDto);
		}
	}

	/**
	 * Verifies that the given dto's structure is the same as the bundle's structure - no SKUs are specified in the DTO that are not also in the
	 * Bundle (The trees are mirrors of each other). If the given product is not a bundle, then the DTO's sku is simply verified to be among the
	 * product's skus.
	 *
	 * @param product the product to check
	 * @param dto the dto to check
	 * @return true if the trees are consistent, false if not.
	 */
	protected boolean verifyDtoStructureEqualsBundleStructure(final Product product, final ShoppingItemDto dto) {
		if (!(product instanceof ProductBundle) || dto.getConstituents().size() == 0) {
			return getSkuFromProduct(product, dto.getSkuCode()) != null;
		}
		ProductBundle bundle = (ProductBundle) product;
		int constituentIndex = 0;
		for (BundleConstituent bundleItem : bundle.getConstituents()) {
			ConstituentItem constituent = bundleItem.getConstituent();
			ShoppingItemDto correspondingDto = dto.getConstituents().get(constituentIndex);
			if (constituent.isProductSku() && !correspondingDto.getSkuCode().equals(constituent.getCode())) {
				return false;
			}
			if (constituent.isProduct() && !(verifyDtoStructureEqualsBundleStructure(constituent.getProduct(), correspondingDto))) {
				return false;
			}
			constituentIndex++;
		}
		return true;
	}

	/**
	 * Verifies that the given dto's selections match the given product's selection rules. If the given product is not a bundle, then the rules are
	 * not checked.
	 *
	 * @param product the product to check
	 * @param dto the dto specifying selections
	 * @return true if the rules are followed, false if there is a violation
	 */
	protected boolean verifySelectionRulesFollowed(final Product product, final ShoppingItemDto dto) {
		if (!(product instanceof ProductBundle) || dto.getConstituents().size() == 0) {
			return true;
		}
		ProductBundle bundle = (ProductBundle) product;
		int bundleSelectionRuleParameter = getBundleSelectionRuleParameter(bundle);
		int constituentIndex = 0;
		int numberSelectedConstituents = 0;
		boolean valid = true;
		for (BundleConstituent bundleItem : bundle.getConstituents()) {
			ConstituentItem bundleItemProduct = bundleItem.getConstituent();
			ShoppingItemDto correspondingDto = dto.getConstituents().get(constituentIndex);
			if (correspondingDto.isSelected()) {
				numberSelectedConstituents++;
				if (bundleItem.getConstituent().isBundle()) {
					// if it's a bundle and it is valid, then this bundle is "selected" and should increase our count.
					valid = valid && verifySelectionRulesFollowed(bundleItemProduct.getProduct(), correspondingDto);
				}
			} else if (!isLeafDto(correspondingDto)) {
				// nothing should be selected in children
				valid = valid && !hasSelectedInChildren(correspondingDto);
			}

			constituentIndex++;
		}
		if (bundleSelectionRuleParameter == 0) {
			bundleSelectionRuleParameter = bundle.getConstituents().size();
		}
		return valid && numberSelectedConstituents == bundleSelectionRuleParameter;
	}

	private boolean hasSelectedInChildren(final ShoppingItemDto correspondingDto) {
		boolean hasSelected = false;
		for (ShoppingItemDto constituent : correspondingDto.getConstituents()) {
			if (isLeafDto(constituent)) {
				hasSelected = hasSelected || constituent.isSelected();
			} else {
				hasSelected = hasSelected || hasSelectedInChildren(constituent);
			}
		}
		return hasSelected;
	}

	private int getBundleSelectionRuleParameter(final ProductBundle bundle) {
		int bundleSelectionRuleParameter;
		SelectionRule selectionRule = bundle.getSelectionRule();
		if (selectionRule == null) {
			LOG.debug("Bundle Selection Rule for bundle [" + bundle.getCode() + "] is null. Defaulting to 'Select All'");
			bundleSelectionRuleParameter = 0;
		} else {
			bundleSelectionRuleParameter = selectionRule.getParameter();
		}
		return bundleSelectionRuleParameter;
	}

	/**
	 * Recursive algorithm to create a tree of {@code ShoppingItem}s.
	 *
	 * @param bundle The bundle at the root of the tree.
	 * @param parentShoppingItemDto The DTO representing the parent.
	 * @param parent The ShoppingItem which is the parent of the children created in this invocation.
	 * @param parentShipQuantity The number of items the parent node is going to ship.
	 */
	protected void createShoppingItemTree(final ProductBundle bundle, final ShoppingItemDto parentShoppingItemDto, final ShoppingItem parent,
			final int parentShipQuantity) {
		int selectCount = getBundleSelectionRuleParameter(bundle);
		int selections = 0;
		int constituentIndex = 0;
		for (BundleConstituent bundleItem : bundle.getConstituents()) {
			ConstituentItem constituentProduct = bundleItem.getConstituent();
			// If we're not at the root of the DTO tree then we need to get the DTO from the parent's constituents, at the
			// same index of the bundle's constituent list that we're processing
			ShoppingItemDto thisShoppingItemDto = getShoppingItemDtoMatchingBundleConstituentAtIndex(parentShoppingItemDto, constituentIndex);
			if (thisShoppingItemDto == null || thisShoppingItemDto.isSelected()) {
				selections++;
				if (selectCount > 0 && selections > selectCount) {
					break;
				}
				ProductSku sku = retrieveSkuForShoppingItem(bundleItem, thisShoppingItemDto);
				ShoppingItem shoppingItem = getShoppingItemFactory().createShoppingItem(sku, null, bundleItem.getQuantity() * parentShipQuantity,
						constituentIndex, Collections.<String, String>emptyMap());
				parent.addChildItem(shoppingItem);
				if (constituentProduct.isBundle()) {
					createShoppingItemTree((ProductBundle) constituentProduct.getProduct(), thisShoppingItemDto, shoppingItem, parentShipQuantity
							* bundleItem.getQuantity());
				}
			}
			constituentIndex++;
		}
	}

	private boolean isLeafDto(final ShoppingItemDto dto) {
		return dto.getConstituents().isEmpty();
	}

	private ShoppingItemDto getShoppingItemDtoMatchingBundleConstituentAtIndex(final ShoppingItemDto parentShoppingItemDto,
			final int constituentIndex) {
		ShoppingItemDto dto = null;
		if ((parentShoppingItemDto != null) && (constituentIndex < parentShoppingItemDto.getConstituents().size())) {
			dto = parentShoppingItemDto.getConstituents().get(constituentIndex);
		}
		return dto;
	}

	/**
	 * Gets the sku with the given code from the given product. If no skucode is given then the product's default sku will be returned. Also, if the
	 * given product doesn't have its skus loaded, then they will be loaded.
	 *
	 * @param product the product
	 * @param skuCode the sku code
	 * @return the requested product sku, or null if a sku with the given code doesn't exist on the given product
	 */
	protected ProductSku getSkuFromProduct(final Product product, final String skuCode) {

		Product productWithSkus = product;
		if (CollectionUtils.isEmpty(product.getProductSkus())) {
			productWithSkus = getProductWithSkus(product);
		}
		ProductSku productSku;
		if (StringUtils.isBlank(skuCode)) { // you weren't given the sku code
			productSku = productWithSkus.getDefaultSku();
		} else {
			// you were given the sku code for the multisku product, so get the sku specified
			productSku = productWithSkus.getSkuByCode(skuCode);
		}
		return productSku;
	}

	/**
	 * Gets the given product with its list of skus populated.
	 *
	 * @param product the product
	 * @return the product with its skus
	 */
	Product getProductWithSkus(final Product product) {
		return getProductService().getProductWithSkus(product);
	}

	/**
	 * Retrieves the sku that should go on the ShoppingItem, given a domain bundle and its corresponding DTO. If the DTO is null then the call is
	 * assumed to be for an Accessory, which is typically passed in as a selection without any sku code being known (product only), in which case the
	 * product's default sku is used. Otherwise, the skuCode specified on the incoming DTO is used to retrieve the sku from the {@code
	 * ProductSkuService}.
	 *
	 * @param bundleItem the bundle item from which to build a ShoppingItem
	 * @param thisShoppingItemDto the DTO coming from the user (from which to build a ShoppingItem)
	 * @return the {@code ProductSku}
	 */
	ProductSku retrieveSkuForShoppingItem(final BundleConstituent bundleItem, final ShoppingItemDto thisShoppingItemDto) {
		ConstituentItem constituent = bundleItem.getConstituent();

		ProductSku sku = null;

		// Because accessory items do not have child dtos we need to handle a null dto.
		String skuCode = "";
		if (thisShoppingItemDto != null) {
			skuCode = thisShoppingItemDto.getSkuCode();
		}

		if (constituent.isProductSku()) {
			sku = constituent.getProductSku();
		} else if (constituent.isProduct()) {
			sku = getSkuFromProduct(constituent.getProduct(), skuCode);
		}

		if (sku == null) {
			throw new EpSystemException("Sku for skuCode [" + skuCode + "] for constituent [" + bundleItem.getGuid() + "] could not be found");
		}

		if (constituent.isProduct() && !constituent.getProduct().equals(sku.getProduct())) {
			throw new EpSystemException("The skuCode [" + sku.getSkuCode() + "] for constituent [" + bundleItem.getGuid()
					+ "] is not for a product which is a constituent of this bundle.");
		}
		if (constituent.isProductSku() && !StringUtils.isEmpty(skuCode) && !skuCode.equals(constituent.getCode())) {
			throw new EpSystemException("The bundle constituent [uid: " + bundleItem.getUidPk() + "] specifies a single Sku ["
			        + constituent.getCode() + " ] which is different than the sku selected [" + skuCode + "].");
		}

		return sku;
	}

	/**
	 * Retrieve the ProductSku with the given guid.
	 *
	 * @param currentSkuGuid the guid
	 * @return the corresponding ProductSku
	 * @throw EpServiceException if the productSku is not found
	 */
	ProductSku getProductSku(final String currentSkuGuid) {
		final ProductSku sku = productSkuService.findBySkuCode(currentSkuGuid);
		if (sku == null) { // not found.
			throw new EpServiceException("ProductSku with the specified sku code [" + currentSkuGuid + "] does not exist");
		}
		return sku;
	}

	/**
	 * Creates a {@code ShoppingItemDto} from a {@code ShoppingItem}. This implementation calls {@link #createShoppingItemDto(Product, int)} to
	 * create the default DTO from the product at the ShoppingItem's root level, and then calls
	 * {@link #configureShoppingItemDtoFromShoppingItem(ShoppingItemDto, ShoppingItem)} to configure the default DTO with data from the ShoppingItem.
	 *
	 * @param shoppingItem the shoppingItem that should be reflected by the DTO
	 * @return the generated and configured shoppingItemDto
	 */
	public ShoppingItemDto assembleShoppingItemDtoFrom(final ShoppingItem shoppingItem) {
		// Create a ShoppingItemDto from the Product represented by the ShoppingItem's root product
		Product product = shoppingItem.getProductSku().getProduct();
		//product.setDefaultSku(shoppingItem.getProductSku());
		ShoppingItemDto rootDto = createShoppingItemDto(product, shoppingItem.getQuantity());
		// Run through the Dto and set selected items according to the items in the ShoppingItem.
		return configureShoppingItemDtoFromShoppingItem(rootDto, shoppingItem);
	}

	/**
	 * Creates a dto from a product and quantity using the ShoppingItemDtoFactory.
	 *
	 * @param product the product sku code
	 * @param quantity the quantity
	 * @return the created dto
	 */
	ShoppingItemDto createShoppingItemDto(final Product product, final int quantity) {
		return getShoppingItemDtoFactory().createDto(product, quantity);
	}

	/**
	 * Configures a ShoppingItemDto to reflect data in a ShoppingItem by ensuring that the items in the ShoppingItem are "selected" in the DTO, and
	 * the root DTO has the prices and totals that are present on the ShoppingItem. If the DTO corresponds to a sku from a Multi-sku product, the sku
	 * specified in the ShoppingItem will override the DTO's referenced sku.
	 *
	 * @param rootDto the root-level dto
	 * @param rootItem the root-level ShoppingItem
	 * @return the configured DTO
	 */
	protected ShoppingItemDto configureShoppingItemDtoFromShoppingItem(final ShoppingItemDto rootDto, final ShoppingItem rootItem) {
		// We know that the root DTO's product and the root ShoppingItem's product are going to be the same, but we must match the skus.
		rootDto.setSelected(true);
		rootDto.setSkuCode(rootItem.getProductSku().getSkuCode());
		rootDto.setShoppingItemUidPk(rootItem.getUidPk());
		rootDto.setItemFields(rootItem.getFields());
		rootDto.setQuantity(rootItem.getQuantity());

		for (ShoppingItem childItem : rootItem.getBundleItems()) {
			ShoppingItemDto foundDto = retrieveAndConfigureChildDtoForShoppingItem(childItem, rootDto.getConstituents());

			if (foundDto != null) {
				if (childItem.isBundle()) {
					configureShoppingItemDtoFromShoppingItem(foundDto, childItem);
				} else {
					foundDto.setShoppingItemUidPk(childItem.getUidPk());
					foundDto.setItemFields(childItem.getFields());
					foundDto.setQuantity(childItem.getQuantity());
					foundDto.setSelected(true);
					foundDto.setPrice(childItem.getPrice());
				}
			}
		}

		configureShoppingItemDtoPricesFromShoppingItem(rootDto, rootItem);
		return rootDto;
	}

	/**
	 * Copies the price and total from the ShoppingItem to the ShoppingItemDto.
	 *
	 * @param rootDto the dto
	 * @param rootItem the shopping item
	 */
	void configureShoppingItemDtoPricesFromShoppingItem(final ShoppingItemDto rootDto, final ShoppingItem rootItem) {
		rootDto.setPrice(rootItem.getPrice());
		if (rootItem.getPrice().getCurrency() != null) {
			rootDto.setTotal(rootItem.getTotal());
		}
	}

	/**
	 * Looks in the child Shopping Item DTO's of a bundle, and retrieves the DTO that corresponds to the given child shopping item. It sets the
	 * SKU code of the childShoppingItem to the returning DTO, and makes the DTO selected.
	 * @param childShoppingItem the shoppingItem to search for. It represents the selected item in a bundle.
	 * @param childDtoConstituents the constituents to search through. It contains all the constituents of the bundle.
	 * @return the modified DTO
	 * @throws com.elasticpath.commons.exception.InvalidBundleTreeStructureException if the bundle structure is not consistent with the
	 * shopping item.
	 */
	protected ShoppingItemDto retrieveAndConfigureChildDtoForShoppingItem(final ShoppingItem childShoppingItem,
			final List<ShoppingItemDto> childDtoConstituents) throws InvalidBundleTreeStructureException {
		final int childIndex = childShoppingItem.getOrdering();

		if (childDtoConstituents.size() <= childIndex) {
			throw new InvalidBundleTreeStructureException("The number of items in the bundle is less than the index of the selected constituent.");
		}

		final ShoppingItemDto childDto = childDtoConstituents.get(childIndex);
		if (!childDtoMatchesChildShoppingItem(childShoppingItem, childDto)) {
			throw new InvalidBundleTreeStructureException("The DTO from the selected bundle constituent does not match the shopping item.");
		}

		childDto.setSkuCode(childShoppingItem.getProductSku().getSkuCode());
		return childDto;
	}

	private boolean childDtoMatchesChildShoppingItem(final ShoppingItem childShoppingItem, final ShoppingItemDto dto) {
		return belongToTheSameProduct(childShoppingItem, dto)
				&& (!dto.isProductSkuConstituent() || dto.getSkuCode().equals(childShoppingItem.getProductSku().getSkuCode()));
	}


	private boolean belongToTheSameProduct(final ShoppingItem childShoppingItem, final ShoppingItemDto dto) {
		return childShoppingItem.getProductSku().getProduct().getSkuByCode(dto.getSkuCode()) != null;
	}

	/**
	 * @return the ProductSkuService
	 */
	public ProductSkuService getProductSkuService() {
		return this.productSkuService;
	}

	/**
	 * Setter for shopping item factory.
	 *
	 * @param shoppingItemFactory The factory to set.
	 */
	public void setShoppingItemFactory(final ShoppingItemFactory shoppingItemFactory) {
		this.shoppingItemFactory = shoppingItemFactory;
	}


	/**
	 * @return the shoppingItemFactory
	 */
	public ShoppingItemFactory getShoppingItemFactory() {
		return shoppingItemFactory;
	}

	/**
	 * @param productService the productService to set
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService() {
		return productService;
	}

	/**
	 * @param shoppingItemDtoFactory the shoppingItemDtoFactory to set
	 */
	public void setShoppingItemDtoFactory(final ShoppingItemDtoFactory shoppingItemDtoFactory) {
		this.shoppingItemDtoFactory = shoppingItemDtoFactory;
	}

	/**
	 * @return the shoppingItemDtoFactory
	 */
	public ShoppingItemDtoFactory getShoppingItemDtoFactory() {
		return shoppingItemDtoFactory;
	}

	/**
	 * Sets the productSkuService.
	 *
	 * @param productSkuService The ProductSkuService.
	 */
	public void setProductSkuService(final ProductSkuService productSkuService) {
		this.productSkuService = productSkuService;
	}

}
