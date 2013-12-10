package com.elasticpath.sfweb.filters;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;
import org.tuckey.web.filters.urlrewrite.UrlRewriter;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.catalogview.StoreSeoUrlBuilderFactory;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Subclass of URL rewrite filter to customise behaviour in terms of configuration and reloading.
 */
public class StoreUrlRewriteFilter extends UrlRewriteFilter {
	
	private static final Logger LOG = Logger.getLogger(StoreUrlRewriteFilter.class);
	
	private static final int MILLI = 1000;
	
	private static final String SETTING_PREFIX = "setting:";
	
	private SettingsReader settingsReader;
	
	private UrlRewriter urlRewriter;
	
	private ServletContext context;
	
	private String confPath;
	
	private boolean modRewriteStyleConf = false;

	private String confUrlStr = "";
	
	private boolean confReloadInProgress = false;
	
	private boolean confLoadedFromFile = false;
	
	private long confReloadLastCheck;
	
	private long confLastLoad = 0;
	
	private FilterConfig filterConfig;
	
	private String settingsPath;
	
	private String fieldSeparatorSettingKeyPath;
	
	private StoreSeoUrlBuilderFactory storeSeoUrlBuilderFactory;
	
    /**
     * Overriding method to load configuration XML from setting instead of default XML file.
     * 
     * @param filterConfig Filter configuration.
     * @throws ServletException If loading of URL rewriter should fail.
     */
	@Override
    protected void loadUrlRewriter(final FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		
		if (settingsReader == null) {
			throw new EpSystemException("SettingsReader property not set.");
		}
		
		parseConfPath(filterConfig);
		
		context = filterConfig.getServletContext();
        if (context == null) {
        	throw new EpSystemException("Unable to init as servlet context is null.");
        }
        
        InputStream inputStream = null;
        if (confPath.startsWith(SETTING_PREFIX)) {
        	confLoadedFromFile = false;
    		
        	settingsPath = confPath.substring(SETTING_PREFIX.length());
        	LOG.info("Loading configuration from setting: " + settingsPath);
    		SettingValue settingValue = settingsReader.getSettingValue(settingsPath);
    		if (settingValue == null) {
    			throw new EpSystemException("No such setting path exists: " + settingsPath);
    		}
    		
    		String value = settingValue.getValue();
    		if (value == null) {
    			value = "";
    		}
        	inputStream = new ByteArrayInputStream(value.getBytes());
        	
        } else {
        	confLoadedFromFile = true;
        	
        	LOG.info("Loading configuration from resource: " + confPath);
        	inputStream = getResourceInputStream();
        }
        
		Conf conf = new Conf(context, inputStream, confPath, confUrlStr, modRewriteStyleConf);
		urlRewriter = new UrlRewriter(conf);
		
		
		// load the Token separator
		SettingValue value =  settingsReader.getSettingValue(fieldSeparatorSettingKeyPath);
		String fieldSeparator = null;
		 if (value == null) {
			fieldSeparator = SeoConstants.DEFAULT_SEPARATOR_BETWEEN_TOKENS;
		} else {
			fieldSeparator = value.getValue();
		}
		// reset the token separator for each builder that is already created 
		getStoreSeoUrlBuilderFactory().resetFieldSeparator(fieldSeparator);
		LOG.info("EpUrlRewriteFilter configured.");
    }
	
	private void parseConfPath(final FilterConfig filterConfig) {
		String modRewriteConf = filterConfig.getInitParameter("modRewriteConf");
        if (!StringUtils.isBlank(modRewriteConf)) {
            modRewriteStyleConf = "true".equals(StringUtils.trim(modRewriteConf).toLowerCase());
        }

        String confPathStr = filterConfig.getInitParameter("confPath");
        if (StringUtils.isBlank(confPathStr)) {
            if (modRewriteStyleConf) {
            	confPath = DEFAULT_MOD_REWRITE_STYLE_CONF_PATH;
            } else {
            	confPath = DEFAULT_WEB_CONF_PATH;
            }
        } else {
        	confPath = StringUtils.trim(confPathStr);
        }
	}
	
	private InputStream getResourceInputStream() {
    	URL confUrl = null;
    	try {
    		confUrl = context.getResource(confPath);
    	} catch (MalformedURLException e) {
    		LOG.error("UrlRewrite.xml Configuration path was not given in the correct form.");
    		throw new EpServiceException("Unable to configure url rewriter.", e);
    	}
    	
    	confUrlStr = null;
    	if (confUrl != null) {
    		confUrlStr = confUrl.toString();
    	}
    	
    	InputStream inputStream = context.getResourceAsStream(confPath);
    	if (inputStream == null) {
    		LOG.error("Unable to find urlrewrite configuration file at " + confPath);
    		// set the writer back to null
    		if (urlRewriter != null) {
    			LOG.error("Unloading existing conf");
    			urlRewriter = null;
    		}
    	} 
    	
    	return inputStream;
	}
	
	/**
     * Called for every request.
     * <p/>
     * Split from doFilter so that it can be overridden.
     * 
     * @param request The incoming request to filter.
     * @param response The response object.
     * @param chain The filter chain.
     * @return The URL rewriter previously initialised.
     */
	@Override
    protected UrlRewriter getUrlRewriter(final ServletRequest request, final ServletResponse response, final FilterChain chain) {
        // check to see if the conf needs reloading
        if (isConfReloadCheckEnabled() && !confReloadInProgress && isTimeToReloadConf()) {
            try {
            	confReloadInProgress = true;
            	reloadConf();
            } finally {
            	confReloadInProgress = false;
            }
        }
        return urlRewriter;
    }
    
    /**
     * Is it time to reload the configuration now.  Depends on is conf reloading is enabled.
     * 
     * @return True if the configuration has changed, false otherwise.
     */
    @Override
    public boolean isTimeToReloadConf() {
        boolean isTime = false;
        
    	// check if reload interval has passed since last check
    	long confReloadCheckInterval = getConfReloadCheckInterval() * MILLI;
    	long confReloadTime = System.currentTimeMillis() - confReloadCheckInterval; // interval is in seconds
    	isTime = confReloadTime > confReloadLastCheck;
    	
        return isTime;
    }

    /**
     * Forcibly reload the configuration now.
     */
    @Override
    public void reloadConf() {
        long now = System.currentTimeMillis();
        confReloadLastCheck = now;

        long confCurrentTime = 0;
        if (confLoadedFromFile) {
        	confCurrentTime = getConfFileLastModified();
        } else {
        	confCurrentTime = getConfSettingLastModified();
        }
        
        if (confLastLoad < confCurrentTime) {
            // reload conf
            confLastLoad = System.currentTimeMillis();
            if (confLoadedFromFile) {
            	LOG.info("Configuration file modified since last load, reloading.");
            } else {
            	LOG.info("Configuration setting modified since last load, reloading.");
            }            
            
            try {
            	loadUrlRewriter(filterConfig);
            } catch (ServletException e) {
            	LOG.error("Failed to reload URL rewriter.", e);
            }
        }        
    }
    
    /**
     * Gets the last modified date of the setting.
     *
     * @return time as a long
     */
    private long getConfSettingLastModified() {
        SettingValue settingValue = settingsReader.getSettingValue(settingsPath);
        if (settingValue == null) {
        	LOG.error("No such setting: " + settingsPath);
        }
        
        return settingValue.getLastModifiedDate().getTime();
    }
    
    /**
     * Gets the last modified date of the conf file.
     *
     * @return time as a long
     */
    private long getConfFileLastModified() {
        File confFile = new File(context.getRealPath(confPath));
        return confFile.lastModified();
    }

	/**
	 * Sets the setting reader to be used to load configuration.
	 * 
	 * @param settingsReader The setting reader to inject.
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
	
	/**
	 * Returns the time the last configuration reload was performed.
	 * 
	 * @return The time in milliseconds.
	 */
	public long getConfLastLoad() {
		return confLastLoad;
	}
	
	

	/**
	 * Getter for the setting path property.
	 * @return the fieldSeparatorSeparatorSettingKeyPath
	 */
	public String getFieldSeparatorSettingKeyPath() {
		return fieldSeparatorSettingKeyPath;
	}

	/**
	 * Setter for the setting path property.
	 * @param fieldSeparatorSeparatorSettingKeyPath the fieldSeparatorSeparatorSettingKeyPath to set
	 */
	public void setFieldSeparatorSettingKeyPath(final String fieldSeparatorSeparatorSettingKeyPath) {
		this.fieldSeparatorSettingKeyPath = fieldSeparatorSeparatorSettingKeyPath;
	}

	/**
	 * Getter for the factory that builds {@link SeoUrlBuilder}  objects.
	 * @return the storeSeoUrlBuilderFactory
	 */
	public StoreSeoUrlBuilderFactory getStoreSeoUrlBuilderFactory() {
		return storeSeoUrlBuilderFactory;
	}

	/**
	 * Setter for the factory that builds {@link SeoUrlBuilder}  objects.
	 * @param storeSeoUrlBuilderFactory the storeSeoUrlBuilderFactory to set
	 */
	public void setStoreSeoUrlBuilderFactory(final StoreSeoUrlBuilderFactory storeSeoUrlBuilderFactory) {
		this.storeSeoUrlBuilderFactory = storeSeoUrlBuilderFactory;
	}
	
	
}
