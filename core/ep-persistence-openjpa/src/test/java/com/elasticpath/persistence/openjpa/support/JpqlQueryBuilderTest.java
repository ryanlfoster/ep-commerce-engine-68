package com.elasticpath.persistence.openjpa.support;

import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup.ConjunctionType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

/**
 * Test class for {@link com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder}.
 */
public class JpqlQueryBuilderTest {


	private static final String ALIAS = "cust";
	private static final String TEST_ENTITY_TABLE = "CustomerImpl";
	private static final String SELECT_DISTINCT = "SELECT DISTINCT ";

	private JpqlQueryBuilder query;

	/**
	 * Set up.
	 */
	@Before
	public void setUp() {
	query = new JpqlQueryBuilder(TEST_ENTITY_TABLE, ALIAS);
	}

	/**
	 * That that appending the builder with a join clause and a few AND clauses having a nested OR clause
	 * will output the correct JPQL.
	 */
	@Test
    public void testToStringWithAndNestedOrJoin() {
    	// Append inner joins
    	query.appendInnerJoin("CustomerAddressImpl", "addr");

    	// Append where clause
    	JpqlQueryBuilderWhereGroup andGroup = query.getDefaultWhereGroup();
    	andGroup.appendWhereEquals("cust.gender", "M");
	JpqlQueryBuilderWhereGroup orGroup = new JpqlQueryBuilderWhereGroup(ConjunctionType.OR);
    	orGroup.appendLikeWithWildcards("cust.fullName", "Smith");
    	orGroup.appendLikeWildcardOnStart("cust.fullName", "Jones");
    	andGroup.appendWhereGroup(orGroup);
    	andGroup.appendLikeWildcardOnEnd("cust.lastName", "Edwards");

    	// Append order by clause
    	query.appendOrderBy("addr.country", true);
    	query.appendOrderBy("addr.subCountry", true);
    	query.appendOrderBy("addr.city", true);

    	Iterator<Object> iterator = query.getParameterList().iterator();

    	Assert.assertEquals("first element should be M", "M", iterator.next());
    	Assert.assertEquals("fourth element should be Edwards", "Edwards%", iterator.next());
    	Assert.assertEquals("second element should be Smith", "%Smith%", iterator.next());
    	Assert.assertEquals("third element should be Jones", "%Jones", iterator.next());

    	Assert.assertEquals("SELECT cust FROM CustomerImpl AS cust JOIN CustomerAddressImpl AS addr"
			    + " WHERE cust.gender = ?1 AND cust.lastName LIKE ?2 AND (cust.fullName LIKE ?3 OR cust.fullName LIKE ?4)"
			    + " ORDER BY addr.country ASC, addr.subCountry ASC, addr.city ASC",
			    query.toString());
    	}

	/**
	 * Test {@link com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder#distinct()} method.
	 */
	@Test
	public void testDistinct() {
		query.distinct();

		final String queryText = query.toString();
		Assert.assertNotNull(queryText);
		Assert.assertTrue("The query should start with '" + SELECT_DISTINCT + "'.", queryText.startsWith(SELECT_DISTINCT));
	}

}
