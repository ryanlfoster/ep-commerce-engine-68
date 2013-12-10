package com.elasticpath.test.integration;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.test.integration.junit.DatabaseHandlingTestExecutionListener;
import com.elasticpath.test.persister.TestApplicationContext;

/**
 * Basic Spring Context Test.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integration-context.xml")
@SuppressWarnings("PMD.AbstractNaming")
@TestExecutionListeners({
		DatabaseHandlingTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class
})
public abstract class BasicSpringContextTest {

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private TestApplicationContext tac;
	
	/**
	 * @return the tac
	 */
	protected TestApplicationContext getTac() {
		return tac;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
