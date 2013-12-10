package com.elasticpath.common.dto.cmuser;

import java.util.Collection;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserPasswordHistoryItem;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.cmuser.UserRoleService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.store.WarehouseService;
/**
 * Assembler for CmUser domain objects and their associated DTOs. 
 */
public class CmUserDtoAssembler extends AbstractDtoAssembler<CmUserDTO, CmUser> {

	private BeanFactory beanFactory;

	private UserRoleService userRoleService;
	
	private StoreService storeService;
	
	private WarehouseService warehouseService;
	
	private CatalogService catalogService;

	private static final String CMUSER_MESSAGE_PREFIX = "CmUser ";
	
	@Override
	public CmUser getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.CMUSER);
	}

	@Override
	public CmUserDTO getDtoInstance() {
		return new CmUserDTO();
	}
	
	/**
	 * Factory method for {@link UserPasswordHistoryItem}. 
	 * @return new, uninitialized {@link UserPasswordHistoryItem}.
	 */
	protected UserPasswordHistoryItem userPasswordHistoryItemDomainFactory() {
		return beanFactory.getBean(ContextIdNames.USER_PASSWORD_HISTORY_ITEM);
	}
	
	@Override
	public void assembleDto(final CmUser source, final CmUserDTO target) {
		target.setGuid(source.getGuid());
		target.setUserName(source.getUserName());
		target.setEmail(source.getEmail());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setPassword(source.getPassword());
		
		target.setCreationDate(source.getCreationDate());
		target.setLastLoginDate(source.getLastLoginDate());
		target.setLastChangedPasswordDate(source.getLastChangedPasswordDate());
		target.setLastModifiedDate(source.getLastModifiedDate());
		
		target.setFailedLoginAttempts(source.getFailedLoginAttempts());
		target.setEnabled(source.isEnabled());
		target.setHasTemporaryPassword(source.isTemporaryPassword());
		
		target.setAllWarehousesAccess(source.isAllWarehousesAccess());
		target.setAllCatalogsAccess(source.isAllCatalogsAccess());
		target.setAllStoresAccess(source.isAllStoresAccess());
		target.setAllPriceListsAccess(source.isAllPriceListsAccess());
		
		for (Warehouse sourceWarehouse : source.getWarehouses()) {
			target.getAccessibleWarehouseCodes().add(sourceWarehouse.getCode());
		}
		
		for (Catalog sourceCatalog : source.getCatalogs()) {
			target.getAccessibleCatalogCodes().add(sourceCatalog.getCode());
		}
		
		for (Store sourceStore : source.getStores()) {
			target.getAccessibleStoreCodes().add(sourceStore.getCode());
		}		
		
		for (String sourcePriceListGuid : source.getPriceLists()) {
			target.getAccessiblePriceListGuids().add(sourcePriceListGuid);
		}
		
		for (UserRole sourceUserRole : source.getUserRoles()) {
			target.getUserRoleGuids().add(sourceUserRole.getGuid());
		}
		
		for (UserPasswordHistoryItem sourceUserPasswordHistoryItem : source.getPasswordHistoryItems()) {

			UserPasswordHistoryItemDTO targetUserPasswordHistoryItemDTO = new UserPasswordHistoryItemDTO();
			targetUserPasswordHistoryItemDTO.setExpirationDate(sourceUserPasswordHistoryItem.getExpirationDate());
			targetUserPasswordHistoryItemDTO.setOldPassword(sourceUserPasswordHistoryItem.getOldPassword());
			
			target.getUserPasswordHistoryItems().add(targetUserPasswordHistoryItemDTO);
		}
		
	}
	
	@Override
	public void assembleDomain(final CmUserDTO source, final CmUser target) {
		
		target.setGuid(source.getGuid());
		target.setUserName(source.getUserName());
		target.setEmail(source.getEmail());
		target.setFirstName(source.getFirstName());
		target.setLastName(source.getLastName());
		target.setPassword(source.getPassword());
		
		target.setCreationDate(source.getCreationDate());
		target.setLastLoginDate(source.getLastLoginDate());
		target.setLastChangedPasswordDate(source.getLastChangedPasswordDate());
		target.setLastModifiedDate(source.getLastModifiedDate());
		
		target.setFailedLoginAttempts(source.getFailedLoginAttempts());
		target.setEnabled(source.isEnabled());
		target.setTemporaryPassword(source.hasTemporaryPassword());
		
		target.setAllWarehousesAccess(source.isAllWarehousesAccess());
		target.setAllCatalogsAccess(source.isAllCatalogsAccess());
		target.setAllStoresAccess(source.isAllStoresAccess());
		target.setAllPriceListsAccess(source.isAllPriceListsAccess());
		
		populateAndAddPasswordHistoryItemsToDomain(source, target);	
		addPriceListGuidsToDomain(source, target);
		populateAndAddUserRolesToDomain(source, target);
		populateAndAddWarehousesToDomain(source, target);
		populateAndAddCatalogsToDomain(source, target);
		populateAndAddStoresToDomain(source, target);
		
	}
	
	private void populateAndAddPasswordHistoryItemsToDomain(final CmUserDTO source, final CmUser target) {
		
		for (UserPasswordHistoryItemDTO sourceUserPasswordHistoryItemDTO : source.getUserPasswordHistoryItems()) {

			UserPasswordHistoryItem targetUserPasswordHistoryItem = userPasswordHistoryItemDomainFactory();
			targetUserPasswordHistoryItem.setExpirationDate(sourceUserPasswordHistoryItemDTO.getExpirationDate());
			targetUserPasswordHistoryItem.setOldPassword(sourceUserPasswordHistoryItemDTO.getOldPassword());

			Collection<UserPasswordHistoryItem> userPasswordHistoryFromDomain = target.getPasswordHistoryItems();

			if (!userPasswordHistoryFromDomain.contains(targetUserPasswordHistoryItem)) {
				target.getPasswordHistoryItems().add(targetUserPasswordHistoryItem);
			}
			
		}
		
	}
	
	private void populateAndAddWarehousesToDomain(final CmUserDTO source, final CmUser target) {

		for (String warehouseCodeFromDto : source.getAccessibleWarehouseCodes()) {

			Warehouse warehouseFromDTO = warehouseService.findByCode(warehouseCodeFromDto);

			if (warehouseFromDTO == null) {
				throw new EpSystemException(CMUSER_MESSAGE_PREFIX + source.getGuid() + " references warehouse " + warehouseCodeFromDto
						+ " which is not in the target system. Maybe run an export/import on warehouses first.");
			}

			target.getWarehouses().add(warehouseFromDTO);
		}
		
	}

	private void populateAndAddCatalogsToDomain(final CmUserDTO source, final CmUser target) {
	
		for (String catalogCodeFromDto : source.getAccessibleCatalogCodes()) {

			Catalog catalogFromDTO = catalogService.findByCode(catalogCodeFromDto);

			if (catalogFromDTO == null) {
				throw new EpSystemException(CMUSER_MESSAGE_PREFIX + source.getGuid() + " references catalog " + catalogCodeFromDto
						+ " which is not in the target system. Maybe run an export/import on catalogs first.");
			}

			target.getCatalogs().add(catalogFromDTO);
		}	
		
	}
	
	private void populateAndAddStoresToDomain(final CmUserDTO source, final CmUser target) {
		
		for (String storeCodeFromDto : source.getAccessibleStoreCodes()) {

			Store storeFromDTO = storeService.findStoreWithCode(storeCodeFromDto);

			if (storeFromDTO == null) {
				throw new EpSystemException(CMUSER_MESSAGE_PREFIX + source.getGuid() + " references store " + storeCodeFromDto
						+ " which is not in the target system. Maybe run an export/import on stores first.");
			}

			target.getStores().add(storeFromDTO);
		}		
		
	}
	
	private void populateAndAddUserRolesToDomain(final CmUserDTO source, final CmUser target) {
		
		for (String userRoleGuidFromDto : source.getUserRoleGuids()) {

			UserRole userRoleFromDTO = userRoleService.findByGuid(userRoleGuidFromDto);

			if (userRoleFromDTO == null) {
				throw new EpSystemException(CMUSER_MESSAGE_PREFIX + source.getGuid() + " references user role guid " + userRoleGuidFromDto
						+ " which is not in the target system. Maybe run an export/import on user roles first.");
			}
			
			Collection<UserRole> userRolesFromDomain = target.getUserRoles();
			
			if (!userRolesFromDomain.contains(userRoleFromDTO)) {
				target.getUserRoles().add(userRoleFromDTO);
			}
		}		
	}
	
	private void addPriceListGuidsToDomain(final CmUserDTO source, final CmUser target) {
		
		for (String priceListGuidFromDto : source.getAccessiblePriceListGuids()) {

			Collection<String> priceListGuidsFromDomain = target.getPriceLists();

			if (!priceListGuidsFromDomain.contains(priceListGuidFromDto)) {
				priceListGuidsFromDomain.add(priceListGuidFromDto);
			}

		}

	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param userRoleService the userRoleService to set
	 */
	public void setUserRoleService(final UserRoleService userRoleService) {
		this.userRoleService = userRoleService;
	}

	/**
	 * @param storeService the storeService to set
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 * @param warehouseService the warehouseService to set
	 */
	public void setWarehouseService(final WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	/**
	 * @param catalogService the catalogService to set
	 */
	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}
	
}
