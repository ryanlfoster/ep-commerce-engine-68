package com.elasticpath.domain.store;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * The Type-Safe Enum for Store State.
 * <br><code>StoreState</code> was designed for future subclassing.
 * 
 * <p>
 * An example for subclass follows:
 * <pre>
 *     package my.package;          
 *     public class MyStoreState extends StoreState {
 *     
 *         public static final MyStoreState NEW_STATE = new MyStoreState("myNew_StoreState", 500);
 *         
 *         ...
 *
 *         protected MyState(final String name, final int value) {
 *             super(name, value);
 *         }
 *     }
 * </pre>
 * </p>
 */
public class StoreState implements Serializable {	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/*
	 * The declaration of values map should be placed before any StoreState constant.
	 */
	private static Map<Integer, StoreState> stateValuesMap = new HashMap<Integer, StoreState>();

	/**
	 * Under Construction Store State.
	 */
	public static final StoreState UNDER_CONSTRUCTION = new StoreState("StoreState_UnderConstruction", 0);

	/**
	 * Restricted Store State.
	 */
	public static final StoreState RESTRICTED = new StoreState("StoreState_Restricted", 100);

	/**
	 * Open Store State.
	 */
	public static final StoreState OPEN = new StoreState("StoreState_Open", 200);

	/*
	 * The Value Code which is saved in Store State.
	 */
	private final int value;

	/*
	 * The key name for messages.
	 */
	private final String nameMessageKey;

	/**
	 * The constructor.
	 * <br> Saves the value and registers itself in stateValuesMap (a static Map).
	 * <br> Subclasses will invoke this constructor and register themselves automatically.
	 * <br> StoreState constructor is protected. This at least protects the class from 
	 * <br> having anyone add a new state and forces subclassing of StoreState before 
	 * <br> adding a new state.
	 *
	 * @param name the name key for messages
	 * @param value the integer representation of StoreState
	 * @throws IllegalArgumentException if value is exist in stateValuesMap, to prevent duplication of value code in subclasses
	 */
	protected StoreState(final String name, final int value) {
		this.nameMessageKey = name;
		this.value = value;

		if (stateValuesMap.containsKey(value)) {
			throw new IllegalArgumentException("Duplicate StoreState value found : " + value + ", with name " + name);
		}

		stateValuesMap.put(value, this);
	}

	/**
	 * This implementation is simple and final for preventing overriding in subclass.
	 * {@inheritDoc}
	 * 
	 * @see Object#equals(Object)
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (!(obj instanceof StoreState)) {
			return false;
		}
		return value == ((StoreState) obj).value;
	}

	/**
	 * This implementation is simple and final for preventing overriding in subclass.
	 * {@inheritDoc}
	 * 
	 * @see Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	/**
	 * Gets the value.
	 *
	 * @return the value.
	 */
	public final Integer getValue() {
		return value;
	}

	/**
	 * Gets the value of nameMessageKey.
	 *
	 * @return the value of nameMessageKey.
	 */
	public final String getNameMessageKey() {
		return nameMessageKey;
	}
	
	/**
	 * Shows the completion stage of the Store State.
	 *
	 * @return true if value between UnderConstruction value and Restricted value and false otherwise.
	 */
	public final boolean isIncomplete() {
		return (UNDER_CONSTRUCTION.getValue() <= value) && (value < RESTRICTED.getValue());
	}

	/**
     * Returns the StoreState constant corresponding to specified value.
     * <br> Subclasses can use this method for theirs values, 
     * <br> because every StoreState constant registers itself in values Map.
     *
	 * @param value the StoreState Value
	 * @return the StoreState constant instance if value was found, or null otherwise.
	 */
	public static StoreState valueOf(final int value) {
		return stateValuesMap.get(value);
	}
	
}
