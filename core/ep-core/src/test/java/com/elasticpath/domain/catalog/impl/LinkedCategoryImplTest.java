/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Stack;

import com.elasticpath.domain.localization.LocaleFallbackPolicy;
import org.jmock.Expectations;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.impl.AttributeGroupAttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test <code>LinkedCategoryImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class LinkedCategoryImplTest extends AbstractEPTestCase {

	private Category parentImpl;

	private Category masterCategoryImpl;

	private Category linkedCategoryImpl;

	private Category childImpl;

	private CategoryType categoryType;

	private CatalogImpl masterCatalog;


	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of error happens
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		setupCategoryType();

		setupParentCategory();

		setupCategory();

		setupLinkedCategory();

		setupChildCategory();

	}

	private void setupChildCategory() {
		this.childImpl = getCategory();
	}

	private void setupCategory() {
		this.masterCategoryImpl = getCategory();
	}

	private void setupLinkedCategory() {
		linkedCategoryImpl = new LinkedCategoryImpl();
		linkedCategoryImpl.setMasterCategory(masterCategoryImpl);
		linkedCategoryImpl.initialize();
		linkedCategoryImpl.setCatalog(getCatalog());
	}

	private void setupParentCategory() {
		this.parentImpl = getCategory();
	}

	private void setupCategoryType() {
		this.categoryType = new CategoryTypeImpl();
		this.categoryType.setAttributeGroup(new AttributeGroupImpl());

		final AttributeGroupAttribute caAttr1 = new AttributeGroupAttributeImpl();
		final Attribute attr1 = new AttributeImpl();
		attr1.setAttributeType(AttributeType.INTEGER);
		caAttr1.setAttribute(attr1);
		categoryType.getAttributeGroup().addAttributeGroupAttribute(caAttr1);

		final AttributeGroupAttribute caAttr2 = new AttributeGroupAttributeImpl();
		final Attribute attr2 = new AttributeImpl();
		attr2.setAttributeType(AttributeType.SHORT_TEXT);
		caAttr2.setAttribute(attr2);
		categoryType.getAttributeGroup().addAttributeGroupAttribute(caAttr2);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.LinkedCategoryImpl.getChildren()'.
	 */
	@Test
	public void testGetChildren() {
		assertNotNull(linkedCategoryImpl.getChildren());
		assertEquals(0, linkedCategoryImpl.getChildren().size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.addChild()'.
	 */
	@Test
	public void testAddChild() {
		linkedCategoryImpl.addChild(childImpl);
		assertSame(linkedCategoryImpl, childImpl.getParent());
		assertTrue(linkedCategoryImpl.getChildren().contains(childImpl));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.addChild()'.
	 */
	@Test
	public void testAddChildAgain() {
		linkedCategoryImpl.addChild(childImpl);
		final int size1 = linkedCategoryImpl.getChildren().size();

		linkedCategoryImpl.addChild(childImpl);
		final int size2 = linkedCategoryImpl.getChildren().size();

		assertEquals(size1, size2);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.addChild()'.
	 */
	@Test
	public void testAddChildWithNull() {
		final int size1 = linkedCategoryImpl.getChildren().size();
		// add null should do nothing
		linkedCategoryImpl.addChild(null);

		final int size2 = linkedCategoryImpl.getChildren().size();
		assertEquals(size1, size2);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.addChild()'.
	 */
	@Test
	public void testAddChildWithLoop() {
		linkedCategoryImpl.setParent(parentImpl);
		childImpl.setParent(linkedCategoryImpl);
		childImpl.addChild(parentImpl);
		// Setting category tree to a loop is supposed to be an exceptional
		// case.
		// However, the catalog manager donot allow adding an existing category
		// to the children list.
		// So this case won't happen through the catalog manager.
		// The import manager might need to add an existing category to the
		// children list.
		// Loop-check might be implemented in either the import manager or the
		// category domain later.
		assertSame(childImpl, parentImpl.getParent());
		assertSame(parentImpl, linkedCategoryImpl.getParent());
		assertSame(linkedCategoryImpl, childImpl.getParent());

	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.removeChild()'.
	 */
	@Test
	public void testRemoveChild() {
		linkedCategoryImpl.addChild(childImpl);
		assertSame(linkedCategoryImpl, childImpl.getParent());
		assertTrue(linkedCategoryImpl.getChildren().contains(childImpl));

		linkedCategoryImpl.removeChild(childImpl);
		assertNull(childImpl.getParent());
		assertFalse(linkedCategoryImpl.getChildren().contains(childImpl));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.removeChild()'.
	 */
	@Test
	public void testRemoveChildAgain() {
		linkedCategoryImpl.addChild(childImpl);

		linkedCategoryImpl.removeChild(childImpl);
		final int size1 = linkedCategoryImpl.getChildren().size();

		linkedCategoryImpl.removeChild(childImpl);
		final int size2 = linkedCategoryImpl.getChildren().size();

		assertEquals(size1, size2);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.removeChild()'.
	 */
	@Test
	public void testRemoveChildWithNull() {
		final int size1 = linkedCategoryImpl.getChildren().size();
		// remove null should do nothing
		linkedCategoryImpl.removeChild(null);

		final int size2 = linkedCategoryImpl.getChildren().size();
		assertEquals(size1, size2);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getStartDate()'.
	 */
	@Test
	public void testGetStartDate() {
		assertNotNull(linkedCategoryImpl.getStartDate());
		assertSame(masterCategoryImpl.getStartDate(), linkedCategoryImpl.getStartDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.setStartDate(Date)'.
	 */
	@Test
	public void testSetStartDate() {
		final Date date = new Date();

		try {
			linkedCategoryImpl.setStartDate(date);
		} catch (EpUnsupportedOperationException e) {
			// expecting a EpUnsupportedOperationException
			assertNotNull(e);
		}

		masterCategoryImpl.setStartDate(date);
		assertSame(date, linkedCategoryImpl.getStartDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getEndDate()'.
	 */
	@Test
	public void testGetEndDate() {
		assertNull(linkedCategoryImpl.getEndDate());
		assertSame(masterCategoryImpl.getEndDate(), linkedCategoryImpl.getEndDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.setEndDate(Date)'.
	 */
	@Test
	public void testSetEndDate() {
		final Date date = new Date();

		try {
			linkedCategoryImpl.setEndDate(date);
		} catch (EpUnsupportedOperationException e) {
			// expecting a EpUnsupportedOperationException
			assertNotNull(e);
		}

		masterCategoryImpl.setEndDate(date);
		assertSame(date, linkedCategoryImpl.getEndDate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getOrdering()'.
	 */
	@Test
	public void testGetOrdering() {
		assertEquals(0, linkedCategoryImpl.getOrdering());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.setOrdering(int)'.
	 */
	@Test
	public void testSetOrdering() {
		final int ordering = 999;
		linkedCategoryImpl.setOrdering(ordering);
		assertEquals(ordering, linkedCategoryImpl.getOrdering());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getTemplate()'.
	 */
	@Test
	public void testGetTemplate() {
		final CategoryType categoryType = new CategoryTypeImpl();
		final String template = "template";
		categoryType.setTemplate(template);
		masterCategoryImpl.setCategoryType(categoryType);
		assertSame(template, masterCategoryImpl.getTemplate());
		assertSame(masterCategoryImpl.getTemplate(), linkedCategoryImpl.getTemplate());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getTemplateName()'.
	 */
	@Test
	public void testGetTemplateName() {
		final CategoryType categoryType = new CategoryTypeImpl();
		masterCategoryImpl.setCategoryType(categoryType);

		// Fall back to default template name if the template is not set
		final String templateName = "templateName";
		final String defaultTemplateName = "default";
		assertEquals(defaultTemplateName, masterCategoryImpl.getTemplateWithFallBack(defaultTemplateName));
		assertEquals(masterCategoryImpl.getTemplateWithFallBack(defaultTemplateName), linkedCategoryImpl
				.getTemplateWithFallBack(defaultTemplateName));

		// Use the template name if it is set.
		categoryType.setTemplate(templateName);
		assertEquals(templateName, masterCategoryImpl.getTemplateWithFallBack(defaultTemplateName));
		assertEquals(masterCategoryImpl.getTemplateWithFallBack(defaultTemplateName), linkedCategoryImpl
				.getTemplateWithFallBack(defaultTemplateName));
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getLevel()'.
	 */
	@Test
	public void testGetLevel() {
		final int level1 = 1;
		final int level2 = 2;
		final int level3 = 3;

		linkedCategoryImpl.setParent(parentImpl);
		childImpl.setParent(linkedCategoryImpl);

		assertEquals(level3, childImpl.getLevel());
		assertEquals(level2, linkedCategoryImpl.getLevel());
		assertEquals(level1, parentImpl.getLevel());

		// change parent, and the level is supposed to change accordingly.
		parentImpl.addChild(childImpl);
		assertEquals(level2, childImpl.getLevel());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getPath()'.
	 */
	@Test
	public void testGetPath() {
		linkedCategoryImpl.setParent(parentImpl);
		childImpl.setParent(linkedCategoryImpl);

		Stack<Category> path = childImpl.getPath();
		assertSame(parentImpl, path.pop());
		assertSame(linkedCategoryImpl, path.pop());
		assertSame(childImpl, path.pop());

		List<Category> pathAsList = childImpl.getPathAsList();
		assertSame(parentImpl, pathAsList.get(0));
		assertSame(linkedCategoryImpl, pathAsList.get(1));
		assertSame(childImpl, pathAsList.get(2));

		// Get the same stack again to check it's still good
		path = childImpl.getPath();
		assertSame(parentImpl, path.pop());
		assertSame(linkedCategoryImpl, path.pop());
		assertSame(childImpl, path.pop());

		// change parent, and the stack is supposed to change accordingly.
		parentImpl.addChild(childImpl);
		path = childImpl.getPath();
		assertSame(parentImpl, path.pop());
		assertSame(childImpl, path.pop());
	}

	/**
	 * Test for isAvailable().
	 */
	@Test
	public void testIsAvaliable() {
		final long timeUnit = 20000;
		Date beforeNow = new Date();
		beforeNow.setTime(beforeNow.getTime() - timeUnit);

		Date muchBeforeNow = new Date();
		beforeNow.setTime(beforeNow.getTime() - timeUnit * 2);

		Date afterNow = new Date();
		afterNow.setTime(afterNow.getTime() + timeUnit);

		masterCategoryImpl.setStartDate(beforeNow);
		assertTrue(masterCategoryImpl.isAvailable());
		assertSame(masterCategoryImpl.isAvailable(), linkedCategoryImpl.isAvailable());

		masterCategoryImpl.setStartDate(afterNow);
		assertFalse(masterCategoryImpl.isAvailable());
		assertSame(masterCategoryImpl.isAvailable(), linkedCategoryImpl.isAvailable());

		masterCategoryImpl.setStartDate(beforeNow);
		masterCategoryImpl.setEndDate(afterNow);
		assertTrue(masterCategoryImpl.isAvailable());
		assertSame(masterCategoryImpl.isAvailable(), linkedCategoryImpl.isAvailable());

		masterCategoryImpl.setStartDate(muchBeforeNow);
		masterCategoryImpl.setEndDate(beforeNow);
		assertFalse(masterCategoryImpl.isAvailable());
		assertSame(masterCategoryImpl.isAvailable(), linkedCategoryImpl.isAvailable());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getAvailableChildren()'.
	 */
	@Test
	public void testAvailableChildren() {
		final long timeUnit = 20000;
		Date afterNow = new Date();
		afterNow.setTime(afterNow.getTime() + timeUnit);

		childImpl.setStartDate(afterNow);
		linkedCategoryImpl.addChild(childImpl);
		assertTrue(linkedCategoryImpl.getChildren().contains(childImpl));
		assertFalse(linkedCategoryImpl.getAvailableChildren().contains(childImpl));
	}

	/**
		 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.setDefaultValues()'.
		 */
		@Test
		public void testInitialize() {
	
			final Date oldDate = linkedCategoryImpl.getStartDate();
			final String oldGuid = linkedCategoryImpl.getGuid();
	
			// set default values again, no value should be changed.
			final String newGuid = "AAAAAAAAABBBB";
			stubGetBean(ContextIdNames.RANDOM_GUID, newGuid);
	
			linkedCategoryImpl.initialize();
			assertEquals(oldGuid, linkedCategoryImpl.getGuid());
			assertSame(oldDate, linkedCategoryImpl.getStartDate());
		}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getFullAttributeValues()'.
	 */
	@Test
	public void testGetFullAttributeValues() {
		this.masterCategoryImpl.setCategoryType(this.categoryType);
		assertSame(this.categoryType, this.masterCategoryImpl.getCategoryType());
		assertSame(this.masterCategoryImpl.getCategoryType(), this.linkedCategoryImpl.getCategoryType());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getLocaleDependantFields(LocaleFallbackPolicy)'.
	 */
	@Test
	public void testGetLocaleDependantFieldsUsingLocaleFallBackPolicy() {
		final LocaleFallbackPolicy policy = context.mock(LocaleFallbackPolicy.class);
		final Category masterCategory = context.mock(Category.class);
		linkedCategoryImpl.setMasterCategory(masterCategory);
		context.checking(new Expectations() {
			{
				oneOf(masterCategory).getLocaleDependantFields(policy);
				ignoring(policy);
			}
		});
		this.linkedCategoryImpl.getLocaleDependantFields(policy);
	}

	/**
	 * Test method for {@link LinkedCategoryImpl#setCode(String)}.
	 */
	@Test
	public void testGetSetCode() {
		assertNotNull(masterCategoryImpl.getCode());
		assertSame(masterCategoryImpl.getCode(), linkedCategoryImpl.getCode());
		assertEquals(linkedCategoryImpl.getCode(), linkedCategoryImpl.getGuid());
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.CategoryImpl.getLastModifiedDate()'.
	 */
	@Test
	public void testGetLastModifiedDate() {
		assertNull(linkedCategoryImpl.getLastModifiedDate());
	}
	
	/**
	 * Test method for {@link LinkedCategoryImpl#equals(Object)}.
	 */
	@Test
	public void testEquals() {
		final LinkedCategoryImpl first = new LinkedCategoryImpl();
		final LinkedCategoryImpl second = new LinkedCategoryImpl();
		final LinkedCategoryImpl third = new LinkedCategoryImpl();
		final Category masterCategory = new CategoryImpl();
		
		assertEquals("Should reflexive", first, first);
		assertEquals("New objects should be equal", first, second);
		assertEquals("Should be symmetric", second, first);
		
		first.setMasterCategory(masterCategory);
		second.setMasterCategory(masterCategory);
		assertEquals("Equal codes should be equal", first, second);
		assertEquals("Equal codes should be symmetric", second, first);

		third.setMasterCategory(masterCategory);
		assertEquals("Equal codes should be equal", second, third);
		assertEquals("Should be transitive", first, third);
		
		first.setMasterCategory(null);
		first.setUidPk(1L);
		second.setMasterCategory(null);
		second.setUidPk(2L);
		assertEquals("Equals should not be dependant on the UID", first, second);
		
		second.setUidPk(1L);
		assertEquals("Same UIDs with no code should be equal", first, second);
		
		first.setCatalog(getCatalog());
		assertFalse("Different catalogs should not be equal", first.equals(second));
		
		second.setCatalog(getCatalog());
		assertEquals("Same catalog should be equal", first, second);
	}
	
	/**
	 * Test method for {@link LinkedCategoryImpl#hashCode()}.
	 */
	@Test
	public void testHashCode() {
		final LinkedCategoryImpl first = new LinkedCategoryImpl();
		final LinkedCategoryImpl second = new LinkedCategoryImpl();
		final Category masterCategory = new CategoryImpl();
		
		assertEquals("Hash code should be same on different calls", first.hashCode(), first.hashCode());
		
		first.setMasterCategory(masterCategory);
		second.setMasterCategory(masterCategory);
		assertEquals("Same code should hash to same value", first.hashCode(), second.hashCode());
		
		first.setUidPk(1L);
		second.setUidPk(1L);
		
		if (first.equals(second)) {
			assertEquals("Equal objects should have same hash code", first.hashCode(), second.hashCode());
		}
		
		first.setMasterCategory(null);
		second.setMasterCategory(null);
		assertEquals("No codes with same UIDs should be the same", first.hashCode(), second.hashCode());
		
		first.setCatalog(getCatalog());
		second.setCatalog(getCatalog());
		assertEquals("Different catalogs should have different hash codes", first.hashCode(), second.hashCode());
	}
	
	/**
	 * @return a new <code>Category</code> instance.
	 */
	protected Category getCategory() {
		final Category category = new CategoryImpl();
		category.initialize();
		category.setCode((new RandomGuidImpl()).toString());
		category.setCatalog(getCatalog());

		return category;
	}

	/**
	 * @return the master catalog singleton
	 */
	protected Catalog getCatalog() {
		if (masterCatalog == null) {
			masterCatalog = new CatalogImpl();
			masterCatalog.setMaster(true);
			masterCatalog.setCode("irrelevant catalog code");
		}
		return masterCatalog;
	}
}
