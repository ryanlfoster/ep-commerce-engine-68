package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeCustomerAddressImpl;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.util.Utils;

/**
 * Test import job for CustomerAddress.
 */
public class ImportCustomerAddressTest extends ImportJobTestCase {

	private static final int STREET_ONE = 6;
	private static final int STREET_TWO = 7;
	private static final int STREET_THREE = 10;
	private static final int STREET_FOUR = 11;
	private static final String ZIPCODE = "777777";
	private static final String CUSTOMER_L_N = "CustomerLN";
	private static final String CUSTOMER_ADDRESS_LINE_1 = "Customer Address line 1";
	private static final String CUSTOMER_ADDRESS_LINE_2 = "Customer Address line 2";
	private static final String CUSTOMER_GUID = "1001";

	@Autowired
	private CustomerService customerService;

	/**
	 * Test import CustomerAddress insert/update.
	 * 
	 * @throws Exception on any error
	 */
	@DirtiesDatabase
	@Test
	public void testImportCustomerAddressInsertUpdate() throws Exception {
		// import customers first
		executeImportJob(createInsertCustomerImportJob());

		// update customer addresses
		executeImportJob(createUpdateCustomerAddressImportJob());

		Customer customer = customerService.findByGuid("101");
		CustomerAddress customerAddress = customer.getAddressByGuid(CUSTOMER_GUID);

		// assert new customer address has been created during import
		assertEquals(CUSTOMER_GUID, customerAddress.getGuid());
		assertEquals("CustomerFN", customerAddress.getFirstName());
		assertEquals(CUSTOMER_L_N, customerAddress.getLastName());
		assertEquals("1111111111", customerAddress.getPhoneNumber());
		assertEquals(CUSTOMER_ADDRESS_LINE_1, customerAddress.getStreet1());
		assertEquals(CUSTOMER_ADDRESS_LINE_2, customerAddress.getStreet2());
		assertEquals("Customer sity", customerAddress.getCity());
		assertEquals("AL", customerAddress.getSubCountry());
		assertEquals("US", customerAddress.getCountry());
		assertEquals(ZIPCODE, customerAddress.getZipOrPostalCode());

		// assert existing customer address has been updated during import
		CustomerAddress customerAddress2 = customer.getAddressByGuid("1002");
		assertEquals("1002", customerAddress2.getGuid());
		assertEquals("CustomerFN2", customerAddress2.getFirstName());
		assertEquals(CUSTOMER_L_N, customerAddress2.getLastName());
		assertEquals("4444444444", customerAddress2.getPhoneNumber());
		assertEquals(CUSTOMER_ADDRESS_LINE_1, customerAddress2.getStreet1());
		assertEquals(CUSTOMER_ADDRESS_LINE_2, customerAddress2.getStreet2());
		assertEquals("Customer city", customerAddress2.getCity());
		assertEquals("AL", customerAddress2.getSubCountry());
		assertEquals("US", customerAddress2.getCountry());
		assertEquals(ZIPCODE, customerAddress2.getZipOrPostalCode());

		// assert new customer address has been created during import
		Customer customer2 = customerService.findByGuid("102");
		CustomerAddress customerAddress3 = customer2.getAddressByGuid("1003");
		assertEquals("1003", customerAddress3.getGuid());
		assertEquals("CustomerFN", customerAddress3.getFirstName());
		assertEquals(CUSTOMER_L_N, customerAddress3.getLastName());
		assertEquals("2222222222", customerAddress3.getPhoneNumber());
		assertEquals(CUSTOMER_ADDRESS_LINE_1, customerAddress3.getStreet1());
		assertEquals(CUSTOMER_ADDRESS_LINE_2, customerAddress3.getStreet2());
		assertEquals("Customer sity", customerAddress3.getCity());
		assertEquals("AL", customerAddress3.getSubCountry());
		assertEquals("US", customerAddress3.getCountry());
		assertEquals(ZIPCODE, customerAddress3.getZipOrPostalCode());
	}

	/**
	 * Checks whether updating a subset of address attributes does not wipe out the other data.
	 * 
	 * @throws Exception on error
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateAlreadyExistingCustomerAddress() throws Exception {
		// import customers first
		executeImportJob(createInsertCustomerImportJob());

		// update customer addresses
		executeImportJob(createUpdateCustomerAddressImportJob());

		// update customer addresses once again with less data
		executeImportJob(createCustomUpdateCustomerAddressImportJob());

		Customer customer = customerService.findByGuid("101");
		CustomerAddress customerAddress = customer.getAddressByGuid(CUSTOMER_GUID);

		// assert new customer address has been created during import
		assertEquals(CUSTOMER_GUID, customerAddress.getGuid());
		assertEquals("CustomerFN", customerAddress.getFirstName());
		assertEquals(CUSTOMER_L_N, customerAddress.getLastName());
		assertEquals("1111111111", customerAddress.getPhoneNumber());
		assertEquals(CUSTOMER_ADDRESS_LINE_1, customerAddress.getStreet1());
		assertEquals(CUSTOMER_ADDRESS_LINE_2, customerAddress.getStreet2());
		assertEquals("Customer sity", customerAddress.getCity());
		assertEquals("AL", customerAddress.getSubCountry());
		assertEquals("US", customerAddress.getCountry());
		assertEquals(ZIPCODE, customerAddress.getZipOrPostalCode());
		
	}

	// DELETE is not supported for customer addresses
//	@DirtiesDatabase
//	@Test
//	public void testDeleteCustomerAddress() throws Exception {
//		// import customers first
//		executeImportJob(createInsertCustomerImportJob());
//
//		// update customer addresses
//		executeImportJob(createUpdateCustomerAddressImportJob());
//
//		// update customer addresses once again with less data
//		executeImportJob(createDeleteCustomerAddressImportJob());
//
//		CustomerService customerService = (CustomerService) elasticPath.getBean(ContextIdNames.CUSTOMER_SERVICE);
//		Customer customer = customerService.findByGuid("101");
//		assertTrue(customer.getAddresses().isEmpty());
//		
//	}
	
	/**
	 * Creates a custom update customer address import job with only a few import fields.
	 * 
	 * @return ImportJob instance
	 */
	protected ImportJob createCustomUpdateCustomerAddressImportJob() {
		List<ImportDataType> importDataTypes = importService.getCustomerImportDataTypes();
		String importDataTypeName = findByType(importDataTypes, ImportDataTypeCustomerAddressImpl.class).getName();
		Map<String, Integer> mappings = new HashMap<String, Integer>();

		mappings.put("guid", 1);
		mappings.put("customerGuid", 2);
		mappings.put("street1", STREET_ONE);
		mappings.put("street2", STREET_TWO);
		mappings.put("country", STREET_THREE);
		mappings.put("zipOrPostalCode", STREET_FOUR);

		ImportJob importJob = createSimpleImportJob(scenario.getStore(), Utils.uniqueCode("Update Customer Addresses"),
				"resources/importTests/customeraddress_update.csv", AbstractImportTypeImpl.UPDATE_TYPE, importDataTypeName, mappings);
		return importJob;
	}

//	protected ImportJob createDeleteCustomerAddressImportJob() {
//		List<ImportDataType> importDataTypes = importService.getCustomerImportDataTypes();
//		String importDataTypeName = findByType(importDataTypes, ImportDataTypeCustomerAddressImpl.class).getName();
//		Map<String, Integer> mappings = new HashMap<String, Integer>();
//
//		mappings.put("customerGuid", 2);
//
//		ImportJob importJob = createSimpleImportJob(scenario.getStore(), Utils.uniqueCode("Update Customer Addresses"),
//				"resources/importTests/customeraddress_update.csv", AbstractImportTypeImpl.DELETE_TYPE, importDataTypeName, mappings);
//		return importJob;
//	}

}
