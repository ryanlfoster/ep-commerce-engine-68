package com.elasticpath.settings.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.elasticpath.test.integration.junit.DatabaseHandlingTestExecutionListener;

/**
 * <p>Integration test class for {@link com.elasticpath.settings.beanframework.SettingFactoryBean SettingFactoryBean}.</p>
 * <p>This test expects the Spring context to autowire a bean defined in ep-settings-itest-context.xml.  This bean is a
 * {@code String} whose value is loaded from the database by
 * {@link com.elasticpath.settings.beanframework.SettingFactoryBean SettingFactoryBean}.</p>  The setting is defined
 * in a Liquibase changeset within ep-settings-itest-changelog.xml.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ep-settings-itest-context.xml")
@TestExecutionListeners({
		DatabaseHandlingTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class
})
public class SettingFactoryBeanITest {

	// Value defined in insert-testing-settings-values.xml Liquibase change set
	public static final String EXPECTED_VALUE = "Expected value";

	@Autowired
	@Qualifier("settingFactoryBeanTestValue")
	private String injectedSettingValue;

	@Test
	public void testSettingFactoryBeanAutowiring() {
		assertEquals("Unexpected injected value", EXPECTED_VALUE, injectedSettingValue);
	}

}