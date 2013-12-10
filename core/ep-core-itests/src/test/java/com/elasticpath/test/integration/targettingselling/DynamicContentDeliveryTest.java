package com.elasticpath.test.integration.targettingselling;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.DynamicContentDeliveryTestPersister;


/**
 * Test of {@link DynamicContentDelivery} to verify deletion of orphaned {@link SellingContext}.
 */
public class DynamicContentDeliveryTest extends DbTestCase {

	@Autowired
	private DynamicContentDeliveryService dynamicContentDeliveryService;  
	
	@Autowired
	private SellingContextService sellingContextService;  
	
	/**
	 * Verify deletion of orphaned selling context.
	 */
	@DirtiesDatabase
	@Test
	public void verifyDeletionOfOrphanedSellingContext() {
		DynamicContentDeliveryTestPersister dynamicContentDeliveryTestPersister = 
			getTac().getPersistersFactory().getDynamicContentDeliveryTestPersister();
		dynamicContentDeliveryTestPersister.persistDynamicContent("dynamicContentName", "wrapperId");
		
		DynamicContentDelivery dynamicContentDelivery = 
			dynamicContentDeliveryTestPersister.persistDynamicContentAssignment("dynamicContentDeliveryName", "dynamicContentName", 1);
		
		dynamicContentDelivery = 
			dynamicContentDeliveryTestPersister.persistDCDWithSellingContext(dynamicContentDelivery, 
					TagDictionary.DICTIONARY_SHOPPER_GUID, "sellingCondition");
		
		String sellingContextGuid = dynamicContentDelivery.getSellingContextGuid();
		assertNotNull("Selling context should be persisted.", sellingContextService.getByGuid(sellingContextGuid));
		dynamicContentDelivery.setSellingContext(null);
		dynamicContentDeliveryService.saveOrUpdate(dynamicContentDelivery);
		assertNull("Orphaned selling context should be deleted.", sellingContextService.getByGuid(sellingContextGuid));
	}
}
