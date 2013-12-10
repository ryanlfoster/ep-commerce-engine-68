package com.elasticpath.service.search.solr.query;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import org.apache.lucene.search.Query;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.cmuser.UserRole;
import com.elasticpath.domain.cmuser.UserStatus;
import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.UserSearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test <code>UserQueryComposerImpl</code>.
 */
public class UserQueryComposerImplTest extends QueryComposerTestCase {

	private static final String WHITESPACE_REGEX = "\\s";

	private UserQueryComposerImpl userQueryComposerImpl;

	private UserSearchCriteria searchCriteria;


	@Override
	public void setUp() throws Exception {
		super.setUp();

		userQueryComposerImpl = new UserQueryComposerImpl();
		userQueryComposerImpl.setAnalyzer(getAnalyzer());
		userQueryComposerImpl.setIndexUtility(getIndexUtility());

		searchCriteria = new UserSearchCriteria();
	}

	/**
	 * Test method for wrong search criteria.
	 */
	@Test
	public void testWrongSearchCriteria() {
		final SearchCriteria wrongSearchCriteria = new AbstractSearchCriteriaImpl() {
			private static final long serialVersionUID = 757953593088978600L;

			public void optimize() {
				// do nothing
			}

			public IndexType getIndexType() {
				return null;
			}
		};

		try {
			userQueryComposerImpl.composeQuery(wrongSearchCriteria, getSearchConfig());
			fail("EpServiceException expected for wrong search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		try {
			userQueryComposerImpl.composeFuzzyQuery(wrongSearchCriteria, getSearchConfig());
			fail("EpServiceException expected for wrong search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for empty search criteria.
	 */
	@Test
	public void testEmptyCriteria() {
		try {
			userQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for no parameters in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		try {
			userQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for no parameters in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.UserSearchCriteria.setCatalogCode(String)}.
	 */
	@Test
	public void testCatalogCode() {
		final String catalogCode = "catalog001";
		searchCriteria.setCatalogCode(catalogCode);

		Query query = userQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATALOG_CODE, String.valueOf(catalogCode));
		query = userQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATALOG_CODE, String.valueOf(catalogCode));
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.UserSearchCriteria.setStoreCode(String)}.
	 */
	@Test
	public void testStoreCode() {
		final String storeCode = "store001";
		searchCriteria.setStoreCode(storeCode);

		Query query = userQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.STORE_CODE, String.valueOf(storeCode));
		query = userQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.STORE_CODE, String.valueOf(storeCode));
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.UserSearchCriteria.setUserName(String)}.
	 */
	@Test
	public void testUserName() {
		final String userName = "user name";
		searchCriteria.setUserName(userName);

		Query query = userQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.USER_NAME, userName.split(WHITESPACE_REGEX));
		query = userQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.USER_NAME, userName.split(WHITESPACE_REGEX));
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.UserSearchCriteria.setLastName(String)}.
	 */
	@Test
	public void testLastName() {
		final String lastName = "last name";
		searchCriteria.setLastName(lastName);

		Query query = userQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.LAST_NAME, lastName.split(WHITESPACE_REGEX));
		query = userQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.LAST_NAME, lastName.split(WHITESPACE_REGEX));
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.UserSearchCriteria.setFirstName(String)}.
	 */
	@Test
	public void testFirstName() {
		final String userName = "first name";
		searchCriteria.setFirstName(userName);

		Query query = userQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.FIRST_NAME, userName.split(WHITESPACE_REGEX));
		query = userQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.FIRST_NAME, userName.split(WHITESPACE_REGEX));
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.UserSearchCriteria.setEmail(String)}.
	 */
	@Test
	public void testEmail() {
		final String email = "first@name.com";
		searchCriteria.setEmail(email);

		Query query = userQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.EMAIL, email.split(WHITESPACE_REGEX));
		query = userQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.EMAIL, email.split(WHITESPACE_REGEX));
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.UserSearchCriteria.setUserStatus(UserStatus)}.
	 */
	@Test
	public void testUserStatus() {
		final UserStatus userStatus = UserStatus.ENABLED;
		searchCriteria.setUserStatus(userStatus);

		Query query = userQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.STATUS, userStatus.getPropertyKey());
		query = userQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.STATUS, userStatus.getPropertyKey());
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.UserSearchCriteria.setUserRole(UserRole)}.
	 */
	@Test
	public void testUserRole() {
		String userRoleName = UserRole.CMUSER;		
		searchCriteria.setUserRoleName(userRoleName);
		
		Query query = userQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.USER_ROLE, userRoleName);
		query = userQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.USER_ROLE, userRoleName);
	}

	@Override
	protected QueryComposer getComposerUnderTest() {
		return userQueryComposerImpl;
	}

	@Override
	protected SearchCriteria getCriteriaUnderTest() {
		return searchCriteria;
	}
}
