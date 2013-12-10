package com.elasticpath.commons.util.impl;

import java.text.MessageFormat;
import java.util.Locale;

import org.springframework.context.support.AbstractMessageSource;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.StoreMessageSource;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Custom message resource to retrieve properties from property file sets according to store/theme.
 * This class looks up the store from the current thread using a thread-local object, and calls the
 * {@code SettingsReader} to retrieve the store's theme code. Wraps a {@link StoreMessageSource}
 * to retrieve the actual message. 
 */
public class StoreThemeMessageSource extends AbstractMessageSource {
    
    private StoreConfig storeConfig;

    private StoreMessageSource storeMessageSource;
    
    /**
     * Finds properties without arguments.
     * We don't want to fail any rendering just from a missing message, but log them.
     * Spring expects a null to be returned if no message is found and will throw NoMessageException 
     * regardless of what this class does. This can be used in conjunction with useCodeAsDefaultMessage property
     * to avoid exceptions from being thrown from the superclass AbstractMessageSource
     * Calls {@link #getStoreCode()} and {@link #getThemeCode()}.
     * 
     * @param messageCode The property key.
     * @param locale The property locale.
     * @return The property value.
     */
    @Override
    protected String resolveCodeWithoutArguments(final String messageCode, final Locale locale) {
        return getStoreMessageSource().getMessage(getStoreCode(), getThemeCode(), messageCode, locale);
    }

	/**
	 * Retrieve the store's code.
	 * @return the store's code
	 */
	protected String getStoreCode() {
		return storeConfig.getStoreCode();
	}
	
	/**
	 * Retrieve the store's theme code by retrieving the setting from the setting service.
	 * @return the store's theme code
	 */
	protected String getThemeCode() {
		SettingValue settingValue = storeConfig.getSetting("COMMERCE/STORE/theme");
		if (settingValue == null) {
			throw new EpSystemException("StoreConfig missing setting 'COMMERCE/STORE/theme' for store: " + storeConfig.getStoreCode());
		}
		
		String themeCode = settingValue.getValue();
		if (themeCode == null || themeCode.trim().length() == 0) {
			throw new EpSystemException("StoreConfig missing setting 'COMMERCE/STORE/theme' for store: " + storeConfig.getStoreCode());
		}
		return themeCode;
	}

    /**
     * Returns a message.
     * 
     * @param code The property key.
     * @param locale The property locale.
     * @return The property value.
     */
    @Override
    protected MessageFormat resolveCode(final String code, final Locale locale) {
    	String msg = resolveCodeWithoutArguments(code, locale);
    	if (msg == null) {
    		return null;
    	}
    	return new MessageFormat(msg, locale);
    }
    
    /**
     * Sets the store message source.
     * @param storeMessageSource the message source to set
     */
	public void setStoreMessageSource(final StoreMessageSource storeMessageSource) {
		this.storeMessageSource = storeMessageSource;
	}

	/**
	 * @return the store message source.
	 */
	public StoreMessageSource getStoreMessageSource() {
		return storeMessageSource;
	}
	
	/**
     * Set the store config handler.
     * 
     * @param storeConfig The store config.
     */
    public void setStoreConfig(final StoreConfig storeConfig) {
        this.storeConfig = storeConfig;
    }
    
}