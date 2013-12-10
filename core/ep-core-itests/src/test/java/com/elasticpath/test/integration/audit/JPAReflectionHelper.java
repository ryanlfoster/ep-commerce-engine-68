/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration.audit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.openjpa.enhance.Reflection;
import org.apache.openjpa.meta.ClassMetaData;
import org.apache.openjpa.meta.FieldMetaData;
import org.apache.openjpa.persistence.JPAFacadeHelper;

import com.elasticpath.domain.audit.DataChanged;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;

/**
 * JPAReflectionHelper class.
 */
public class JPAReflectionHelper {

	/**
	 * Check field meta data values.
	 * @param category category	
	 * @param dataChanges a list of data changed
	 * @param entityManager entity manager
	 */
	public static void checkFieldMetaDataValues(final Category category,
										  final List<DataChanged> dataChanges, 
										  final EntityManager entityManager) {
		
		ClassMetaData metaData = JPAFacadeHelper.getMetaData(entityManager, category.getClass());
        for (FieldMetaData fieldMetaData : metaData.getFields()) {

              Method method = Reflection.findGetter(category.getClass(), fieldMetaData.getName(), true);
              String fieldName = fieldMetaData.getName();

              String fieldValue = getFieldValue(Reflection.get(category, method));
                          
              DataChanged dataChanged = findDataChanged(fieldName, dataChanges);
              assertNotNull("Field data is not correct", dataChanged);
              assertEquals("Field values do not match ", fieldValue, dataChanged.getFieldNewValue());              
        }
	}
	
	/**
	 * Find data changed.
	 * @param fieldName  field name
	 * @param dataChanges a list of data changed
	 * @return element changed
	 */
	public static DataChanged findDataChanged(final String fieldName, 
										final List<DataChanged> dataChanges) {
				
		for (DataChanged dataChanged : dataChanges) {
			if (dataChanged.getFieldName().equals(fieldName)) {
				return dataChanged;
			}
		}			
		return null;
	}
	
	/**
	 * Get field value.
	 * @param field is the field
	 * @return the field value 
	 */
	public static String getFieldValue(final Object field) {
		if (field == null) {
			return null;
		}
		if (field instanceof Entity) {
			return ((Entity) field).getGuid();
		}
		if (field instanceof Persistable) {
			return String.valueOf(((Persistable) field).getUidPk());
		}
		return field.toString();
	}
}

