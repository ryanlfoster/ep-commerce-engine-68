package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;

/**
 * Tests {@link SkuConfiguration}.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class SkuConfigurationTest {
    private static final String COLOR_GREEN = "green";
    private static final String COLOR_RED = "red";
    private static final String SIZE_LARGE = "large";
    private static final String STYLE_STRIPED = "striped";
    private static final String COLOR = "color";
    private static final String SIZE = "size";
    private static final String STYLE = "style";
    
    private static final String SKU_GUID = "sku-guid";
    private static final long SKU_UID = 1234L;

    private SkuOption size;
    private SkuOptionValue sizeLarge;

    private SkuOption color;
    private SkuOptionValue colorGreen;
    private SkuOptionValue colorRed;
    
    private SkuOption style;
    private SkuOptionValue styleStriped;
    
    /**
     * Runs before every test case.
     */
    @Before
    public void setUp() {
        size = createSkuOptionWithValues(SIZE, SIZE_LARGE);
        sizeLarge = size.getOptionValue(SIZE_LARGE);
        
        color = createSkuOptionWithValues(COLOR, COLOR_GREEN, COLOR_RED);
        colorGreen = color.getOptionValue(COLOR_GREEN);
        colorRed = color.getOptionValue(COLOR_RED);
        
        style = createSkuOptionWithValues(STYLE, STYLE_STRIPED);
        styleStriped = style.getOptionValue(STYLE_STRIPED);
    }

    /**
     * Tests reading values from the Sku Configuration.
     */
    @Test
    public void testGetOptions() {
        List<SkuOptionValue> optionsList = Arrays.asList(sizeLarge, colorGreen);
		SkuConfiguration configuration = new SkuConfiguration(SKU_GUID, SKU_UID, optionsList);
        assertEquals("sku guid getter should work", SKU_GUID, configuration.getSkuGuid());
        assertEquals("sku uid getter should work", SKU_UID, configuration.getSkuUid());

        assertSame("should have the color 'green'", colorGreen, configuration.getOptionValueForOptionKey(COLOR));
        assertSame("should have the size 'large'", sizeLarge, configuration.getOptionValueForOptionKey(SIZE));
        assertNull("should not contain 'style'", configuration.getOptionValueForOptionKey(STYLE));
        assertEquals("should have 2 options.", optionsList.size(), configuration.getOptionValues().size());
        assertTrue("options will contain 'size: large', and 'color: green'", configuration.getOptionValues().containsAll(optionsList));
    }

    /**
     * Tests {@link S#isCompatibleWithSelection(java.util.Collection)}.
     */
    @Test
    public void testIsCompatibleWithSelection() {
    	SkuConfiguration configuration = new SkuConfiguration(SKU_GUID, SKU_UID, Arrays.asList(sizeLarge, colorGreen));

        assertTrue("everything is compatible with no selections", configuration.isCompatibleWithSelection(Collections.<SkuOptionValue>emptyList()));
        assertTrue("should be compatible with color 'green'", configuration.isCompatibleWithSelection(Arrays.asList(colorGreen)));
        assertTrue("should be compatible with size 'large'", configuration.isCompatibleWithSelection(Arrays.asList(sizeLarge)));
        assertTrue("should be compatible with color 'green' and size 'large'", 
        		configuration.isCompatibleWithSelection(Arrays.asList(colorGreen, sizeLarge)));

        assertFalse("should not be compatible with style striped, an option that is not part of the sku", 
        		configuration.isCompatibleWithSelection(Arrays.asList(styleStriped)));
        assertFalse("should not be compatible with color green, and style striped, because style is not part of the sku configuration", 
        		configuration.isCompatibleWithSelection(Arrays.asList(colorGreen, styleStriped)));
        assertFalse("it is green, not red!", configuration.isCompatibleWithSelection(Arrays.asList(colorRed)));
        assertFalse("it is green, not red!", configuration.isCompatibleWithSelection(Arrays.asList(sizeLarge, colorRed)));
    }

    private SkuOption createSkuOptionWithValues(final String key, final String... values) {
    	SkuOption skuOption = new SkuOptionImpl();
    	skuOption.setOptionKey(key);
    	for (int i = 0; i < values.length; ++i) {
	    	SkuOptionValue skuOptionValue = new SkuOptionValueImpl();
	    	skuOptionValue.setOptionValueKey(values[i]);
	    	skuOptionValue.setOrdering(i);
	    	skuOptionValue.setSkuOption(skuOption);
	    	skuOption.addOptionValue(skuOptionValue);
	    	
    	}
    	return skuOption;
    }

}
