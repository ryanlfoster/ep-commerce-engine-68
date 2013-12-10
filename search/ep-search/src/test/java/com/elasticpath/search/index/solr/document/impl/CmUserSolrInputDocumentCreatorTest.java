package com.elasticpath.search.index.solr.document.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.SolrInputDocument;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.cmuser.UserStatus;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.cmuser.impl.UserRoleImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.search.index.pipeline.IndexingStage;
import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test construction of {@link SolrInputDocument}s from {@link CmUser}s using {@link CmUserSolrInputDocumentCreator}.
 */

public class CmUserSolrInputDocumentCreatorTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private CmUserSolrInputDocumentCreator documentCreator;

	private PipelinePerformance pipelinePerformance;

	@SuppressWarnings("unchecked")
	private final IndexingStage<SolrInputDocument, ?> nextStage = context.mock(IndexingStage.class);

	/**
	 * Set up the test case.
	 */
	@Before
	public void setUp() {
		documentCreator = new CmUserSolrInputDocumentCreator();
		pipelinePerformance = context.mock(PipelinePerformance.class);

		documentCreator.setAnalyzer(new AnalyzerImpl());
		documentCreator.setPipelinePerformance(pipelinePerformance);
		documentCreator.setNextStage(nextStage);
	}

	/**
	 * Tests {@link CmUserSolrInputDocumentCreator#run()} method.
	 */
	@Test
	public void testRun() {
		final CmUser cmUser = createCmUser();
		documentCreator.setEntity(cmUser);

		context.checking(new Expectations() {
			{
				exactly(2).of(pipelinePerformance).addCount(with(any(String.class)), with(any(Long.class)));
				oneOf(nextStage).send(with(any(SolrInputDocument.class)));
			}
		});

		documentCreator.run();
	}

	/**
	 * Tests {@link CmUserSolrInputDocumentCreator#run()} method with null user.
	 */
	@Test
	public void testRunNullCmUser() {
		documentCreator.setEntity(null);

		documentCreator.run();
	}

	/**
	 * Tests {@link CmUserSolrInputDocumentCreator#run()} method with null nextStage.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRunNullNextStage() {
		documentCreator.setEntity(createCmUser());
		documentCreator.setNextStage(null);
		documentCreator.run();
	}

	/**
	 * Tests {@link CmUserSolrInputDocumentCreator#createDocument()} method.
	 */
	@Test
	public void testCreateDocument() {
		final CmUser cmUser = createCmUser();
		documentCreator.setEntity(cmUser);

		final SolrInputDocument createdDocument = documentCreator.createDocument();

		assertEquals(String.valueOf(cmUser.getUidPk()), createdDocument.getFieldValue(SolrIndexConstants.OBJECT_UID));
		assertEquals(cmUser.getUserName(), createdDocument.getFieldValue(SolrIndexConstants.USER_NAME));
		assertEquals(cmUser.getLastName(), createdDocument.getFieldValue(SolrIndexConstants.LAST_NAME));
		assertEquals(cmUser.getFirstName(), createdDocument.getFieldValue(SolrIndexConstants.FIRST_NAME));
		assertEquals(cmUser.getEmail(), createdDocument.getFieldValue(SolrIndexConstants.EMAIL));
		assertEquals(UserStatus.ENABLED.getPropertyKey(), createdDocument.getFieldValue(SolrIndexConstants.STATUS));
		assertEquals(String.valueOf(cmUser.isAllCatalogsAccess()), createdDocument.getFieldValue(SolrIndexConstants.ALL_CATALOGS_ACCESS));
		assertEquals(String.valueOf(cmUser.isAllStoresAccess()), createdDocument.getFieldValue(SolrIndexConstants.ALL_STORES_ACCESS));

		assertTrue(CollectionUtils.subtract(Arrays.asList("role1", "role2"), 
				createdDocument.getFieldValues(SolrIndexConstants.USER_ROLE)).size() == 0);

		assertTrue(CollectionUtils.subtract(Arrays.asList("catalog1", "catalog2"), 
				createdDocument.getFieldValues(SolrIndexConstants.CATALOG_CODE))
				.size() == 0);

		assertTrue(CollectionUtils.subtract(Arrays.asList("store1", "store2"), 
				createdDocument.getFieldValues(SolrIndexConstants.STORE_CODE)).size() == 0);
	}

	/**
	 * Tests {@link CmUserSolrInputDocumentCreator#createDocument()} method with a null {@link CmUser}.
	 */
	@Test
	public void testCreateDocumentWithNullCmUser() {
		final SolrInputDocument createdDocument = documentCreator.createDocument();
		assertNull(createdDocument);
	}

	/**
	 * Test {@link CmUserSolrInputDocumentCreator#setEntity(Object)}. Ensure cmUser gets set.
	 */
	@Test
	public void testSetEntity() {
		assertNull(documentCreator.getEntity());

		final CmUserImpl entity = new CmUserImpl();
		documentCreator.setEntity(entity);

		assertEquals(documentCreator.getEntity(), entity);
	}

	private CmUser createCmUser() {
		final CmUser cmUser = new CmUserImpl() {
			private static final long serialVersionUID = -6507569527118530890L;

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

		final UserRole userRole1 = new UserRoleImpl();
		userRole1.setName("role1");
		final UserRole userRole2 = new UserRoleImpl();
		userRole2.setName("role2");
		cmUser.addUserRole(userRole1);
		cmUser.addUserRole(userRole2);

		final Catalog catalog1 = new CatalogImpl();
		catalog1.setCode("catalog1");
		final Catalog catalog2 = new CatalogImpl();
		catalog2.setCode("catalog2");
		cmUser.addCatalog(catalog1);
		cmUser.addCatalog(catalog2);

		final Store store1 = new StoreImpl();
		store1.setCode("store1");
		final Store store2 = new StoreImpl();
		store2.setCode("store2");
		cmUser.addStore(store1);
		cmUser.addStore(store2);

		return cmUser;
	}
}
