/*
 * Copyright 2006-2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elasticpath.service.osgi;

import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;

/**
 * Blueprint converter exposing the backing container conversion capabilities.
 * 
 * This has been copied from spring-osgi and modified to ignore property editors and conversion services. It
 * allows classes implementing an interface using generics to work within an OSGi blueprint container. Basically
 * it delegates the conversion to the Spring beans simple type converter.
 */
public class SpringBlueprintConverter implements Converter {

	private final TypeConverter typeConverter = new SimpleTypeConverter();

	@Override
	public boolean canConvert(final Object source, final ReifiedType targetType) {
		Class<?> required = targetType.getRawClass();
		try {
			typeConverter.convertIfNecessary(source, required);
			return true;
		} catch (TypeMismatchException ex) {
			return false;
		}
	}

	@Override
	public Object convert(final Object source, final ReifiedType targetType) throws Exception {
		Class<?> target = null;
		if (targetType != null) {
			target = targetType.getRawClass();
		}
		return typeConverter.convertIfNecessary(source, target);
	}

}
