package com.elasticpath.sfweb.view.helpers;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.MoneyFormatter;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.sfweb.test.BeanFactoryExpectationsFactory;

/**
 * Tests the MoneyFormatterProxy class.
 */
@RunWith(JMock.class)
@SuppressWarnings("PMD.NonStaticInitializer")
public class MoneyFormatterProxyTest {
	private final Money money = MoneyFactory.createMoney(new BigDecimal(100), Currency.getInstance("CAD"));
	private final Locale locale = Locale.CANADA;

	private final JUnit4Mockery context = new JUnit4Mockery();
	private MoneyFormatter formatter;
	private MoneyFormatterProxy proxy;
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory bfef;

	/**
	 * Set up fields.
	 */
	@Before
	public void setUp() {
		formatter = context.mock(MoneyFormatter.class);

		beanFactory = context.mock(BeanFactory.class);
		bfef = new BeanFactoryExpectationsFactory(context, beanFactory);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.MONEY_FORMATTER, formatter);

		proxy = new MoneyFormatterProxy();
	}

	/**
	 * Clean up the mess.
	 */
	@After
	public void tearDown() {
		bfef.close();
	}

	/**
	 * Tests that the proxy formatter proxies to the base formatter defined in the spring context.
	 */
	@Test
	public void testProxyMethods() {
		// CHECKSTYLE:OFF   Magic Numbers should be fine in unit tests
		context.checking(new Expectations() { {
			oneOf(formatter).format(money, true, locale); will(returnValue("FORMAT"));
			oneOf(formatter).formatAmount(money, locale); will(returnValue("FORMAT_AMOUNT"));
			oneOf(formatter).formatCurrency(money, locale); will(returnValue("FORMAT_CURRENCY"));
			oneOf(formatter).formatCurrencySymbol(money.getCurrency()); will(returnValue("FORMAT_CURRENCY_SYMBOL"));
			oneOf(formatter).formatPercentage(100, locale); will(returnValue("FORMAT_PERCENTAGE"));
		} });

		assertEquals("FORMAT", proxy.format(money, true, locale));
		assertEquals("FORMAT_AMOUNT", proxy.formatAmount(money, locale));
		assertEquals("FORMAT_CURRENCY", proxy.formatCurrency(money, locale));
		assertEquals("FORMAT_CURRENCY_SYMBOL", proxy.formatCurrencySymbol(money.getCurrency()));
		assertEquals("FORMAT_PERCENTAGE", proxy.formatPercentage(100, locale));
		// CHECKSTYLE:ON
	}
}
