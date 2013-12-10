/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.catalog.impl;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.impl.GiftCertificateImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test suite for <code>GiftCertificateServiceImpl</code>.
 */
public class GiftCertificateServiceImplTest extends AbstractEPServiceTestCase {

	private static final String INCORRECT_EXCEPTION_MESSAGE = "Exception message incorrect";

	private static final String SERVICE_EXCEPTION_EXPECTED = "EpServiceException expected.";

	private static final String PLACEHOLDER_FOR_LIST = "list";

	private static final String FIND_BY_GUID_NAMED_QUERY = "GIFT_CERTIFICATE_FIND_BY_GUID";

	private static final String FIND_BY_CODE_NAMED_QUERY = "GIFT_CERTIFICATE_FIND_BY_CODE";

	private static final String FIND_BY_CODE_AND_STORE_NAMED_QUERY = "GIFT_CERTIFICATE_FIND_BY_CODE_AND_STORE";

	private static final String NON_EXISTENT_GIFT_CERTIFICATE_GUID = "1c2f7f69-5417-40dc-9216-431d6da50f44";

	private static final String NON_EXISTENT_GIFT_CERTIFICATE_CODE = "GC_1c2f7f69-5417-40dc-9216-431d6da50f44";

	private static final String ORDINARY_GIFT_CERTIFICATE_GUID = "81524b1e-b463-40aa-a2d0-b3dfe065a0fc";

	private static final String ORDINARY_GIFT_CERTIFICATE_CODE = "GC_81524b1e-b463-40aa-a2d0-b3dfe065a0fc";

	private static final String DUPLICATED_GIFT_CERTIFICATE_GUID = "5d98280e-ee03-47b5-903a-3d2ddbc62390";

	private static final String DUPLICATED_GIFT_CERTIFICATE_CODE = "GC_5d98280e-ee03-47b5-903a-3d2ddbc62390";

	private static final long TEST_STORE_UID = 9999;

	private static final Store TEST_STORE = new StoreImpl();


	private GiftCertificateService giftCertificateService;

	private PersistenceEngine mockPersistenceEngine;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		GiftCertificateServiceImpl giftCertificateServiceImpl = new GiftCertificateServiceImpl();
//		giftCertificateServiceImpl.setUtility(getUtility());
		mockPersistenceEngine = getMockPersistenceEngine();
		setUpPersistenceForFindByGuid();
		setUpPersistenceForFindByGiftCertificateCode();
		giftCertificateServiceImpl.setPersistenceEngine(mockPersistenceEngine);
		giftCertificateService = giftCertificateServiceImpl;

		TEST_STORE.setUidPk(TEST_STORE_UID);
	}

	/**
	 * Setup up mock persistence engine for findByGuid.
	 * @throws Exception on error.
	 */
	public void setUpPersistenceForFindByGuid() throws Exception {

		// query returns no results
		setUpPersistenceNamedQueryWithListResult(
				FIND_BY_GUID_NAMED_QUERY, Collections.<GiftCertificate>emptyList(), NON_EXISTENT_GIFT_CERTIFICATE_GUID);

		// query returns one result
		final List<GiftCertificate> successfulGiftCertificateQueryResult = createGiftCertificatesFromGuids(ORDINARY_GIFT_CERTIFICATE_GUID);
		setUpPersistenceNamedQueryWithListResult(FIND_BY_GUID_NAMED_QUERY,
				successfulGiftCertificateQueryResult, ORDINARY_GIFT_CERTIFICATE_GUID);

		// query returns more than one result
		final List<GiftCertificate> duplicateGiftCertificateQueryResult =
				createGiftCertificatesFromGuids(DUPLICATED_GIFT_CERTIFICATE_GUID, DUPLICATED_GIFT_CERTIFICATE_GUID);
		setUpPersistenceNamedQueryWithListResult(FIND_BY_GUID_NAMED_QUERY,
				duplicateGiftCertificateQueryResult, DUPLICATED_GIFT_CERTIFICATE_GUID);

	}

	/**
	 * Setup up mock persistence engine for findByGiftCertificateCode.
	 * @throws Exception on error.
	 */
	public void setUpPersistenceForFindByGiftCertificateCode() throws Exception {
		// query returns no results
		setUpPersistenceNamedQueryWithListResult(
				FIND_BY_CODE_NAMED_QUERY, Collections.<GiftCertificate>emptyList(), NON_EXISTENT_GIFT_CERTIFICATE_CODE);

		setUpPersistenceNamedQueryWithListResult(FIND_BY_CODE_AND_STORE_NAMED_QUERY, Collections.<GiftCertificate>emptyList(),
				NON_EXISTENT_GIFT_CERTIFICATE_CODE, TEST_STORE_UID);

		// query returns one result
		final List<GiftCertificate> successfulGiftCertificateQueryResult = createGiftCertificatesFromCodes(ORDINARY_GIFT_CERTIFICATE_CODE);
		setUpPersistenceNamedQueryWithListResult(FIND_BY_CODE_NAMED_QUERY,
				successfulGiftCertificateQueryResult, ORDINARY_GIFT_CERTIFICATE_CODE);

		setUpPersistenceNamedQueryWithListResult(FIND_BY_CODE_AND_STORE_NAMED_QUERY,
				successfulGiftCertificateQueryResult, ORDINARY_GIFT_CERTIFICATE_CODE, TEST_STORE_UID);

		// query returns more than one result
		final List<GiftCertificate> duplicateGiftCertificateQueryResult =
			createGiftCertificatesFromCodes(DUPLICATED_GIFT_CERTIFICATE_CODE, DUPLICATED_GIFT_CERTIFICATE_CODE);
		setUpPersistenceNamedQueryWithListResult(FIND_BY_CODE_NAMED_QUERY,
				duplicateGiftCertificateQueryResult, DUPLICATED_GIFT_CERTIFICATE_CODE);

		setUpPersistenceNamedQueryWithListResult(FIND_BY_CODE_AND_STORE_NAMED_QUERY,
				duplicateGiftCertificateQueryResult, DUPLICATED_GIFT_CERTIFICATE_CODE, TEST_STORE_UID);

	}

	/**
	 * Sets up the mocked persistence engine with list results for a named query.
	 * @param namedQuery the named query to submit
	 * @param queryResultList list of gift certificates to return
	 * @param queryParameters the query parameters to pass with the named query.
	 */
	public void setUpPersistenceNamedQueryWithListResult(final String namedQuery,
			final List<GiftCertificate> queryResultList, final Object... queryParameters) {
		context.checking(new Expectations() {
			{

				allowing(mockPersistenceEngine).retrieveByNamedQuery(namedQuery, queryParameters);
				will(returnValue(queryResultList));
			}
		});

	}

	/**
	 * Generates a list of new {@link com.elasticpath.domain.catalog.GiftCertificate} with the supplied guids.
	 * @param giftCertificateGuids the guids to generate Gift Certificates from.
	 * @return list of {@link com.elasticpath.domain.catalog.GiftCertificate}s.
	 */
	protected List<GiftCertificate> createGiftCertificatesFromGuids(final String ... giftCertificateGuids) {
		final List<GiftCertificate> giftCertificates = new ArrayList<GiftCertificate>();

		for (String giftCertificateGuid : giftCertificateGuids) {
			GiftCertificate giftCertificate = new GiftCertificateImpl();
			giftCertificate.setGuid(giftCertificateGuid);
			giftCertificates.add(giftCertificate);
		}

		return giftCertificates;
	}

	/**
	 * Generates a list of new {@link com.elasticpath.domain.catalog.GiftCertificate} with the supplied code.
	 * @param giftCertificateCodes the codes to generate Gift Certificates from.
	 * @return list of {@link com.elasticpath.domain.catalog.GiftCertificate}s.
	 */
	protected List<GiftCertificate> createGiftCertificatesFromCodes(final String ... giftCertificateCodes) {
		final List<GiftCertificate> giftCertificates = new ArrayList<GiftCertificate>();

		for (String giftCertificateCode : giftCertificateCodes) {
			GiftCertificate giftCertificate = new GiftCertificateImpl();
			giftCertificate.setGuid(UUID.randomUUID().toString());
			giftCertificate.setGiftCertificateCode(giftCertificateCode);
			giftCertificates.add(giftCertificate);
		}

		return giftCertificates;
	}


	/**
	 * Test method for 'com.elasticpath.service.GiftCertificateServiceImpl.setPersistenceEngine(PersistenceEngine)'.
	 */
	@Test
	public void testSetPersistenceEngine() {
		giftCertificateService.setPersistenceEngine(null);
		try {
			giftCertificateService.add(new GiftCertificateImpl());
			fail(SERVICE_EXCEPTION_EXPECTED);
		} catch (final EpServiceException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.service.GiftCertificateServiceImpl.getPersistenceEngine()'.
	 */
	@Test
	public void testGetPersistenceEngine() {
		assertNotNull(giftCertificateService.getPersistenceEngine());
	}

	/**
	 * Returns a new <code>GiftCertificate</code> instance.
	 *
	 * @return a new <code>GiftCertificate</code> instance.
	 */
	protected GiftCertificate getGiftCertificate() {
		final GiftCertificate giftCertificate = new GiftCertificateImpl();
		giftCertificate.initialize();
		// Hard coding it for the time being will replace with some random code generator
		giftCertificate.setGiftCertificateCode("GC0123456789ABCDEF");
		return giftCertificate;
	}

	/**
	 * Test method for 'com.elasticpath.service.GiftCertificateServiceImpl.add(GiftCertificate)'.
	 */
	@Test
	public void testAdd() {
		final GiftCertificate giftCertificate = new GiftCertificateImpl();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(with(same(giftCertificate)));
			}
		});
		giftCertificateService.add(giftCertificate);
	}

	/**
	 * Test method for 'com.elasticpath.service.GiftCertificateServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoad() {
		stubGetBean(ContextIdNames.GIFT_CERTIFICATE, GiftCertificateImpl.class);

		final long uid = 1234L;
		final GiftCertificate giftCertificate = new GiftCertificateImpl();
		giftCertificate.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(GiftCertificateImpl.class, uid);
				will(returnValue(giftCertificate));
			}
		});
		final GiftCertificate loadedGiftCertificate = giftCertificateService.load(uid);
		assertSame(giftCertificate, loadedGiftCertificate);
	}

	/**
	 * Test method for 'com.elasticpath.service.GiftCertificateServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoadAnNonExistGiftCertificate() {
		stubGetBean(ContextIdNames.GIFT_CERTIFICATE, GiftCertificateImpl.class);

		final long uid = 1234L;
		final GiftCertificate giftCertificate = new GiftCertificateImpl();
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(GiftCertificateImpl.class, uid);
				will(returnValue(giftCertificate));
			}
		});
		assertSame(giftCertificate, giftCertificateService.load(uid));
	}

	/**
	 * Test method for 'com.elasticpath.service.GiftCertificateServiceImpl.load(Long)'.
	 */
	@Test
	public void testLoadANewGiftCertificate() {
		stubGetBean(ContextIdNames.GIFT_CERTIFICATE, GiftCertificateImpl.class);

		assertNotNull(giftCertificateService.load(0L));
	}

	/**
	 * Test method for 'com.elasticpath.service.GiftCertificateServiceImpl.saveOrUpdate(GiftCertificate)'.
	 */
	@Test
	public void testSaveOrUpdate() {
		final GiftCertificate giftCertificate = new GiftCertificateImpl();

		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).saveOrUpdate(with(same(giftCertificate)));
				will(returnValue(null));
			}
		});

		giftCertificateService.saveOrUpdate(giftCertificate);
	}

	/**
	 * Test method for 'com.elasticpath.service.GiftCertificateServiceImpl.findByModifiedDate(Date)'.
	 */
	@Test
	public void testFindByModifiedDate() {
		final Date date = new Date();
		final List<GiftCertificate> giftCertificates = new ArrayList<GiftCertificate>();
		context.checking(new Expectations() {
			{

				allowing(mockPersistenceEngine).retrieveByNamedQuery("GIFT_CERTIFICATE_SELECT_BY_MODIFIED_DATE", date);
				will(returnValue(giftCertificates));
			}
		});
		assertSame(giftCertificates, giftCertificateService.findByModifiedDate(date));
	}


	/**
	 * Test method for 'com.elasticpath.service.GiftCertificateServiceImpl.findByGiftCertificateUids(List)'.
	 */
	@Test
	public void testFindByGiftCertificateUids() {
		final List<Long> giftCertificateUids = new ArrayList<Long>();
		giftCertificateUids.add(Long.valueOf("1"));
		giftCertificateUids.add(Long.valueOf("2"));
		giftCertificateUids.add(Long.valueOf("3"));

		// expectations
		final List<GiftCertificate> giftCertificates = new ArrayList<GiftCertificate>();
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).retrieveByNamedQueryWithList(
						with("GIFT_CERTIFICATE_BY_UIDS"), with(PLACEHOLDER_FOR_LIST), with(giftCertificateUids), with(any(Object[].class)));
				will(returnValue(giftCertificates));
			}
		});
		assertSame(giftCertificates, this.giftCertificateService.findByUids(giftCertificateUids));

		// Should return an empty list if no giftCertificate uid is given.
		List<GiftCertificate> result = this.giftCertificateService.findByUids(new ArrayList<Long>());
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Test com.elasticpath.service.catalog.impl.GiftCertificateServiceImpl.findByGuid(String) with a null guid.
	 */
	@Test
	public void testFindByGuidWithNullGuid() {
		String nullGuid = null;
		try {
			giftCertificateService.findByGuid(nullGuid);

			fail("EpServiceException should have been thrown for null guid.");

		} catch (EpServiceException giftCertificateServiceException) {

			org.hamcrest.MatcherAssert.assertThat(INCORRECT_EXCEPTION_MESSAGE, giftCertificateServiceException.getMessage(),
					containsString("Cannot retrieve null giftCertificate GUID"));
		}
	}

	/**
	 * Test com.elasticpath.service.catalog.impl.GiftCertificateServiceImpl.findByGuid(String) with two certificates with identical guids.
	 */
	@Test
	public void testFindByGuidWithDuplicateGuids() {

		try {
			giftCertificateService.findByGuid(DUPLICATED_GIFT_CERTIFICATE_GUID);

			fail("EpServiceException should have been thrown for duplicate guid.");

		} catch (EpServiceException giftCertificateServiceException) {

			org.hamcrest.MatcherAssert.assertThat(INCORRECT_EXCEPTION_MESSAGE, giftCertificateServiceException.getMessage(),
					containsString("Inconsistent data. Duplicate gift certificate identifier(s) exist"));
		}
	}

	/**
	 * Test com.elasticpath.service.catalog.impl.GiftCertificateServiceImpl.findByGuid(String) with a non-existent gift certificate.
	 */
	@Test
	public void testFindByGuidReturnsNullIfGiftCertificateNotFound() {

		assertSame(null, giftCertificateService.findByGuid(NON_EXISTENT_GIFT_CERTIFICATE_GUID));
	}

	/**
	 * Test com.elasticpath.service.catalog.impl.GiftCertificateServiceImpl.findByGuid(String) with a valid gift certificate guid.
	 */
	@Test
	public void testFindByGuidOnExistingGiftCertificate() {

		GiftCertificate giftCertificate = giftCertificateService.findByGuid(ORDINARY_GIFT_CERTIFICATE_GUID);

		assertNotNull(giftCertificate);
		assertEquals(giftCertificate.getGuid(), ORDINARY_GIFT_CERTIFICATE_GUID);
	}

	/**
	 * Test com.elasticpath.service.catalog.impl.GiftCertificateServiceImpl.findByGiftCertificateCode(String) with a null code.
	 */
	@Test
	public void testFindByCodeWithNullCode() {
		String nullCode = null;
		try {
			giftCertificateService.findByGiftCertificateCode(nullCode);

			fail("EpServiceException should have been thrown for null code.");

		} catch (EpServiceException giftCertificateServiceException) {

			org.hamcrest.MatcherAssert.assertThat(INCORRECT_EXCEPTION_MESSAGE, giftCertificateServiceException.getMessage(),
					containsString("Cannot retrieve null giftCertificateCode"));
		}
	}

	/**
	 * Test com.elasticpath.service.catalog.impl.GiftCertificateServiceImpl.findByGiftCertificateCode(String, Store) with a null code.
	 */
	@Test
	public void testFindByCodeAndStoreWithNullCode() {
		String nullCode = null;
		try {
			giftCertificateService.findByGiftCertificateCode(nullCode, TEST_STORE);

			fail("IllegalArgumentException should have been thrown for null code.");

		} catch (IllegalArgumentException giftCertificateServiceException) {

			org.hamcrest.MatcherAssert.assertThat(INCORRECT_EXCEPTION_MESSAGE, giftCertificateServiceException.getMessage(),
					containsString("can not be null"));
		}
	}

	/**
	 * Test com.elasticpath.service.catalog.impl.GiftCertificateServiceImpl.findByGiftCertificateCode(String)
	 * with two certificates with identical codes.
	 */
	@Test
	public void testFindByCodeWithDuplicateCode() {

		try {
			giftCertificateService.findByGiftCertificateCode(DUPLICATED_GIFT_CERTIFICATE_CODE);

			fail("EpServiceException should have been thrown for duplicate code.");

		} catch (EpServiceException giftCertificateServiceException) {

			org.hamcrest.MatcherAssert.assertThat(INCORRECT_EXCEPTION_MESSAGE, giftCertificateServiceException.getMessage(),
					containsString("Inconsistent data. Duplicate gift certificate identifier(s) exist"));
		}
	}

	/**
	 * Test com.elasticpath.service.catalog.impl.GiftCertificateServiceImpl.findByGiftCertificateCode(String, Store)
	 * with two certificates with identical codes.
	 */
	@Test
	public void testFindByCodeAndStoreWithDuplicateCode() {

		try {
			giftCertificateService.findByGiftCertificateCode(DUPLICATED_GIFT_CERTIFICATE_CODE, TEST_STORE);

			fail("EpServiceException should have been thrown for duplicate code.");

		} catch (EpServiceException giftCertificateServiceException) {

			org.hamcrest.MatcherAssert.assertThat(INCORRECT_EXCEPTION_MESSAGE, giftCertificateServiceException.getMessage(),
					containsString("Inconsistent data. Duplicate gift certificate identifier(s) exist"));
		}
	}

	/**
	 * Test com.elasticpath.service.catalog.impl.GiftCertificateServiceImpl.findByGiftCertificateCode(String)
	 * with a valid gift certificate code.
	 */
	@Test
	public void testFindByCodeOnExistingGiftCertificate() {

		GiftCertificate giftCertificate = giftCertificateService.findByGiftCertificateCode(ORDINARY_GIFT_CERTIFICATE_CODE);

		assertNotNull(giftCertificate);
		assertEquals(giftCertificate.getGiftCertificateCode(), ORDINARY_GIFT_CERTIFICATE_CODE);
	}

	/**
	 * Test com.elasticpath.service.catalog.impl.GiftCertificateServiceImpl.findByGiftCertificateCode(String, Store)
	 * with a valid gift certificate code.
	 */
	@Test
	public void testFindByCodeAndStoreOnExistingGiftCertificate() {

		GiftCertificate giftCertificate = giftCertificateService.findByGiftCertificateCode(ORDINARY_GIFT_CERTIFICATE_CODE, TEST_STORE);

		assertNotNull(giftCertificate);
		assertEquals(giftCertificate.getGiftCertificateCode(), ORDINARY_GIFT_CERTIFICATE_CODE);
	}

	/**
	 * Test method for 'com.elasticpath.service.CategoryServiceImpl.removeGiftCertificate(long)'.
	 */
	@Test
	public void testRemoveGiftCertificate() {
		stubGetBean(ContextIdNames.GIFT_CERTIFICATE, GiftCertificateImpl.class);

		final long giftCertificateUid = 23456L;
		final GiftCertificate giftCertificate = getGiftCertificate();
		giftCertificate.initialize();
		giftCertificate.setUidPk(giftCertificateUid);

		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).bulkUpdate(with(any(String.class)), with(any(Object[].class)));
				will(returnValue(1));

				allowing(mockPersistenceEngine).get(GiftCertificateImpl.class, giftCertificateUid);
				will(returnValue(giftCertificate));

				allowing(mockPersistenceEngine).delete(with(same(giftCertificate)));
			}
		});

		giftCertificateService.removeGiftCertificate(giftCertificateUid);
	}
}
