package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Locale;

import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test <code>AbstractLocaleDependantFieldsImpl</code>.
 */
public class AbstractLocaleDependantFieldsImplTest extends AbstractEPTestCase {


	private AbstractLocaleDependantFieldsImpl localeDependantFields;

	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of error happens
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		stubGetBean(ContextIdNames.UTILITY, new UtilityImpl());
		
		this.localeDependantFields = new AbstractLocaleDependantFieldsImpl() {
			private static final long serialVersionUID = 8573857602031136445L;

			public long getUidPk() {
				// Not Testing
				return 0;
			}

			public void setUidPk(final long uidPk) {
				// Not Testing
			}
		};
	}

	/**
	 * Test that setUrl converts to lowercase using the locale that's set.
	 */
	@Test
	public void testSetUrl() {
		AbstractLocaleDependantFieldsImpl aldf = new AbstractLocaleDependantFieldsImpl() {
			private static final long serialVersionUID = 3877467599445017179L;

			public long getUidPk() {
				// not testing
				return 0;
			}

			public void setUidPk(final long uidPk) {
				//not testing
			}
			
			public Locale getLocale() {
				return Locale.CANADA;
			}
		};
		final String url = "TestUrl";
		aldf.setUrl(url);
		assertEquals(url.toLowerCase(Locale.CANADA), aldf.getUrl());
	}
	
	/**
	 * Test that setUrl works with a null value.
	 */
	@Test
	public void testSetUrlNull() {
		AbstractLocaleDependantFieldsImpl aldf = new AbstractLocaleDependantFieldsImpl() {
			private static final long serialVersionUID = 4020829479614729780L;

			public long getUidPk() {
				// not testing
				return 0;
			}

			public void setUidPk(final long uidPk) {
				//not testing
			}
			
			public Locale getLocale() {
				return Locale.CANADA;
			}
		};
		aldf.setUrl(null);
		assertEquals(null, aldf.getUrl());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.LocaleDependantFieldsImpl.setLocale(Locale)'.
	 */
	@Test
	public void testSetLocale() {
		final Locale locale = Locale.CANADA;
		localeDependantFields.setLocale(locale);
		
		assertEquals(locale, localeDependantFields.getLocale());
		assertSame(locale, localeDependantFields.getLocale());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.LocaleDependantFieldsImpl.setKeyWords(String)'.
	 */
	@Test
	public void testSetKeyWords() {
		final String keyWords = "test key words";
		localeDependantFields.setKeyWords(keyWords);
		assertSame(keyWords, localeDependantFields.getKeyWords());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.LocaleDependantFieldsImpl.setDescription(String)'.
	 */
	@Test
	public void testSetDescription() {
		final String desc = "test desc";
		localeDependantFields.setDescription(desc);
		assertSame(desc, localeDependantFields.getDescription());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.LocaleDependantFieldsImpl.setTitle(String)'.
	 */
	@Test
	public void testSetTitle() {
		final String title = "test title";
		localeDependantFields.setTitle(title);
		assertSame(title, localeDependantFields.getTitle());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.LocaleDependantFieldsImpl.setDisplayName(String)'.
	 */
	@Test
	public void testSetDisplayName() {
		final String displayName = "test title";
		localeDependantFields.setDisplayName(displayName);
		assertSame(displayName, localeDependantFields.getDisplayName());
	}
}
