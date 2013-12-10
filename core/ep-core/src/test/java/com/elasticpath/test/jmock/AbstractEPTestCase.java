package com.elasticpath.test.jmock;

import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Subclass this for an ElasticPath instance with
 * stubbed out <code>getBean()</code> calls, it also subs out those calls on a
 * <code>BeanFactory</code> to help migrate tests and classes away from using ElasticPath.
 *
 * The stubbed beans have <code>setDefaultValues</code> called on them.
 *
 * Note: by default RandomGuid bean is stubbed to return RandomGuidImpl as this is used across
 * the whole system.
 * @deprecated use directly BeanFactoryExpectationsFactory instead
 */
@Deprecated
public class AbstractEPTestCase {

	//CHECKSTYLE:OFF
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	//CHECKSTYLE:ON

	@Mock
	private BeanFactory beanFactory;

	private BeanFactoryExpectationsFactory bfef;

	/**
	 * Sets up the mock persistence engine with a single stub for "randomGuid".
	 * Subclasses must call this to have their ElasticPath instance registered.
	 *
	 * @throws Exception if something goes wrong during set up.
	 */
	@Before
	@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
	public void setUp() throws Exception {
		bfef = new BeanFactoryExpectationsFactory(context, beanFactory);
		stubGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);
	}

	/**
	 * Cleans BeanFactoryExpectationsFactory between tests.
	 */
	@After
	public void tearDown() {
		bfef.close();
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * Allow calls to <code>getBean</code> and <code>getBeanImplClass</code> on ElaticPath for
	 * a particular bean name, creating a new instance of the bean.
	 *
	 * @param beanName the bean name to allow getBean for.
	 * @param beanClass the class of the bean to create.
	 */
	protected void stubGetBean(final String beanName, final Class<?> beanClass) {
		bfef.allowingBeanFactoryGetBean(beanName, beanClass);
	}

	/**
	 * Allow calls to <code>getBean</code> on ElasticPath for a particular bean name, returning
	 * the specific object.
	 *
	 * @param beanName the bean name to allow getBean for.
	 * @param object the object to have returned.
	 */
	protected void stubGetBean(final String beanName, final Object object) {
		bfef.allowingBeanFactoryGetBean(beanName, object);
	}

}
