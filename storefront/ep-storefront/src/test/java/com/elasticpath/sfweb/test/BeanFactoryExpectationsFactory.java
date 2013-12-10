package com.elasticpath.sfweb.test;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.impl.ElasticPathImpl;

/**
 * Common expectations used throughout tests.
 *
 * Each test should have its own BeanFactoryExpectationsFactory.
 * Use @Before to ensure this, but remember to call close() within @After, otherwise beans might leak between tests.
 */

public class BeanFactoryExpectationsFactory {

    private final Mockery mockery;

    private final BeanFactory beanFactory;

    /**
     * Create a new ExpectationsFactory.
     * @param mockery the Mockery to use.
     * @param beanFactory TODO
     */
    @SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
    public BeanFactoryExpectationsFactory(final Mockery mockery, final BeanFactory beanFactory) {
        this.mockery = mockery;
        this.beanFactory = beanFactory;
        ElasticPathImpl elasticPath = (ElasticPathImpl) ElasticPathImpl.getInstance();
        elasticPath.setBeanFactory(beanFactory);
    }

    /**
     * Clean up after factory.
     */
    @SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
    public void close() {
        ElasticPathImpl elasticPath = (ElasticPathImpl) ElasticPathImpl.getInstance();
        elasticPath.setBeanFactory(null);
    }

    /**
     * Allow calls to getBean on the BeanFactory for a particular bean name, creating a new instance of the bean.
     * Will also call setElasticPath on the bean if necessary, with an ElasticPath that supports getBean using the same BeanFactory.
     * @param beanName the bean name to allow getBean for
     * @param beanClass the class of the bean to create.
     */
    public void allowingBeanFactoryGetBean(final String beanName, final Class<?> beanClass) {
        mockery.checking(new Expectations() { {
            allowing(beanFactory).getBean(beanName);
            will(new PrototypeBeanCustomStub(beanClass));

            allowing(beanFactory).getBeanImplClass(beanName);
            will(returnValue(beanClass));
        } });
    }

    /**
     * Allow calls to getBean on the BeanFactory for a particular bean name, creating a new instance of the bean.
     * Will also call setElasticPath on the bean if necessary, with an ElasticPath that supports getBean using the same BeanFactory.
     * @param beanName the bean name to allow getBean for
     * @param beanClass the class of the bean to create.
     */
    public void oneBeanFactoryGetBean(final String beanName, final Class<?> beanClass) {
        mockery.checking(new Expectations() { {
            oneOf(beanFactory).getBean(beanName);
            will(new PrototypeBeanCustomStub(beanClass));

            allowing(beanFactory).getBeanImplClass(beanName);
            will(returnValue(beanClass));
        } });
    }

    /**
     * Allow calls to getBean on the BeanFactory for a particular bean name, returning the specified bean.
     * Will also call setElasticPath on the bean if necessary, with an ElasticPath that supports getBean using the same BeanFactory.
     * @param beanName the bean name to allow getBean for
     * @param bean the bean instance to return.
     */
    public void allowingBeanFactoryGetBean(final String beanName, final Object bean) {
        mockery.checking(new Expectations() { {
            allowing(beanFactory).getBean(beanName);
            will(new CustomAction("Return " + beanName) {
                public Object invoke(final Invocation invocation) throws Throwable {
                    return bean;
                }
            });

            allowing(beanFactory).getBeanImplClass(beanName);
            if (bean == null) {
                will(returnValue(null));
            } else {
                will(returnValue(bean.getClass()));
            }
        } });
    }

    /**
     * Allow calls to getBean on the BeanFactory for a particular bean name, returning the specified bean.
     * Will also call setElasticPath on the bean if necessary, with an ElasticPath that supports getBean using the same BeanFactory.
     * @param beanName the bean name to allow getBean for
     * @param bean the bean instance to return.
     */
    public void oneBeanFactoryGetBean(final String beanName, final Object bean) {
        mockery.checking(new Expectations() { {
            oneOf(beanFactory).getBean(beanName);
            will(new CustomAction("Return " + beanName) {
                public Object invoke(final Invocation invocation) throws Throwable {
                    return bean;
                }
            });

            allowing(beanFactory).getBeanImplClass(beanName);
            if (bean == null) {
                will(returnValue(null));
            } else {
                will(returnValue(bean.getClass()));
            }
        } });
    }

}
