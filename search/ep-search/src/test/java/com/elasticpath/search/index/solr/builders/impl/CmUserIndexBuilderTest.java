package com.elasticpath.search.index.solr.builders.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.cmuser.UserStatus;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.cmuser.impl.UserRoleImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.search.index.solr.document.impl.CmUserSolrInputDocumentCreator;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.IndexUtility;
import com.elasticpath.service.search.solr.IndexUtilityImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Tests for CmUserIndexBuilder class.
 */
public class CmUserIndexBuilderTest {

	private CmUserIndexBuilder cmUserIndexBuilder;
	
	private CmUserSolrInputDocumentCreator cmUserDocumentCreator;

	private AnalyzerImpl analyzer;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CmUserService cmUserServiceMock;

	private IndexUtility indexUtility;

	@Before
	public void setUp() throws Exception {
		cmUserIndexBuilder = new CmUserIndexBuilder();
		indexUtility = new IndexUtilityImpl();
		analyzer = new AnalyzerImpl();

		cmUserServiceMock = context.mock(CmUserService.class);

		cmUserIndexBuilder.setCmUserService(cmUserServiceMock);

		cmUserDocumentCreator = new CmUserSolrInputDocumentCreator();
		cmUserDocumentCreator.setAnalyzer(analyzer);
		cmUserDocumentCreator.setIndexUtility(indexUtility);
		cmUserDocumentCreator.setPipelinePerformance(new PipelinePerformanceImpl());
		
	}

	/**
	 * Tests getName() method.
	 */
	@Test
	public void testGetName() {
		assertEquals(SolrIndexConstants.CMUSER_SOLR_CORE, cmUserIndexBuilder.getName());
	}

	/**
	 * Tests getIndexType() method.
	 */
	@Test
	public void testGetIndexType() {
		assertEquals(IndexType.CMUSER, cmUserIndexBuilder.getIndexType());
	}

	/**
	 * Tests findUidsByNotification() method.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void testFindUidsByNotification() {
		cmUserIndexBuilder.findUidsByNotification(null);
	}

	/**
	 * Tests findDeletedUids() method.
	 */
	@Test
	public void testFindDeletedUids() {
		assertSame(Collections.emptyList(), cmUserIndexBuilder.findDeletedUids(null));
	}

	/**
	 * Tests findAllUids() method.
	 */
	@Test
	public void testFindAllUids() {
		final List<Long> result = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(cmUserServiceMock).findAllUids();
				will(returnValue(result));
			}
		});
		assertSame(result, cmUserIndexBuilder.findAllUids());
	}

	/**
	 * Tests findAddedOrModifiedUids() method.
	 */
	@Test
	public void testFindAddedOrModifiedUids() {
		final Date date = new Date();
		final List<Long> result = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(cmUserServiceMock).findUidsByModifiedDate(date);
				will(returnValue(result));
			}
		});
		assertSame(result, cmUserIndexBuilder.findAddedOrModifiedUids(date));
	}

	/**
	 * Tests createDocument() method.
	 */
	@Test
	public void testCreateDocument() {
		CmUser cmUser = createCmUser();

		cmUserDocumentCreator.setEntity(cmUser);
		final SolrInputDocument createDocument = cmUserDocumentCreator.createDocument();

		assertEquals(String.valueOf(cmUser.getUidPk()), createDocument.getFieldValue(SolrIndexConstants.OBJECT_UID));
		assertEquals(cmUser.getUserName(), createDocument.getFieldValue(SolrIndexConstants.USER_NAME));
		assertEquals(cmUser.getLastName(), createDocument.getFieldValue(SolrIndexConstants.LAST_NAME));
		assertEquals(cmUser.getFirstName(), createDocument.getFieldValue(SolrIndexConstants.FIRST_NAME));
		assertEquals(cmUser.getEmail(), createDocument.getFieldValue(SolrIndexConstants.EMAIL));
		assertEquals(UserStatus.ENABLED.getPropertyKey(), createDocument.getFieldValue(SolrIndexConstants.STATUS));
		assertEquals(String.valueOf(cmUser.isAllCatalogsAccess()), createDocument.getFieldValue(SolrIndexConstants.ALL_CATALOGS_ACCESS));
		assertEquals(String.valueOf(cmUser.isAllStoresAccess()), createDocument.getFieldValue(SolrIndexConstants.ALL_STORES_ACCESS));
		
		assertTrue(CollectionUtils.subtract(Arrays.asList("role1", "role2"), 
				createDocument.getFieldValues(SolrIndexConstants.USER_ROLE)).size() == 0);
		
		assertTrue(CollectionUtils.subtract(Arrays.asList("catalog1", "catalog2"), 
				createDocument.getFieldValues(SolrIndexConstants.CATALOG_CODE)).size() == 0);
		
		assertTrue(CollectionUtils.subtract(Arrays.asList("store1", "store2"), 
				createDocument.getFieldValues(SolrIndexConstants.STORE_CODE)).size() == 0);
	}

	private CmUser createCmUser() {
		CmUser cmUser = new CmUserImpl() {
			private static final long serialVersionUID = -5534887622471126321L;

			@Override
			public boolean isAccountNonLocked() {
				return true;
			}
		};
		cmUser.setGuid("guid");
		cmUser.initialize();
		cmUser.setUidPk(1L);
		cmUser.setUserName("userName");
		cmUser.setLastName("lastName");
		cmUser.setFirstName("firstName");
		cmUser.setEmail("email");
		cmUser.setEnabled(true);
		cmUser.setAllCatalogsAccess(false);
		cmUser.setAllStoresAccess(true);

		UserRole userRole1 = new UserRoleImpl();
		userRole1.setName("role1");
		UserRole userRole2 = new UserRoleImpl();
		userRole2.setName("role2");
		cmUser.addUserRole(userRole1);
		cmUser.addUserRole(userRole2);

		Catalog catalog1 = new CatalogImpl();
		catalog1.setCode("catalog1");
		Catalog catalog2 = new CatalogImpl();
		catalog2.setCode("catalog2");
		cmUser.addCatalog(catalog1);
		cmUser.addCatalog(catalog2);

		Store store1 = new StoreImpl();
		store1.setCode("store1");
		Store store2 = new StoreImpl();
		store2.setCode("store2");
		cmUser.addStore(store1);
		cmUser.addStore(store2);

		return cmUser;
	}

}
