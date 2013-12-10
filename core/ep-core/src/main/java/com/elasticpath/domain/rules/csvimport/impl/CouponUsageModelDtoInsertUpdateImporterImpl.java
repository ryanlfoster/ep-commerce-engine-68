/**
 * 
 */
package com.elasticpath.domain.rules.csvimport.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;

import com.elasticpath.common.dto.CouponModelDto;
import com.elasticpath.common.dto.CouponModelDtoAssembler;
import com.elasticpath.common.dto.CouponUsageModelDto;
import com.elasticpath.common.dto.CouponUsageModelDtoAssembler;
import com.elasticpath.csvimport.AbstractInsertUpdateImporter;
import com.elasticpath.csvimport.DependentDtoImporter;
import com.elasticpath.csvimport.ImportValidRow;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;

/**
 * An insert/update importer with header extension support(PriceListDescriptorDTO) for BaseAmount.
 *
 */
public class CouponUsageModelDtoInsertUpdateImporterImpl extends AbstractInsertUpdateImporter 
	implements DependentDtoImporter<CouponUsageModelDto, CouponConfig> {

	private CouponModelDtoAssembler couponAssembler;
	
	private CouponUsageModelDtoAssembler couponUsageAssembler;
	
	private CouponService couponService;
	
	private CouponUsageService couponUsageService;
	

	@Override
	public List<ImportBadRow> importDtos(final List<ImportValidRow<CouponUsageModelDto>> validRows, final CouponConfig couponConfig) {
		List<ImportBadRow> badRows;
		if (CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(couponConfig.getUsageType())) {
			badRows = importCouponsAndUsageDtos(validRows, couponConfig);
		} else {
			badRows = importOnlyCouponDtos(validRows, couponConfig);
		}
		return badRows;
	}


	private List<ImportBadRow> importOnlyCouponDtos(final List<ImportValidRow<CouponUsageModelDto>> validRows,
													final CouponConfig couponConfig) {
		List<ImportBadRow> badRows = new ArrayList<ImportBadRow>();
		try {
			Set<CouponModelDto> uniqueCouponDtos = new HashSet<CouponModelDto>();
			for (ImportValidRow<CouponUsageModelDto> row : validRows) {				
				CouponUsageModelDto dto = row.getDto();
				String code = dto.getCouponCode();
	
				if (!validateCouponCode(code)) {
					ImportBadRow badRow = createBadRow(row, dto, "Coupon code is not valid.");
					badRows.add(badRow);
					continue;
				}
	
				if (uniqueCouponDtos.contains(dto)) {
					ImportBadRow badRow = createBadRow(row, dto, "Duplicate coupon.");
					badRows.add(badRow);
					continue;
				}
				uniqueCouponDtos.add(dto);
			}
			
			 
			Collection<String> existingCodes = new HashSet<String>();
			Collection<String> badCodes = new HashSet<String>();
			importCouponDtos(couponConfig, uniqueCouponDtos, existingCodes, badCodes);
			
			for (ImportValidRow<CouponUsageModelDto> row : validRows) {
				if (existingCodes.contains(row.getDto().getCouponCode())) {
					ImportBadRow badRow = createBadRow(row, row.getDto(), "Coupon already exists.");
					badRows.add(badRow);
				}
				if (badCodes.contains(row.getDto().getCouponCode())) {
					ImportBadRow badRow = createBadRow(row, row.getDto(), "Coupon assigned to another rule.");
					badRows.add(badRow);
				}
			}
		} catch (final EpServiceException x) {
			for (ImportValidRow<CouponUsageModelDto> row : validRows) {
				ImportBadRow badRow = createBadRow(row, row.getDto(), x.getMessage());
				badRows.add(badRow);
			}
		}
		
		return badRows;
	}


	private List<ImportBadRow> importCouponsAndUsageDtos(final List<ImportValidRow<CouponUsageModelDto>> validRows,
														 final CouponConfig couponConfig) {
		List<ImportBadRow> badRows = new ArrayList<ImportBadRow>();
		try {
			Set<CouponModelDto> uniqueCouponDtos = new HashSet<CouponModelDto>();
			Map<String, Collection<CouponUsageModelDto>> uniqueUsageDtos = new HashMap<String, Collection<CouponUsageModelDto>>();
			for (ImportValidRow<CouponUsageModelDto> row : validRows) {				
				CouponUsageModelDto dto = row.getDto();
				String code = dto.getCouponCode();
				String email = dto.getEmailAddress();
	
				if (!validateCouponCode(code)) {
					ImportBadRow badRow = createBadRow(row, dto, "Coupon code is not valid.");
					badRows.add(badRow);
					continue;
				}
	
				if (!validateEmail(email)) {
					ImportBadRow badRow = createBadRow(row, dto, "Email address is not valid.");
					badRows.add(badRow);
					continue;
				}
				
				Collection<CouponUsageModelDto> dtos = uniqueUsageDtos.get(code);
				if (dtos == null) {
					dtos = new HashSet<CouponUsageModelDto>();
					dtos.add(dto);
					uniqueUsageDtos.put(code, dtos);
				} else if (dtos.contains(dto)) {
					ImportBadRow badRow = createBadRow(row, dto, "Duplicate coupon/email.");
					badRows.add(badRow);
					continue;
				}
				dtos.add(dto);
				uniqueCouponDtos.add(dto);
			}
			
			 
			Collection<String> existingCodes = new HashSet<String>();
			Collection<String> badCodes = new HashSet<String>();
			Collection<Coupon> coupons = importCouponDtos(couponConfig, uniqueCouponDtos, existingCodes, badCodes);
			
			for (ImportValidRow<CouponUsageModelDto> row : validRows) {
				if (badCodes.contains(row.getDto().getCouponCode())) {
					ImportBadRow badRow = createBadRow(row, row.getDto(), "Coupon assigned to another rule.");
					badRows.add(badRow);
				}
			}

			Collection<CouponUsageModelDto> existingUsageDtos = importCouponUsageDtos(coupons, uniqueUsageDtos);
			
			for (ImportValidRow<CouponUsageModelDto> row : validRows) {
				if (existingUsageDtos.contains(row.getDto())) {
					ImportBadRow badRow = createBadRow(row, row.getDto(), "Coupon/email already assigned to this rule.");
					badRows.add(badRow);
				}
			}
		} catch (final EpServiceException x) {
			for (ImportValidRow<CouponUsageModelDto> row : validRows) {
				ImportBadRow badRow = createBadRow(row, row.getDto(), x.getMessage());
				badRows.add(badRow);
			}
		}

		return badRows;
	}


	/*
	 * return list of coupons
	 */
	private Collection<Coupon> importCouponDtos(final CouponConfig couponConfig, 
												final Set<CouponModelDto> couponDtos, 
												final Collection<String> existingCodes,
												final Collection<String> badCodes) {
		// populate the list of new codes
		Set<String> newCodes = new HashSet<String>();
		for (CouponModelDto dto : couponDtos) {
			newCodes.add(dto.getCouponCode());
		}
		
		// get the existing coupons for just this rule
		Collection<Coupon> coupons = new HashSet<Coupon>();
		coupons.addAll(couponService.findCouponsForRuleCodeFromCouponCodes(couponConfig.getRuleCode(), newCodes));
		
		// get codes for all rules, codes not for this rule are "bad"
		badCodes.addAll(couponService.findExistingCouponCodes(newCodes));
		
		// populate the list of existing codes for this rule
		for (Coupon coupon : coupons) {
			existingCodes.add(coupon.getCouponCode());
		}
		
		// remove existing codes from the new list
		newCodes.removeAll(existingCodes);
		
		// remove existing codes from the bad list
		badCodes.removeAll(existingCodes);
		
		// remove the bad codes from the new list
		newCodes.removeAll(badCodes);
		
		// add remaining coupons
		for (String code : newCodes) {
			for (CouponModelDto dto : couponDtos) {
				if (dto.getCouponCode().equals(code)) {
					Coupon coupon = getCouponAssembler().assembleDomain(dto, couponConfig);
					Coupon savedCoupon = couponService.add(coupon);
					coupons.add(savedCoupon);
					break;
				}
			}
		}
		
		return coupons; 
	}
	
	private Collection<CouponUsageModelDto> importCouponUsageDtos(final Collection<Coupon> coupons, 
																  final Map<String, Collection<CouponUsageModelDto>> dtoMap) {
		Collection<CouponUsage> newUsages = new LinkedList<CouponUsage>();
		Collection<CouponUsageModelDto> existingDtos = new LinkedList<CouponUsageModelDto>();
		for (Coupon coupon : coupons) {
			String code = coupon.getCouponCode();
			Collection<CouponUsageModelDto> couponUsageModelDtos = dtoMap.get(code);
			if (couponUsageModelDtos == null || couponUsageModelDtos.isEmpty()) {
				continue;
			}
			for (CouponUsageModelDto dto : couponUsageModelDtos) {
				if (couponUsageService.findByCouponCodeAndEmail(code, dto.getEmailAddress()) == null) {
					CouponUsage usage = getCouponUsageAssembler().assembleDomain(dto, coupon);
					newUsages.add(usage);
				} else {
					existingDtos.add(dto);
				}
			}
		}
		for (CouponUsage usage : newUsages) {
			couponUsageService.add(usage);
		}
		
		return existingDtos;
	}

	private boolean validateEmail(final String email) {
		return EmailValidator.getInstance().isValid(email);
	}

	private boolean validateCouponCode(final String couponCode) {
		return StringUtils.isNotBlank(couponCode);
	}

	private ImportBadRow createBadRow(final ImportValidRow<CouponUsageModelDto> row,
			final CouponUsageModelDto dto, final String message) {
		final ImportFault fault = createImportFault(message, dto.toString());
		return createImportBadRow(row.getRow(), row.getRowNumber(), fault);
	}
	
	/**
	 * @return the assembler
	 */
	private CouponModelDtoAssembler getCouponAssembler() {
		if (couponAssembler == null) {
			couponAssembler = new CouponModelDtoAssembler(getBeanFactory());
		}
		return couponAssembler;
	}
	
	/**
	 * @return the assembler
	 */
	private CouponUsageModelDtoAssembler getCouponUsageAssembler() {
		if (couponUsageAssembler == null) {
			couponUsageAssembler = new CouponUsageModelDtoAssembler(getBeanFactory());
		}
		return couponUsageAssembler;
	}

	/**
	 * Set the coupon service.
	 *  
	 * @param couponService the coupon service
	 */
	public void setCouponService(final CouponService couponService) {
		this.couponService = couponService;
	}

	/**
	 * Set the coupon usage service.
	 * 
	 * @param couponUsageService the coupon usage service
	 */ 
	public void setCouponUsageService(final CouponUsageService couponUsageService) {
		this.couponUsageService = couponUsageService;
	}
	
}
