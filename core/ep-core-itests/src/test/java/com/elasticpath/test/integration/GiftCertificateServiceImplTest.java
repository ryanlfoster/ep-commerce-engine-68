package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * An integration test for the GiftCertificateServiceImpl, we are testing from a client's point of view with Spring and the Database up and running.
 */
public class GiftCertificateServiceImplTest extends BasicSpringContextTest {

	/** The main object under test. */
	@Autowired
	private GiftCertificateService service;

	private SimpleStoreScenario scenario;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
	}

	/**
	 * A new non-persistent instance should be created when get(0) is called.
	 */
	@DirtiesDatabase
	@Test
	public void testGetWithUidPk0() {
		GiftCertificate giftCertificate = service.get(0);
		assertFalse(giftCertificate.isPersisted());
		assertEquals(0, giftCertificate.getUidPk());
	}

	/**
	 * Should be identical to testGetWithUidPk0 - implementation uses same code.
	 */
	@DirtiesDatabase
	@Test
	public void testLoadWithUidPk0() {
		GiftCertificate giftCertificate = service.load(0);
		assertFalse(giftCertificate.isPersisted());
		assertEquals(0, giftCertificate.getUidPk());
	}

	/**
	 * Should be identical to testGetWithUidPk0 - implementation uses same code.
	 */
	@DirtiesDatabase
	@Test
	public void testGetObjectWithUidPk0() {
		GiftCertificate giftCertificate = (GiftCertificate) service.getObject(0);
		assertFalse(giftCertificate.isPersisted());
		assertEquals(0, giftCertificate.getUidPk());
	}

	/**
	 * Should be identical to testGetWithUidPk0 - implementation uses same code.
	 */
	@DirtiesDatabase
	@Test
	public void testGetByUidWithUidPk0() {
		GiftCertificate giftCertificate = service.getByUid(0);
		assertFalse(giftCertificate.isPersisted());
		assertEquals(0, giftCertificate.getUidPk());
	}

	/**
	 * Create a new instance, store it and retrieve it.
	 */
	@DirtiesDatabase
	@Test
	public void testAddAndGet() {
		GiftCertificate giftCertificate = createGiftCertificate();

		giftCertificate = service.add(giftCertificate);

		GiftCertificate retrievedCertificate = service.get(giftCertificate.getUidPk());
		assertFalse("Shouldn't have uidPk of zero", 0 == retrievedCertificate.getUidPk());
		assertEquals(giftCertificate.getUidPk(), retrievedCertificate.getUidPk());
		assertNotSame("Should be distinct objects", retrievedCertificate, giftCertificate);
	}

	/**
	 * Should be exactly the same as testAddAndGet - the implementation uses the same code.
	 */
	@DirtiesDatabase
	@Test
	public void testAddAndLoad() {

		GiftCertificate giftCertificate = createGiftCertificate();
		giftCertificate = service.add(giftCertificate);

		GiftCertificate retrievedCertificate = service.load(giftCertificate.getUidPk());
		assertFalse("Shouldn't have uidPk of zero", 0 == retrievedCertificate.getUidPk());
		assertEquals(giftCertificate.getUidPk(), retrievedCertificate.getUidPk());
		assertNotSame("Should be distinct objects", retrievedCertificate, giftCertificate);
	}

	/**
	 * Should be exactly the same as testAddAndGet - the implementation uses the same code.
	 */
	@DirtiesDatabase
	@Test
	public void testAddAndGetObject() {

		GiftCertificate giftCertificate = createGiftCertificate();
		giftCertificate = service.add(giftCertificate);

		GiftCertificate retrievedCertificate = (GiftCertificate) service.getObject(giftCertificate.getUidPk());
		assertFalse("Shouldn't have uidPk of zero", 0 == retrievedCertificate.getUidPk());
		assertEquals(giftCertificate.getUidPk(), retrievedCertificate.getUidPk());
		assertNotSame("Should be distinct objects", retrievedCertificate, giftCertificate);
	}

	/**
	 * Should be exactly the same as testAddAndGet - the implementation uses the same code.
	 */
	@DirtiesDatabase
	@Test
	public void testAddAndGetUidPk() {
		GiftCertificate giftCertificate = createGiftCertificate();
		giftCertificate = service.add(giftCertificate);

		GiftCertificate retrievedCertificate = service.getByUid(giftCertificate.getUidPk());
		assertFalse("Shouldn't have uidPk of zero", 0 == retrievedCertificate.getUidPk());
		assertEquals(giftCertificate.getUidPk(), retrievedCertificate.getUidPk());
		assertNotSame("Should be distinct objects", retrievedCertificate, giftCertificate);
	}

	/**
	 * Tests that calling findByUids with no Uids plays nicely.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByUidsPassingNoUids() {
		final Set<Long> emptySet = Collections.emptySet();
		List<GiftCertificate> resultList = service.findByUids(emptySet);
		assertNotNull("Returned list shouldn't be null", resultList);
		assertEquals(0, resultList.size());
	}

	/**
	 * Test that we get the Gift Certificates we expect back out of the database and non that we don't want.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByUidsAndFindAllUids() {
		Set<Long> createdUids = new HashSet<Long>();

		GiftCertificate giftCertificate1 = createGiftCertificate();
		giftCertificate1 = service.add(giftCertificate1);
		createdUids.add(giftCertificate1.getUidPk());

		GiftCertificate giftCertificate2 = createGiftCertificate();
		giftCertificate2 = service.add(giftCertificate2);
		createdUids.add(giftCertificate2.getUidPk());

		GiftCertificate giftCertificate3 = createGiftCertificate();
		giftCertificate3 = service.add(giftCertificate3);
		createdUids.add(giftCertificate3.getUidPk());

		GiftCertificate giftCertificate4 = createGiftCertificate();
		giftCertificate4 = service.add(giftCertificate4);
		createdUids.add(giftCertificate4.getUidPk());

		// Test data doesn't include these Uids, but we are going to
		// look for them anyway.
		final Long[] invalidUids = new Long[] { 9999999L, 786767657L };
		createdUids.addAll(Arrays.asList(invalidUids));

		List<GiftCertificate> returnedCertificates = service.findByUids(createdUids);
		assertEquals(4, returnedCertificates.size());
		for (GiftCertificate certificate : returnedCertificates) {
			assertTrue(createdUids.contains(certificate.getUidPk()));
		}

	}

	/**
	 * Create a non-persistent gift certificate tied to the default store and with a creation date of new Date().
	 * 
	 * @return the gift certificate.
	 */
	private GiftCertificate createGiftCertificate() {
		GiftCertificate giftCertificate = getBeanFactory().getBean("giftCertificate");
		giftCertificate.setStore(scenario.getStore());
		giftCertificate.setCreationDate(new Date());
		return giftCertificate;
	}
}
