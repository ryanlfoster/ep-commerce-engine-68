package com.elasticpath.importexport.importer.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elasticpath.importexport.importer.types.DependentElementType;

/**
 * The xml adapter for data transformation between <code>DependentElementListConfiguartion</code> and
 * <code>Map&lt;DependentElementType, DependentElementConfiguration></code>.
 */
public class DependentElementAdapter extends XmlAdapter<DependentElementListConfiguartion, Map<DependentElementType, DependentElementConfiguration>> {

	@Override
	public DependentElementListConfiguartion marshal(final Map<DependentElementType, DependentElementConfiguration> dependentElementMap)
			throws Exception {
		DependentElementListConfiguartion dependentElementListConfiguartion = new DependentElementListConfiguartion();
		dependentElementListConfiguartion.setDependentElemenList(new ArrayList<DependentElementConfiguration>(dependentElementMap.values()));
		return dependentElementListConfiguartion;
	}

	@Override
	public Map<DependentElementType, DependentElementConfiguration> unmarshal(final DependentElementListConfiguartion dependentElementConfiguartion)
			throws Exception {
		Map<DependentElementType, DependentElementConfiguration> dependenElementMap = 
			new HashMap<DependentElementType, DependentElementConfiguration>();
		for (DependentElementConfiguration configuration : dependentElementConfiguartion.getDependentElemenList()) {
			dependenElementMap.put(configuration.getDependentElementType(), configuration);
		}
		return dependenElementMap;
	}

}
