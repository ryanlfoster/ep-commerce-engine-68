package com.elasticpath.commons.util.impl;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Utilities for using JAXB.
 */
public final class JaxbUtils {
	
	private JaxbUtils() {
		// Private contructor to prevent instantiation of this class.
	}
	
	/**
	 * Creates an new array suitable for constructing a {@link javax.xml.bind.JAXBContext} by passing the result of this method to
	 * {@link javax.xml.bind.JAXBContext#newInstance(Class[])}. This allows {@link javax.xml.bind.Marshaller}s or {@link javax.xml.bind.Unmarshaller}s
	 * to be created from the context, to instruct it which classes should be used to marshall to XML, or unmarshall from XML, respectively.
	 *
	 * @param mainClass the class type that the {@link javax.xml.bind.Unmarshaller} will return, or the {@link javax.xml.bind.Marshaller}
	 *                  will serialize to XML.
	 * @param otherClasses other JAXB-annotated classes that the {@link javax.xml.bind.Marshaller}/{@link javax.xml.bind.Unmarshaller}
	 *                     should know about in its {@link javax.xml.bind.JAXBContext}.
	 * @param <T> the class type to marshall/unmarshall
	 * @return an array with the <code>mainClass</code> at the end of the array, and the <code>otherClasses</code> (if any) before it.
	 */
	public static <T> Class<?>[] createClassArray(final Class<T> mainClass, final Collection<Class<?>> otherClasses) {
		Class<?>[] otherClassesArray = ArrayUtils.EMPTY_CLASS_ARRAY;

		if (CollectionUtils.isNotEmpty(otherClasses)) {
			otherClassesArray = otherClasses.toArray(new Class<?>[otherClasses.size()]);
		}

		return createClassArray(mainClass, otherClassesArray);
	}

	/**
	 * Creates an new array suitable for constructing a {@link javax.xml.bind.JAXBContext} by passing the result of this method to
	 * {@link javax.xml.bind.JAXBContext#newInstance(Class[])}. This allows {@link javax.xml.bind.Marshaller}s or {@link javax.xml.bind.Unmarshaller}s
	 * to be created from the context, to instruct it which classes should be used to marshall to XML, or unmarshall from XML, respectively.
	 *
	 * @param mainClass the class type that the {@link javax.xml.bind.Unmarshaller} will return, or the {@link javax.xml.bind.Marshaller}
	 *                  will serialize to XML.
	 * @param otherClasses other JAXB-annotated classes that the {@link javax.xml.bind.Marshaller}/{@link javax.xml.bind.Unmarshaller}
	 *                     should know about in its {@link javax.xml.bind.JAXBContext}.
	 * @param <T> the class type to marshall/unmarshall
	 * @return an array with the <code>mainClass</code> at the end of the array, and the <code>otherClasses</code> (if any) before it.
	 */
	public static <T> Class<?>[] createClassArray(final Class<T> mainClass, final Class<?>... otherClasses) {
		final Class<?>[] result = new Class<?>[otherClasses.length + 1];

		if (otherClasses.length != 0) {
			System.arraycopy(otherClasses, 0, result, 0, otherClasses.length);
		}

		result[result.length - 1] = mainClass;

		return result;
	}
}
