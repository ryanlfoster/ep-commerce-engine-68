package com.elasticpath.domain.catalog.impl;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.localization.LocaleFallbackPolicy;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.TopSeller;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;

/**
 * Test case for {@link AbstractCategoryImpl}.
 */
public class AbstractCategoryImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private AbstractCategoryImpl abstractCategoryImpl;
	
	private Category parentImpl;
	
	private Category childImpl;

	private Catalog masterCatalog;

	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception in case of any errors
	 */
	@Before
	public void setUp() throws Exception {
		abstractCategoryImpl = createNewAbstractCategoryImpl();
		parentImpl = getCategory();
		childImpl = getCategory();
	}

	@SuppressWarnings({ "PMD.ExcessiveMethodLength" })
	private AbstractCategoryImpl createNewAbstractCategoryImpl() {
		return new AbstractCategoryImpl() {
			private static final long serialVersionUID = -2685396194326764453L;

			public AttributeValueGroup getAttributeValueGroup() {
				return null;
			}
			public Map<String, AttributeValue> getAttributeValueMap() {
				return null;
			}
			public CategoryType getCategoryType() {
				return null;
			}
			public String getCode() {
				return null;
			}
			public Date getEndDate() {
				return null;
			}
			public Category getMasterCategory() {
				return null;
			}
			public Date getStartDate() {
				return null;
			}
			public String getTemplate() {
				return null;
			}
			public String getTemplateWithFallBack(final String defaultTemplate) {
				return null;
			}
			public Set<TopSeller> getTopSellers() {
				return null;
			}
			public boolean isAvailable() {
				return false;
			}
			public boolean isHidden() {
				return false;
			}
			public boolean isIncluded() {
				return false;
			}
			public boolean isLinked() {
				return false;
			}
			public boolean isVirtual() {
				return false;
			}
			public void setAttributeValueGroup(final AttributeValueGroup attributeValueGroup) {
				// stub method
			}
			public void setAttributeValueMap(final Map<String, AttributeValue> attributeValueMap) {
				// stub method
			}
			public void setCategoryType(final CategoryType categoryType) {
				// stub method
			}
			public void setCode(final String code) {
				// stub method
			}
			public void setEndDate(final Date endDate) {
				// stub method
			}
			public void setHidden(final boolean hidden) {
				// stub method
			}
			public void setIncluded(final boolean include) {
				// stub method
			}
			public void setMasterCategory(final Category masterCategory) {
				// stub method
			}
			public void setStartDate(final Date startDate) {
				// stub method
			}
			public void setTopSellers(final Set<TopSeller> topSellers) {
				// stub method
			}
			public void setVirtual(final boolean virtual) {
				// stub method
			}
			public void addOrUpdateLocaleDependantFields(final LocaleDependantFields ldf) {
				// stub method
			}
			public String getDisplayName(final Locale locale) {
				return null;
			}
			public LocaleDependantFields getLocaleDependantFields(final Locale locale) {
				return null;
			}
			public LocaleDependantFields getLocaleDependantFieldsWithoutFallBack(final Locale locale) {
				return null;
			}
			public void setDisplayName(final String name, final Locale locale) {
				// stub method
			}
			@Override
			public LocaleDependantFields getLocaleDependantFields(final LocaleFallbackPolicy policy) {
				return null;
			}
			public void setLocaleDependantFieldsMap(final Map<Locale, LocaleDependantFields> localeDependantFieldsMap) {
				// stub method
			}
			@Override
			public String getGuid() {
				return null;
			}
			@Override
			public void setGuid(final String guid) {
				// stub method
			}
		};
	}
	
	/**
	 * Test method for {@link AbstractCategoryImpl#addChild(Category)} where the category to be added is in the wrong catalog.
	 */
	@Test(expected = EpDomainException.class)
	public void testAddChildInWrongCatalog() {
		final Catalog mockCatalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				allowing(mockCatalog).getCode();
				will(returnValue("a code"));
			}
		});
		abstractCategoryImpl.setCatalog(mockCatalog);
		
		final Category mockChildCategory = context.mock(Category.class);
		final Catalog mockChildCatalog = context.mock(Catalog.class, "child catalog");
		context.checking(new Expectations() {
			{
				allowing(mockChildCatalog).getCode();
				will(returnValue("another different code"));

				allowing(mockChildCategory).getCatalog();
				will(returnValue(mockChildCatalog));

				allowing(mockChildCategory).getParent();
				allowing(mockChildCategory).setParent(with(any(Category.class)));
			}
		});

		abstractCategoryImpl.addChild(mockChildCategory);
	}
	
	/**
	 * Test method for {@link AbstractCategoryImpl#getParent()}.
	 */
	@Test
	public void testGetParent() {
		assertNull(abstractCategoryImpl.getParent());
	}

	/**
	 * Test method for {@link AbstractCategoryImpl#setParent(Category)}.
	 */
	@Test
	public void testSetParent() {
		abstractCategoryImpl.setCatalog(getCatalog());
		abstractCategoryImpl.setParent(parentImpl);
		assertSame(parentImpl, abstractCategoryImpl.getParent());
		assertTrue(parentImpl.getChildren().contains(abstractCategoryImpl));

		final Category newParent = new CategoryImpl();
		newParent.setCatalog(getCatalog());
		newParent.initialize();
		abstractCategoryImpl.setParent(newParent);
		assertSame(newParent, abstractCategoryImpl.getParent());
		assertTrue(newParent.getChildren().contains(abstractCategoryImpl));
		assertFalse(parentImpl.getChildren().contains(abstractCategoryImpl));
	}

	/**
	 * Test method for {@link AbstractCategoryImpl#setParent(Category)}.
	 */
	@Test
	public void testSetParentWithNull() {
		abstractCategoryImpl.setCatalog(getCatalog());
		abstractCategoryImpl.setParent(parentImpl);
		assertSame(parentImpl, abstractCategoryImpl.getParent());
		assertTrue(parentImpl.getChildren().contains(abstractCategoryImpl));

		abstractCategoryImpl.setParent(null);
		assertNull(abstractCategoryImpl.getParent());
		assertFalse(parentImpl.getChildren().contains(abstractCategoryImpl));
	}

	/**
	 * Test method for {@link AbstractCategoryImpl#setParent(Category)}.
	 */
	@Test
	public void testSetParentWithLoop() {
		abstractCategoryImpl.setCatalog(getCatalog());
		abstractCategoryImpl.setParent(parentImpl);
		childImpl.setParent(abstractCategoryImpl);
		parentImpl.setParent(childImpl);
		// Setting category tree to a loop is supposed to be an exceptional
		// case.
		// However, the catalog manager donot allow change parent of an existing
		// category.
		// So this case won't happen through the catalog manager.
		// The import manager might need to set parent of a category.
		// Loop-check might be implemented in either the import manager or the
		// category domain later.

		assertSame(childImpl, parentImpl.getParent());
		assertSame(parentImpl, abstractCategoryImpl.getParent());
		assertSame(abstractCategoryImpl, childImpl.getParent());
		// try {
		// parentImpl.setParent(childImpl);
		// fail(DOMAIN_EXCEPTION_EXPECTED);
		// } catch (final EpDomainException e) {
		// //succeed
		// assertNotNull(e);
		// }
	}
	
	/**
	 * Returns a new <code>Category</code> instance.
	 * 
	 * @return a new <code>Category</code> instance.
	 */
	private Category getCategory() {
		final Category category = new CategoryImpl();
		category.initialize();
		category.setCode((new RandomGuidImpl()).toString());
		category.setCatalog(getCatalog());

		return category;
	}
	
	/**
	 * Gets the master catalog singleton.
	 *
	 * @return the master catalog singleton
	 */
	private Catalog getCatalog() {
		if (masterCatalog == null) {
			masterCatalog = new CatalogImpl();
			masterCatalog.setMaster(true);
			masterCatalog.setCode("Irrelevant catalog code");
		}
		
		return masterCatalog;
	}
}
