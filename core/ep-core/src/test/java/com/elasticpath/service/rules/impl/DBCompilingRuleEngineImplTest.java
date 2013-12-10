package com.elasticpath.service.rules.impl;

import static org.junit.Assert.assertNull;

import java.io.FileNotFoundException;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.PropertiesDao;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.rules.RulesPackageCompilationException;

/**
 * Test case for {@link DBCompilingRuleEngineImpl}.
 */
public class DBCompilingRuleEngineImplTest {

	private DBCompilingRuleEngineImpl ruleEngine;

	private PropertiesDao mockPropertiesDao;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
		ruleEngine = new DBCompilingRuleEngineImpl();

		mockPropertiesDao = context.mock(PropertiesDao.class);
		ruleEngine.setPropertiesDao(mockPropertiesDao);
	}

	/**
	 * Test that an exception is not thrown when the properties file is not found.
	 */
	@Test
	public void testPropertiesFileNotFound() {
		final EpPersistenceException exception = new EpPersistenceException(null, new FileNotFoundException());
		context.checking(new Expectations() {
			{
				allowing(mockPropertiesDao).getPropertiesFile(with(any(String.class)));
				will(throwException(exception));
			}
		});
		assertNull("Should not retrieve a date from a missing property file", ruleEngine.getLastSuccessfulCompilationBeginDate());
	}
	
	/**
	 * Tests that other exceptions are thrown as they should.
	 */
	@Test(expected = EpPersistenceException.class)
	public void testPropertiesFileIOException() {
		context.checking(new Expectations() {
			{
				allowing(mockPropertiesDao).getPropertiesFile(with(any(String.class)));
				will(throwException(new EpPersistenceException(null)));
			}
		});
		ruleEngine.getLastSuccessfulCompilationBeginDate();
	}
	
	/**
	 * Test that an exception is thrown when the package is null and has errors.
	 */
	@Test(expected = RulesPackageCompilationException.class)
	public void testPackageNotBuiltDueToCompileErrors() {
		final RuleSet ruleset = context.mock(RuleSet.class);
		final Store store = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				allowing(ruleset).getRuleCode(store); will(returnValue("bad rule code"));
			}
		});
		ruleEngine.recompileRuleBase(ruleset, store);
	}
}
