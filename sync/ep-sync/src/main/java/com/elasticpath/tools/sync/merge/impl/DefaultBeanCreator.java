package com.elasticpath.tools.sync.merge.impl;

import com.elasticpath.base.Initializable;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.merge.BeanCreator;

/**
 * Default <code>BeanCreator</code> implementation based on reflection use for object creation.
 */
public class DefaultBeanCreator implements BeanCreator {

	@Override
	public Persistable createBean(final Class< ? > clazz) {
		final Persistable object = constructObject(clazz);
		if (object instanceof Initializable) {
			((Initializable) object).initialize();
		}
		return object;
	}

	/**
	 * Constructs obejct based on given class.
	 * 
	 * @param clazz the object class
	 * @return new object
	 */
	Persistable constructObject(final Class< ? > clazz) {
		try {
			return (Persistable) clazz.newInstance();
		} catch (final ClassCastException e) {
			throw new SyncToolRuntimeException("Failed to construct class :" + clazz, e);
		} catch (final InstantiationException e) {
			throw new SyncToolRuntimeException("Failed to construct class :" + clazz, e);
		} catch (final IllegalAccessException e) {
			throw new SyncToolRuntimeException("Failed to construct class :" + clazz, e);
		}
	}
}
