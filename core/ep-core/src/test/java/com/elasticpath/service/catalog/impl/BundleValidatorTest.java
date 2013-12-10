package com.elasticpath.service.catalog.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuConstituentImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;
import com.elasticpath.domain.subscriptions.impl.PaymentScheduleImpl;
import com.elasticpath.service.catalog.BundleValidator;
import com.elasticpath.service.pricing.impl.PaymentScheduleHelperImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Units tests for the BundleValidator.
 */
public class BundleValidatorTest {

    @Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

    private final SkuOption frequencyOption = context.mock(SkuOption.class);
    private BundleValidator bundleValidator;

    /** . */
    @Before
    public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
		expectationsFactory.allowingBeanFactoryGetBean("productSkuConstituent", ProductSkuConstituentImpl.class);

		setupBundleValidator();
    }

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

    private void setupBundleValidator() {
        bundleValidator = new BundleValidatorImpl();
        PaymentScheduleHelperImpl paymentScheduleHelper = new PaymentScheduleHelperImpl() {

        	@Override
            protected SkuOption getFrequencyOption(final Product product) {
                return frequencyOption;
            }

        };

        paymentScheduleHelper.setBeanFactory(beanFactory);
        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PAYMENT_SCHEDULE, PaymentScheduleImpl.class);

        ((BundleValidatorImpl) bundleValidator).setPaymentScheduleHelper(paymentScheduleHelper);
    }

    private ProductBundle setupProductBundle(final boolean hasRecurringCharge, final boolean isCalculatedBundle) {
        final ProductBundle mainBundle = new ProductBundleImpl();
        mainBundle.setCalculated(isCalculatedBundle);

        final BundleConstituent bundleConstituent = new BundleConstituentImpl();
        final BundleConstituent productConstituent = new BundleConstituentImpl();
        final BundleConstituent productSkuConstituent = new BundleConstituentImpl();

        final BundleConstituent childConstituent = new BundleConstituentImpl();
        final ProductBundle childBundle = new ProductBundleImpl();

        final Product regularProduct = context.mock(Product.class);
        final ProductType regularProductType = context.mock(ProductType.class);
        final ProductSku recurringChargeProductSku = context.mock(ProductSku.class);

        final SkuOptionValue frequencyOptionValue = new SkuOptionValueImpl();
        final Map<String, SkuOptionValue> optionValueMap = new HashMap<String, SkuOptionValue>();

        context.checking(new Expectations() { {
            allowing(regularProduct).getProductType(); will(returnValue(regularProductType));
            allowing(regularProductType).getSkuOptions(); will(returnValue(Collections.emptySet()));

            allowing(recurringChargeProductSku).getProduct();
            allowing(recurringChargeProductSku).getSkuOptionValue(with(frequencyOption)); will(returnValue(frequencyOptionValue));
            allowing(recurringChargeProductSku).getOptionValueMap(); will(returnValue(optionValueMap));
        } });

        bundleConstituent.setConstituent(childBundle);
        childConstituent.setConstituent(regularProduct);
        childBundle.addConstituent(childConstituent);

        productConstituent.setConstituent(regularProduct);
        productSkuConstituent.setConstituent(recurringChargeProductSku);

        mainBundle.addConstituent(bundleConstituent);
        mainBundle.addConstituent(productConstituent);

        if (hasRecurringCharge) {
            mainBundle.addConstituent(productSkuConstituent);
        }

        frequencyOptionValue.setOptionValueKey("Monthly");
        optionValueMap.put("Frequency", frequencyOptionValue);
        return mainBundle;
    }

    /** . */
    @Test
    public void testDoesBundleContainRecurringChargeWithRecurringCharge() {
        final ProductBundle bundle = setupProductBundle(true, false);
        Assert.assertTrue("Recurring charge was not detected in bundle when one existed.",
                bundleValidator.doesAssignedBundleContainRecurringCharge(bundle));
    }

    /** . */
    @Test
    public void testDoesBundleContainRecurringChargeWithoutRecurringCharge() {
        final ProductBundle bundle = setupProductBundle(false, false);
        Assert.assertFalse("Recurring charge was detected in bundle when one did not exist.",
                bundleValidator.doesAssignedBundleContainRecurringCharge(bundle));
    }

    /** . */
    @Test
    public void testDoesAssignedBundleContainRecurringChargeWithCalculatedBundleAndRecurringCharge() {
        final ProductBundle bundle = setupProductBundle(true, true);
        Assert.assertFalse("Calculated Bundle should not be checking for recurring charge.",
                bundleValidator.doesAssignedBundleContainRecurringCharge(bundle));
    }

    /** . */
    @Test
    public void testDoesAssignedBundleContainRecurringChargeWithCalculatedBundleAndNoRecurringCharge() {
        final ProductBundle bundle = setupProductBundle(false, true);
        Assert.assertFalse("Calculated Bundle should not be checking for recurring charge.",
                bundleValidator.doesAssignedBundleContainRecurringCharge(bundle));
    }

    /** . */
    @Test
    public void testDoesAssignedBundleContainRecurringChargeWithAssignedBundleAndRecurringCharge() {
        final ProductBundle bundle = setupProductBundle(true, false);
        Assert.assertTrue("Recurring charge was not detected in assigned bundle when one exists.",
                bundleValidator.doesAssignedBundleContainRecurringCharge(bundle));
    }

    /** . */
    @Test
    public void testDoesAssignedBundleContainRecurringChargeWithAssignedBundleAndNoRecurringCharge() {
        final ProductBundle bundle = setupProductBundle(false, false);
        Assert.assertFalse("Recurring charge was detected in assigned bundle when one did not exist.",
                bundleValidator.doesAssignedBundleContainRecurringCharge(bundle));
    }

    /** . */
    @Test
    public void testIsBundleEmpty() {
        final ProductBundle bundle = setupProductBundle(false, false);
        Assert.assertFalse("Bundle should not be empty when it has constituents.",
                bundleValidator.isBundleEmpty(bundle));
    }

}
