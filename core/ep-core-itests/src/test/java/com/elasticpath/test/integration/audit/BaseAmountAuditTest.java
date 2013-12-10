package com.elasticpath.test.integration.audit;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.category.ChangeSetObjectsImpl;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountUpdateStrategy;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.impl.BaseAmountImpl;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.test.integration.DirtiesDatabase;

public class BaseAmountAuditTest extends AbstractAuditTestSupport {

	private static final String DEFAULT_CURRENCY = "CAD";
	private static final String DEFAULT_DESCRIPTION = "DESC";
	
	private static final long REMOVE_BA_MODIFIER = 1002L;
	private static final long UPDATE_BA_MODIFIER = 1001l;
	private static final long ADD_BA_MODIFIER = 1000L;

	@Autowired
	private BaseAmountUpdateStrategy updateStrategy;

	@Autowired
	@Qualifier("baseAmountService")
	private BaseAmountService baService;
	private BaseAmount addedForRemoval;
	private BaseAmount addedForUpdate;
	private BaseAmountImpl addBa;
	private BaseAmountImpl updateBa;
	private BaseAmountImpl removeBa;

	@Autowired
	private PriceListService priceListService;
	private String pldGuid;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * 
	 * @throws Exception on error
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		//1st ensure a priceList Exists.
		PriceListDescriptorDTO dto = new PriceListDescriptorDTO();
		dto.setCurrencyCode(DEFAULT_CURRENCY);
		dto.setName("PL");
		dto.setDescription(DEFAULT_DESCRIPTION);
		PriceListDescriptorDTO merged = priceListService.saveOrUpdate(dto);
		pldGuid = merged.getGuid();
	}
	
	/**
	 * Test auditing for creating catalog.
	 */
	@DirtiesDatabase
	@Test
	public void testAuditingForModifyBaseAmounts() {
//		ThreadLocalMap<String, Object> metadata = (ThreadLocalMap<String, Object>) getTestApplicationContext().getBeanFactory().
//			getBean("persistenceListenerMetadataMap");
//		metadata.put("changeSetGuid", "changeSetGuid1");
//		metadata.put("userGuid", "userGuid1");

		ChangeSetObjects<BaseAmountDTO> sample = getSampleChangeSet();
		
		updateStrategy.modifyBaseAmounts(sample);
		
		int expectedChangeOperationNumber = 1;
		
		//verify delete
		verifyAuditData(addedForRemoval, null, addedForRemoval.getGuid(), ChangeType.DELETE, expectedChangeOperationNumber);

		//verify added
		verifyAuditData(null, addBa, addBa.getGuid(), ChangeType.CREATE, expectedChangeOperationNumber);

		//verify update
		BaseAmount retrieved = baService.findByGuid(addedForUpdate.getGuid());
		verifyAuditData(retrieved, addedForUpdate, addedForUpdate.getGuid(), ChangeType.UPDATE, expectedChangeOperationNumber);
	}
	
	private BaseAmountImpl getNewBaseAmount(final long modifier) {
		return new BaseAmountImpl("GUID" + modifier, "OBJ_GUID" + modifier, "PRODUCT", 
				new BigDecimal(1 + modifier), new BigDecimal(2 + modifier), new BigDecimal(1 + modifier), pldGuid);
	}
	
	private ChangeSetObjects<BaseAmountDTO> getSampleChangeSet() {
		addBa = getNewBaseAmount(ADD_BA_MODIFIER);
		updateBa = getNewBaseAmount(UPDATE_BA_MODIFIER);
		removeBa = getNewBaseAmount(REMOVE_BA_MODIFIER);

		//to remove.. we need to add
		addedForRemoval = baService.add(removeBa);
		
		//to update.. we need to add as well - these will have same data.
		addedForUpdate = baService.add(updateBa);
		updateBa.setListValue(new BigDecimal(5 + UPDATE_BA_MODIFIER));
		updateBa.setSaleValue(new BigDecimal(5 + UPDATE_BA_MODIFIER));

		BaseAmountDTO updateDto = new BaseAmountDTO();
		updateDto.setGuid(updateBa.getGuid());
		updateDto.setObjectGuid(updateBa.getObjectGuid());
		updateDto.setObjectType(updateBa.getObjectType());
		updateDto.setQuantity(updateBa.getQuantity());
		updateDto.setListValue(updateBa.getListValue());
		updateDto.setSaleValue(updateBa.getSaleValue());
		updateDto.setPriceListDescriptorGuid(pldGuid);

		BaseAmountDTO removeDto = new BaseAmountDTO();
		removeDto.setGuid(removeBa.getGuid());
		removeDto.setObjectGuid(removeBa.getObjectGuid());
		removeDto.setObjectType(removeBa.getObjectType());
		removeDto.setQuantity(removeBa.getQuantity());
		removeDto.setListValue(removeBa.getListValue());
		removeDto.setSaleValue(removeBa.getSaleValue());
		removeDto.setPriceListDescriptorGuid(pldGuid);

		BaseAmountDTO addDto = new BaseAmountDTO();
		addDto.setGuid(addBa.getGuid());
		addDto.setObjectGuid(addBa.getObjectGuid());
		addDto.setObjectType(addBa.getObjectType());
		addDto.setQuantity(addBa.getQuantity());
		addDto.setListValue(addBa.getListValue());
		addDto.setSaleValue(addBa.getSaleValue());
		addDto.setPriceListDescriptorGuid(pldGuid);
		
		ChangeSetObjects<BaseAmountDTO> changeSet = new ChangeSetObjectsImpl<BaseAmountDTO>();
		changeSet.addToAdditionList(addDto);
		changeSet.addToRemovalList(removeDto);
		changeSet.addToUpdateList(updateDto);
		return changeSet;
	}
}
