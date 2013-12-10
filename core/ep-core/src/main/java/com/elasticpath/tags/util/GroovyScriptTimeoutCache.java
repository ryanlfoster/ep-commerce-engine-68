package com.elasticpath.tags.util;

import groovy.lang.Script;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;
import org.codehaus.groovy.runtime.InvokerHelper;

import com.elasticpath.cache.SimpleTimeoutCache;

/**
 * Simple timeout cache implementation for groovy scripts.  When the script is removed from the cache
 * the Class generated for this script is unloaded from memory to prevent a memory leak.
 */
public class GroovyScriptTimeoutCache extends 
	SimpleTimeoutCache<String, FutureTask<Script>> {

	private static final Logger LOG = Logger.getLogger(GroovyScriptTimeoutCache.class);
	
	/**
	 * Default constructor.
	 * 
	 * @param timeout cache timeout.
	 */
	public GroovyScriptTimeoutCache(final long timeout) {
		super(timeout);
	}
	
	/**
	 * Hook to unload classes from Groovy Shell after they have been expired.
	 * 
	 * @param key the key for script
	 * @param script the script
	 */
	@Override
	protected void beforeRemoveHook(final String key, final FutureTask<Script> script) {
		Script compiledScript = null;
		try {
			compiledScript = script.get();
		} catch (InterruptedException iex) {
			return;
		} catch (ExecutionException eex) {
			return;
		}
		if (compiledScript != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Evicting class: " + compiledScript.getClass().getCanonicalName() 
						+ " for script: \n" + key);
			}
			InvokerHelper.removeClass(compiledScript.getClass());
		}
	}

}
