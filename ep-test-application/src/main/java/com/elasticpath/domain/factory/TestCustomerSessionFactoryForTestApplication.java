package com.elasticpath.domain.factory;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.CustomerSessionMemento;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionMementoImpl;
import com.elasticpath.domain.shopper.Shopper;

/**
 * A factory for producing properly constructed CustomerSessions for use in tests.
 * 
 * The intent is to keep all the CustomerSession creation code in one place so that changes in it's instantiation can
 * be fixed all at once.
 */
public final class TestCustomerSessionFactoryForTestApplication {

	/**
	 * Default private constructor.
	 */
	private TestCustomerSessionFactoryForTestApplication() {
		
	}
	
	 /**
	  * From: http://en.wikipedia.org/wiki/Singleton_pattern#The_solution_of_Bill_Pugh
	  * SingletonHolder is loaded on the first execution of Singleton.getInstance() or the first access to SingletonHolder.INSTANCE, 
	  * not before.
	  */
	private static class SingletonHolder {
		public static final TestCustomerSessionFactoryForTestApplication TEST_CUSTOMER_SESSION_FACTORY = new TestCustomerSessionFactoryForTestApplication(); //NOPMD 
	}
	
	/**
	 * Gets an instance of the factory for use.  (Bill Pugh's singleton.)
	 *
	 * @return TestCustomerSessionFactory.
	 */
	public static TestCustomerSessionFactoryForTestApplication getInstance() {
		return SingletonHolder.TEST_CUSTOMER_SESSION_FACTORY;
	}
	
	/**
	 * Creates a new customer session. 
	 *
	 * @return a new customer session.
	 */
	public CustomerSession createNewCustomerSession() {
		Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();

		return createNewCustomerSessionWithContext(shopper);
	}
	
	/**
	 * Creates a new customer session based on incoming shopping context. 
	 *
	 * @param shopper Shopping Context used by CustomerSession.
	 * @return a new customer session.
	 */
	public CustomerSession createNewCustomerSessionWithContext(final Shopper shopper) {
		final CustomerSession customerSession = new CustomerSessionImpl();
		final CustomerSessionMemento customerSessionMemento = new CustomerSessionMementoImpl();

		customerSession.setCustomerSessionMemento(customerSessionMemento);
		customerSession.setShopper(shopper);
		shopper.updateTransientDataWith(customerSession);
		
		customerSession.setGuid(TestGuidUtility.getGuid());
		
		return customerSession;
	}	
}
