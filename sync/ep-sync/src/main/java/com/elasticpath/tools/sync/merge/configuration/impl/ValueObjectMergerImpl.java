package com.elasticpath.tools.sync.merge.configuration.impl;

import java.util.Map;
import java.util.Set;

import com.elasticpath.tools.sync.merge.configuration.ValueObjectMerger;
import com.elasticpath.tools.sync.utils.SyncUtils;

/**
 * Merges custom fields for specified value objects.
 */
public class ValueObjectMergerImpl implements ValueObjectMerger {

	private Map<Class< ? >, Set<String>> fieldMethodNames;

	private SyncUtils syncUtils;

	@Override
	public boolean isMergeRequired(final Class< ? > clazz) {
		return fieldMethodNames.containsKey(clazz);
	}

	@Override
	public void merge(final Object source, final Object target) {
		if (!fieldMethodNames.containsKey(source.getClass())) {
			return;
		}
		for (String getterName : fieldMethodNames.get(source.getClass())) {
			copyField(getterName, source, target);
		}
	}

	/*
	 * Gets a field from source using getter and puts it into target using appropriate setter.
	 */
	private void copyField(final String getterName, final Object source, final Object target) {
		final String setterName = syncUtils.createSetterName(getterName);
		final Object value = syncUtils.invokeGetterMethod(source, syncUtils.findDeclaredMethodWithFallback(
				source.getClass(), getterName));
		syncUtils.invokeSetterMethod(target, syncUtils.findDeclaredMethodWithFallback(
				target.getClass(), setterName, syncUtils.convertToPrimitive(value.getClass())), value);
	}

	/**
	 * @param fieldMethodNames getter names for fields to be updated in objects of specific class
	 */
	public void setFieldMethodNames(final Map<Class< ? >, Set<String>> fieldMethodNames) {
		this.fieldMethodNames = fieldMethodNames;
	}

	/**
	 * @param syncUtils for operations on objects
	 */
	public void setSyncUtils(final SyncUtils syncUtils) {
		this.syncUtils = syncUtils;
	}
}
