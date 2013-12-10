package com.elasticpath.service.search.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.search.SynonymGroup;
import com.elasticpath.domain.search.impl.SynonymGroupImpl;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Test case for {@link SynonymGroupServiceImpl}.
 */
public class SynonymGroupServiceImplTest extends AbstractEPServiceTestCase {

	private SynonymGroupServiceImpl synonymGroupServiceImpl;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		synonymGroupServiceImpl = new SynonymGroupServiceImpl();
		synonymGroupServiceImpl.setPersistenceEngine(getPersistenceEngine());
	}

	/**
	 * Test method for {@link SynonymGroupServiceImpl#saveOrUpdate(SynonymGroup)}.
	 */
	@Test
	public void testSaveOrUpdate() {
		final SynonymGroup synonymGroup = new SynonymGroupImpl();
		final SynonymGroup updatedSynonymGroup = new SynonymGroupImpl();
		final long uidPk = 123456;
		final String name = "updatedSynonymGroup";
		synonymGroup.setUidPk(uidPk);
		synonymGroup.setConceptTerm(name);
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).saveOrUpdate(with(same(synonymGroup)));
				will(returnValue(updatedSynonymGroup));
			}
		});
		final SynonymGroup returnedSynonymGroup = synonymGroupServiceImpl.saveOrUpdate(synonymGroup);
		assertSame(returnedSynonymGroup, updatedSynonymGroup);
	}

	/**
	 * Test method for {@link SynonymGroupServiceImpl#remove(SynonymGroup)}.
	 */
	@Test
	public void testRemove() {
		final SynonymGroup synonymGroup = context.mock(SynonymGroup.class);
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).delete(with(same(synonymGroup)));
			}
		});
		synonymGroupServiceImpl.remove(synonymGroup);
	}

	/**
	 * Test method for {@link SynonymGroupServiceImpl#getSynonymGroup(long)}.
	 */
	@Test
	public void testGetSynonymGroup() {
		stubGetBean(ContextIdNames.SYNONYM_GROUP, SynonymGroupImpl.class);

		final long uid = 1234L;
		final SynonymGroup synonymGroup = context.mock(SynonymGroup.class);
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).get(SynonymGroupImpl.class, uid);
				will(returnValue(synonymGroup));
			}
		});
		assertSame(synonymGroup, synonymGroupServiceImpl.getSynonymGroup(uid));
		assertSame(synonymGroup, synonymGroupServiceImpl.getObject(uid));

		final long nonExistUid = 3456L;
		context.checking(new Expectations() {
			{
				allowing(getMockPersistenceEngine()).get(SynonymGroupImpl.class, nonExistUid);
				will(returnValue(null));
			}
		});
		assertNull(synonymGroupServiceImpl.getSynonymGroup(nonExistUid));

		assertEquals(0, synonymGroupServiceImpl.getSynonymGroup(0).getUidPk());
	}

	/**
	 * Test method for {@link SynonymGroupServiceImpl#findAllSynonymGroupForCatalog(long)}.
	 */
	@Test
	public void testFindAllSynonymGroupForCatalog() {
		final Collection<SynonymGroup> groupList = new ArrayList<SynonymGroup>();
		// expectations
		context.checking(new Expectations() {
			{
				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("SYNONYM_GROUP_FIND_BY_CATALOG"), with(any(Object[].class)));
				will(returnValue(groupList));
			}
		});
		assertSame(groupList, synonymGroupServiceImpl.findAllSynonymGroupForCatalog(1L));
	}

	/**
	 * Test method for {@link SynonymGroupServiceImpl#conceptTermExists(String, Catalog, Locale)}.
	 */
	@Test
	public void testConceptTermExists() {
		final Catalog mockCatalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				allowing(mockCatalog).getUidPk();
				will(returnValue(2L));
			}
		});
		final Catalog catalog = mockCatalog;

		final Locale locale = Locale.CANADA;

		// test where exists
		final SynonymGroup mockSynonymGroup = context.mock(SynonymGroup.class);
		context.checking(new Expectations() {
			{
				allowing(mockSynonymGroup).getLocale();
				will(returnValue(locale));
			}
		});
		final SynonymGroup synonymGroup = mockSynonymGroup;
		context.checking(new Expectations() {
			{

				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("SYNONYM_GROUP_FIND_BY_CONCEPTTERM_CATALOG"), with(any(Object[].class)));
				will(returnValue(Arrays.asList(synonymGroup)));
			}
		});
		assertTrue(synonymGroupServiceImpl.conceptTermExists("some term", catalog, locale));
		context.checking(new Expectations() {
			{

				oneOf(getMockPersistenceEngine()).retrieveByNamedQuery(with("SYNONYM_GROUP_FIND_BY_CONCEPTTERM_CATALOG"), with(any(Object[].class)));
				will(returnValue(new ArrayList<SynonymGroup>()));
			}
		});
		assertFalse(synonymGroupServiceImpl.conceptTermExists("another term", catalog, locale));
	}
}
