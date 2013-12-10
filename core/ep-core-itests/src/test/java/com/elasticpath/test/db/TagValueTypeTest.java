package com.elasticpath.test.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.tags.service.TagValueTypeService;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.validation.domain.ValidationConstraint;

/**
 * Integration test suite for tag group entity and service. 
 */
public class TagValueTypeTest extends DbTestCase {

	private static final String GENDER_TYPE_GUID = "gender";

	@Autowired
	private TagValueTypeService tagValueTypeService;

	/**
	 * Test for fetching validation constraints.
	 */
	@DirtiesDatabase
	@Test
	public void testFetchConstraintsAndLocalization() {
				TagValueType tagValType = tagValueTypeService.findByGuid(GENDER_TYPE_GUID);
				Set<ValidationConstraint> valConstraints = tagValType.getValidationConstraints();
				assertNotNull(valConstraints);
				assertFalse(valConstraints.isEmpty());
				ValidationConstraint constraint = valConstraints.iterator().next();
		assertEquals("Localized message was not retrieved",
				"Please provide a valid gender", 
						constraint.getLocalizedErrorMessage(Locale.ENGLISH));
		assertEquals("Missing locale was not handled properly",
				constraint.getLocalizedErrorMessage(Locale.GERMAN), 
				constraint.getErrorMessageKey());
		assertEquals("Null locale was not handled properly",
				constraint.getLocalizedErrorMessage(null),
						constraint.getErrorMessageKey());
	}
}