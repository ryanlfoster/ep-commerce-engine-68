package com.elasticpath.importexport.common.adapters.products;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;

import com.elasticpath.common.dto.pricing.PriceAdjustmentDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuConstituentImpl;
import com.elasticpath.domain.pricing.PriceAdjustment;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.general.PricingMechanismValues;
import com.elasticpath.importexport.common.dto.products.bundles.ProductBundleConstituentCodeDTO;
import com.elasticpath.importexport.common.dto.products.bundles.ProductBundleConstituentDTO;
import com.elasticpath.importexport.common.dto.products.bundles.ProductBundleDTO;
import com.elasticpath.importexport.common.dto.products.bundles.SelectionRuleDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.service.catalog.BundleConstituentFactory;
import com.elasticpath.service.catalog.BundleValidator;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>ProductBundle</code> and <code>ProductBundleDTO</code> objects.<br>
 * {@link #createDomainObject()} is not overridden here because <code>ProductBundleAdapter</code> should not create <code>ProductBundle</code>s.<br>
 * They are created by <code>ProductAdapter</code>.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ProductBundleAdapter extends AbstractDomainAdapterImpl<ProductBundle, ProductBundleDTO> {
	private static final Logger LOG = Logger.getLogger(ProductBundleAdapter.class);

	private BundleConstituentFactory constituentFactory;

	private BundleValidator bundleValidator;

	private Map<String, String> bundleConstituentCodeTypeResolver = new HashMap<String, String>();

	private static final Comparator<ProductBundleConstituentDTO> COMPARATOR = new Comparator<ProductBundleConstituentDTO>() {
		public int compare(final ProductBundleConstituentDTO constituent1, final ProductBundleConstituentDTO constituent2) {
			return constituent1.getOrdering().compareTo(constituent2.getOrdering());
		}
	};

	@Override
	public void populateDTO(final ProductBundle source, final ProductBundleDTO target) {
		target.setCode(source.getCode());
		target.setConstituents(buildBundleConstituentDTOList(source.getConstituents()));
		target.setSelectionRule(buildSelectionRuleDTO(source.getSelectionRule()));
	}

	/**
	 * @param selectionRule source
	 * @return {@linkplain SelectionRuleDTO} populated from domain. null if domain has null SelectionRule.
	 */
	SelectionRuleDTO buildSelectionRuleDTO(final SelectionRule selectionRule) {
		if (selectionRule == null) {
			return null;
		}
		final SelectionRuleDTO dto = new SelectionRuleDTO();
		dto.setParameter(selectionRule.getParameter());
		return dto;
	}

	/**
	 * Builds the List of <code>ProductBundleConstituentDTO</code> from list of <code>BundleConstituent</code>.
	 *
	 * @param constituents the list of <code>BundleConstituent</code>
	 * @return List of <code>ProductBundleConstituentDTO</code>
	 */
	List<ProductBundleConstituentDTO> buildBundleConstituentDTOList(final List<BundleConstituent> constituents) {
		final List<ProductBundleConstituentDTO> bundleConstituentDTOList = new ArrayList<ProductBundleConstituentDTO>();
		for (BundleConstituent constituent : constituents) {
			bundleConstituentDTOList.add(buildBundleConstituentDTO(constituent));
		}
		return bundleConstituentDTOList;
	}

	/**
	 * Builds <code>ProductBundleConstituentDTO</code> from <code>BundleConstituent</code>.
	 *
	 * @param constituent <code>BundleConstituent</code> instance.
	 * @return the ProductBundleConstituentDTO
	 */
	ProductBundleConstituentDTO buildBundleConstituentDTO(final BundleConstituent constituent) {
		final ProductBundleConstituentDTO bundleConstituentDTO = new ProductBundleConstituentDTO();

		bundleConstituentDTO.setGuid(constituent.getGuid());

		final ProductBundleConstituentCodeDTO productBundleConstituentCode = createProductBundleConstituentCodeDTO(constituent);
		bundleConstituentDTO.setCode(productBundleConstituentCode);

		bundleConstituentDTO.setQuantity(constituent.getQuantity());
		bundleConstituentDTO.setOrdering(constituent.getOrdering());
		final List<PriceAdjustmentDto> adjustments = buildPriceAdjustmentDtoList(constituent.getPriceAdjustments());
		bundleConstituentDTO.setAdjustments(adjustments);

		return bundleConstituentDTO;
	}

	private ProductBundleConstituentCodeDTO createProductBundleConstituentCodeDTO(final BundleConstituent constituent) {
		final ProductBundleConstituentCodeDTO productBundleConstituentCode = new ProductBundleConstituentCodeDTO();

		productBundleConstituentCode.setValue(getBundleConstituentCode(constituent));
		productBundleConstituentCode.setType(getBundleConstituentType(constituent));
		return productBundleConstituentCode;
	}

	private String getBundleConstituentType(final BundleConstituent constituent) {
		final String className = constituent.getConstituent().getClass().getName();
		final String codeType = getBundleConstituentCodeTypeResolver().get(className);
		if (codeType == null) {
			LOG.error("Could not resolve bundle constituent code type for class: " + className);
			throw new PopulationRollbackException("IE-10330", className);
		}
		return codeType;
	}

	private List<PriceAdjustmentDto> buildPriceAdjustmentDtoList(final List<PriceAdjustment> priceAdjustments) {
		List<PriceAdjustmentDto> priceAdjustmentDtoList = new ArrayList<PriceAdjustmentDto>();
		for (PriceAdjustment adjustment : priceAdjustments) {
			priceAdjustmentDtoList.add(buildPriceAdjustmentDto(adjustment));
		}

		return priceAdjustmentDtoList;
	}

	private PriceAdjustmentDto buildPriceAdjustmentDto(final PriceAdjustment adjustment) {
		PriceAdjustmentDto priceAdjustmentDto = new PriceAdjustmentDto();
		priceAdjustmentDto.setGuid(adjustment.getGuid());
		priceAdjustmentDto.setAdjustmentAmount(adjustment.getAdjustmentAmount());
		priceAdjustmentDto.setPriceListGuid(adjustment.getPriceListGuid());

		return priceAdjustmentDto;
	}

	/**
	 * Gets code from <code>BundleConstituent</code>.
	 *
	 * @param constituent the {@link BundleConstituent} instance.
	 * @return String code.
	 */
	String getBundleConstituentCode(final BundleConstituent constituent) {
		return constituent.getConstituent().getCode();
	}

	@Override
	public void populateDomain(final ProductBundleDTO source, final ProductBundle target) {
		target.setCode(source.getCode());

		final List<ProductBundleConstituentDTO> constituents = source.getConstituents();
		final ProductBundleConstituentDTO[] array = constituents.toArray(new ProductBundleConstituentDTO[constituents.size()]);
		Arrays.sort(array, COMPARATOR);

		Collection<BundleConstituent> newConstituents = new ArrayList<BundleConstituent>();
		for (ProductBundleConstituentDTO productBundleConstituentDTO : array) {
			BundleConstituent constituent = createOrUpdateBundleConstituent(target, productBundleConstituentDTO);
			newConstituents.add(constituent);
		}

		replaceConstituents(target, newConstituents);

		checkForCyclicDependencies(target);
		checkForAssignedBundlesWithRecurringCharges(target);

		SelectionRuleDTO sourceRule = source.getSelectionRule();
		SelectionRule targetRule = target.getSelectionRule();

		if (sourceRule == null) {
			targetRule = null;
		} else if (sourceRule != null) {
			//create or edit
			if (targetRule == null) {
			   targetRule = createSelectionRule(sourceRule.getParameter());
		    } else {
			  targetRule.setParameter(sourceRule.getParameter());
		    }
		}
		target.setSelectionRule(targetRule);
		checkSelectionParameter(target);
	}

	/**
	 * Replaces the constituents on the bundle with the new constituents.
	 *
	 * @param bundle the bundle
	 * @param newConstituents the new constituents
	 */
	void replaceConstituents(final ProductBundle bundle, final Collection<BundleConstituent> newConstituents) {
		bundle.removeAllConstituents();
		for (BundleConstituent constituent : newConstituents) {
			bundle.addConstituent(constituent);
		}
	}

	/**
	 * TODO: this is the same as the ProductService validation, but this is repeated because I/E bypasses services.
	 *
	 * @param target bundle
	 */
	void checkSelectionParameter(final ProductBundle target) {
		if (!bundleValidator.isBundleSelectionRuleValid(target)) {
			LOG.error("Selection rule invalid:" + target.getCode() + ". Parameter is " + target.getSelectionRule().getParameter());
			throw new PopulationRollbackException("IE-10328", target.getCode(), String.valueOf(target.getSelectionRule().getParameter()));
		}
	}

	/**
	 * @param param the rule param
	 * @return populated SelectionRule object from DTO
	 */
	SelectionRule createSelectionRule(final Integer param) {
		final SelectionRule rule = getBean(ContextIdNames.BUNDLE_SELECTION_RULE);
		rule.setParameter(param);
		return rule;
	}

	/**
	 * Checks for cyclic dependencies in the bundle constituent tree.
	 *
	 * @param target the ProductBundle being imported
	 * @throws PopulationRollbackException in case of cyclic dependencies
	 */
	void checkForCyclicDependencies(final ProductBundle target) {
		// This code partially replicates the check made in ProductService.saveOrUpdate().
		// Ideally the I/E SavingManager would use the service and get this for free.
		final Product constituent = bundleValidator.getCyclicDependency(target);

		if (constituent != null && target.equals(constituent)) {
			LOG.error("Cyclic constituent dependency detected in bundle:" + target.getCode() + ". Bundle being added to itself.");
			throw new PopulationRollbackException("IE-10327", target.getCode(), constituent.getCode());
		} else if (constituent != null && !target.equals(constituent)) {
			LOG.error("Cyclic constituent dependency detected in bundle:" + target.getCode() + ". Bundle constituent tree has circular reference.");
			throw new PopulationRollbackException("IE-10326", target.getCode(), constituent.getCode());
		}
	}

    /**
     * Checks for Assigned bundles that contain recurring charges.
     *
     * @param target the ProductBundle being imported.
     * @throws PopulationRollbackException in case of assigned bundles with recurring charges.
     */
	void checkForAssignedBundlesWithRecurringCharges(final ProductBundle target) {
	    if (bundleValidator.doesAssignedBundleContainRecurringCharge(target)) {
	        LOG.error("Assigned bundle: " + target.getCode() + " contains Recurring charge constituent.");
	        throw new PopulationRollbackException("IE-10331", target.getCode());
	    }
	}

	/**
	 * Creates a new {@link BundleConstituent}, if it does not exist, or updates an existing one.
	 *
	 * @param bundle the {@link ProductBundle} instance.
	 * @param constituentDTO the {@link ProductBundleConstituentDTO} DTO instance
	 *
	 * @return {@link BundleConstituent} the newly created, or updated bundle constituent object
	 */
	BundleConstituent createOrUpdateBundleConstituent(final ProductBundle bundle, final ProductBundleConstituentDTO constituentDTO) {
		final List<PriceAdjustment> priceAdjustments = createPriceAdjustments(constituentDTO);
		BundleConstituent existingBundleConstituent = findMatchingBundleConstituent(bundle, constituentDTO);
		if (existingBundleConstituent == null) {
			BundleConstituent bundleConstituent = createBundleConstituent(constituentDTO, priceAdjustments);
			// if the constituent is a ProductBundle, need to validate that its pricingMechanism is the same as the parent's.
			checkPricingMechanismForBundleConstituent(bundle, bundleConstituent);
			bundle.addConstituent(bundleConstituent);
			return bundleConstituent;
		}
		updateBundleConstituent(existingBundleConstituent, constituentDTO, priceAdjustments);
		return existingBundleConstituent;
	}

	/**
	 * Finds the bundle constituent matching the given DTO.
	 *
	 * @param bundle the bundle
	 * @param constituentDTO the constituent dto
	 * @return the bundle constituent if one is found, <code>null</code> otherwise.
	 */
	protected BundleConstituent findMatchingBundleConstituent(final ProductBundle bundle,
			final ProductBundleConstituentDTO constituentDTO) {
		String dtoGuid = constituentDTO.getGuid();
		for (BundleConstituent bundleConstituent : bundle.getConstituents()) {
			if (ObjectUtils.equals(bundleConstituent.getGuid(), dtoGuid)) {
				return bundleConstituent;
			}
		}
		return null;
	}

	/**
	 * Gets the type of a bundle constituent.
	 *
	 * @param constituent the constituent
	 * @return the constituent type, e.g. "product" or "sku".
	 */
	protected String getConstituentType(final BundleConstituent constituent) {
		if (constituent.getConstituent().isProduct()) {
			return "product";
		}
		return "sku";
	}

	/**
	 * Update bundle constituent.
	 *
	 * @param bundleConstituent the bundle constituent to be updated
	 * @param constituentDTO the constituent DTO
	 * @param priceAdjustments the new price adjustments
	 */
	protected void updateBundleConstituent(final BundleConstituent bundleConstituent,
			final ProductBundleConstituentDTO constituentDTO,
			final List<PriceAdjustment> priceAdjustments) {
		checkBundleConstituentIdentity(bundleConstituent, constituentDTO.getCode());
		bundleConstituent.setQuantity(constituentDTO.getQuantity());
		for (PriceAdjustment newAdjustment : priceAdjustments) {
			PriceAdjustment existingAdjustment = findMatchingPriceAdjustment(bundleConstituent, newAdjustment);
			if (existingAdjustment == null) {
				bundleConstituent.addPriceAdjustment(newAdjustment);
			} else {
				existingAdjustment.setAdjustmentAmount(newAdjustment.getAdjustmentAmount());
				existingAdjustment.setPriceListGuid(newAdjustment.getPriceListGuid());
			}
		}
	}

	/**
	 * Verify that the bundle constituent has the same identity as the one mentioned in the DTO.
	 *
	 * @param bundleConstituent the bundle constituent
	 * @param productBundleConstituentCode the product bundle constituent code
	 */
	protected void checkBundleConstituentIdentity(final BundleConstituent bundleConstituent,
			final ProductBundleConstituentCodeDTO productBundleConstituentCode) {
		if (!bundleConstituentCodeMatchesDTOCode(productBundleConstituentCode, bundleConstituent)) {
			LOG.error("Trying update an existing bundle constituent with GUID " + bundleConstituent.getGuid() + " by changing its code or type");
			throw new PopulationRollbackException("IE-10334", bundleConstituent.getGuid(),
					getConstituentType(bundleConstituent), bundleConstituent.getConstituent().getCode(),
					productBundleConstituentCode.getType(), productBundleConstituentCode.getValue());
		}
	}

	private boolean bundleConstituentCodeMatchesDTOCode(final ProductBundleConstituentCodeDTO productBundleConstituentCode,
			final BundleConstituent bundleConstituent) {
		return productBundleConstituentCode.getValue().equals(getBundleConstituentCode(bundleConstituent))
		&& productBundleConstituentCode.getType().equals(getBundleConstituentType(bundleConstituent));
	}

	private PriceAdjustment findMatchingPriceAdjustment(final BundleConstituent bundleConstituent, final PriceAdjustment newAdjustment) {
		for (PriceAdjustment adjustment : bundleConstituent.getPriceAdjustments()) {
			if (newAdjustment.equals(adjustment)) {
				return adjustment;
			}
		}
		return null;
	}

	private BundleConstituent createBundleConstituent(final ProductBundleConstituentDTO constituentDTO,
			final List<PriceAdjustment> priceAdjustments) {
		BundleConstituent bundleConstituent = null;
		ProductBundleConstituentCodeDTO constituentCode = constituentDTO.getCode();
		Integer quantity = constituentDTO.getQuantity();
		if (isProductConstituent(constituentCode)) {
			bundleConstituent = constituentFactory.createBundleConstituent(getCachingService().findProductByCode(
					constituentCode.getValue()), quantity);

		} else if (isProductSkuConstituent(constituentCode)) {
			bundleConstituent = constituentFactory.createBundleConstituent(getCachingService()
					.findSkuByCode(constituentCode.getValue()), quantity);
		} else {
			LOG.error("Unhandled product bundle constituent code type: " + constituentCode.getType() + " expect: [product|sku]");
			throw new PopulationRollbackException("IE-10329", constituentCode.getType());
		}
		bundleConstituent.setGuid(constituentDTO.getGuid());
		bundleConstituent.getPriceAdjustments().addAll(priceAdjustments);
		return bundleConstituent;
	}

	/**
	 *  Validate if the pricing mechanism of the parent is matching the inner constituent.
	 * @param bundle the parent bundle
	 * @param bundleConstituent - the constituent of the bundle
	 */
	protected void checkPricingMechanismForBundleConstituent(final ProductBundle bundle, final BundleConstituent bundleConstituent) {
		if (!bundleValidator.isConstituentPricingMechanismValidForThisBundle(bundle, bundleConstituent.getConstituent())) {
			// create friendly message
			String parentPricingMechanismString = null;
			String constituentPricingMechanismString = null;
			if (bundle.isCalculated()) {
				parentPricingMechanismString = PricingMechanismValues.CALCULATED.value();
				constituentPricingMechanismString = PricingMechanismValues.ASSIGNED.value();
			} else {
				parentPricingMechanismString = PricingMechanismValues.ASSIGNED.value();
				constituentPricingMechanismString = PricingMechanismValues.CALCULATED.value();
			}

			LOG.error("Unhandled product bundle constituent pricingMechanism: " + constituentPricingMechanismString
					+ " expect: "  + parentPricingMechanismString);
			throw new PopulationRollbackException("IE-10332", constituentPricingMechanismString, parentPricingMechanismString);
		}

	}

	private boolean isProductSkuConstituent(final ProductBundleConstituentCodeDTO productBundleConstituentCode) {
		final String className = ProductSkuConstituentImpl.class.getName();
		final String codeType = getBundleConstituentCodeTypeResolver().get(className);
		return codeType.equals(productBundleConstituentCode.getType());
	}

	private boolean isProductConstituent(final ProductBundleConstituentCodeDTO productBundleConstituentCode) {
		// assume when productBundleConstituentCode.getType is null, that this is defaulting to product constituents
		final String className = ProductConstituentImpl.class.getName();
		final String codeType = getBundleConstituentCodeTypeResolver().get(className);
		return (codeType.equals(productBundleConstituentCode.getType())
				|| productBundleConstituentCode.getType() == null);
	}

	private List<PriceAdjustment> createPriceAdjustments(final ProductBundleConstituentDTO constituentDTO) {
		final ArrayList<PriceAdjustment> priceAdjustments = new ArrayList<PriceAdjustment>();
		if (constituentDTO.getAdjustments() != null) {
			for (PriceAdjustmentDto adjustmentDto : constituentDTO.getAdjustments()) {
				final PriceAdjustment adjustment = getBean(ContextIdNames.PRICE_ADJUSTMENT);
				adjustment.setAdjustmentAmount(adjustmentDto.getAdjustmentAmount());
				adjustment.setGuid(adjustmentDto.getGuid());
				adjustment.setPriceListGuid(adjustmentDto.getPriceListGuid());
				priceAdjustments.add(adjustment);
			}
		}
		return priceAdjustments;
	}

	@Override
	public ProductBundleDTO createDtoObject() {
		return new ProductBundleDTO();
	}

	/**
	 * Setter for {@link BundleConstituentFactory}.
	 *
	 * @param constituentFactory {@link BundleConstituentFactory}
	 */
	public void setBundleConstituentFactory(final BundleConstituentFactory constituentFactory) {
		this.constituentFactory = constituentFactory;
	}

	/**
	 * @param bundleValidator the bundleValidator to set
	 */
	public void setBundleValidator(final BundleValidator bundleValidator) {
		this.bundleValidator = bundleValidator;
	}

	/**
	 * @return the bundleValidator
	 */
	public BundleValidator getBundleValidator() {
		return bundleValidator;
	}

	private Map<String, String> getBundleConstituentCodeTypeResolver() {
		return bundleConstituentCodeTypeResolver;
	}

	/**
	 * @param bundleConstituentCodeTypeResolver the class name to code type used for the attribute type on a code element for a constituent
	 */
	public void setBundleConstituentCodeTypeResolver(final Map<String, String> bundleConstituentCodeTypeResolver) {
		this.bundleConstituentCodeTypeResolver = bundleConstituentCodeTypeResolver;
	}

}
