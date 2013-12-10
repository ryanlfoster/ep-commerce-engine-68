package com.elasticpath.service.contentspace;


import java.text.MessageFormat;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 *	ScriptEngineFactory create instance of ScriptEngine depends 
 *  from specified in content wrapper <code>script-language</code> value.
 */
public class ScriptEngineFactory {
	
	private static final Logger LOG = Logger.getLogger(ScriptEngineFactory.class);	
	
	private static Map<String, Class< ? extends ScriptEngine> > values;
	
	/**
	 * Create new instance of ScriptEngine depends from <code>scriptLanguageEnum</code>. 
	 * @param scriptLanguage Script language for content wrapper
	 * @return concrete instance of ScriptEngine
	 */
	public ScriptEngine getInstance(final String scriptLanguage) {
		Class< ? extends ScriptEngine> scriptEngineClass = values.get(scriptLanguage);
		if (scriptEngineClass != null) {
			try {
				return scriptEngineClass.newInstance();
			} catch (InstantiationException e) {
				LOG.error(e);
			} catch (IllegalAccessException e) {
				LOG.error(e);
			}
			
		}
		LOG.error(MessageFormat.format("Can not create instance of script engine for {0}", scriptLanguage));
		return null;
	}
	
	/**
	 * Set the supported script engines. 
	 * @param values Map of String and Class 
	 */
	public void setValues(final Map<String, Class< ? extends ScriptEngine>> values) {
		ScriptEngineFactory.values = values;
	}

}
