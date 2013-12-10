package com.elasticpath.common.dto;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.changeset.ChangeSetMemberAction;

/**
 * The change set dependency dto class.
 */
public class ChangeSetDependencyDto implements Serializable {
	
	private static final String META_DATA_KEY_ACTION = "action";

	private static final String META_DATA_KEY_OBJECT_NAME = "objectName";

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private String sourceObjectName;
	private String sourceObjectType;
	private String sourceObjectIdentifier;
	private String dependencyObjectName;
	private String dependencyObjectType;
	private String dependencyObjectIdentifier;
	private String dependencyObjectAction;
	private String dependencyChangeSetName;
	private String dependencyChangeSetGuid;
	
	/**
	 * The default constructor.
	 */
	public ChangeSetDependencyDto() {
		//default constructor
	}
	
	/**
	 * The constructor.
	 * 
	 * @param sourceObject the source object
	 * @param sourceMetaData the meta data of the source object
	 * @param dependencyObject the dependency object
	 * @param dependencyMetaData the meta data of the dependency object
	 * @param changeSet the dependency change set
	 */
	public ChangeSetDependencyDto(final BusinessObjectDescriptor sourceObject, final Map<String, String> sourceMetaData, 
			final BusinessObjectDescriptor dependencyObject, final Map<String, String> dependencyMetaData, final ChangeSet changeSet) {
		if (MapUtils.isNotEmpty(sourceMetaData)) {
			sourceObjectName = sourceMetaData.get(META_DATA_KEY_OBJECT_NAME);
		}
		
		if (MapUtils.isNotEmpty(dependencyMetaData)) {
			dependencyObjectName = dependencyMetaData.get(META_DATA_KEY_OBJECT_NAME);
			dependencyObjectAction = dependencyMetaData.get(META_DATA_KEY_ACTION);
		}
		
		sourceObjectType = sourceObject.getObjectType();
		sourceObjectIdentifier = sourceObject.getObjectIdentifier();
		dependencyObjectType = dependencyObject.getObjectType();
		dependencyObjectIdentifier = dependencyObject.getObjectIdentifier();
		
		dependencyChangeSetName = changeSet.getName();
		dependencyChangeSetGuid = changeSet.getGuid();
	}

	/**
	 * Get source object name.
	 * 
	 * @return the source object name
	 */
	public String getSourceObjectName() {
		return sourceObjectName;
	}

	/**
	 * Set source object name.
	 * 
	 * @param sourceObjectName the source object name
	 */
	public void setSourceObjectName(final String sourceObjectName) {
		this.sourceObjectName = sourceObjectName;
	}

	/**
	 * Get source object type.
	 * 
	 * @return the source object type
	 */
	public String getSourceObjectType() {
		return sourceObjectType;
	}

	/**
	 * Set source object type.
	 * 
	 * @param sourceObjectType the source object type
	 */
	public void setSourceObjectType(final String sourceObjectType) {
		this.sourceObjectType = sourceObjectType;
	}

	/**
	 * get source object identifier.
	 * 
	 * @return the source object identifier
	 */
	public String getSourceObjectIdentifier() {
		return sourceObjectIdentifier;
	}

	/**
	 * Set source object identifier.
	 * 
	 * @param sourceObjectIdentifier the source object identifier
	 */
	public void setSourceObjectIdentifier(final String sourceObjectIdentifier) {
		this.sourceObjectIdentifier = sourceObjectIdentifier;
	}

	/**
	 * get Dependency object name.
	 * 
	 * @return the Dependency object name
	 */
	public String getDependencyObjectName() {
		return dependencyObjectName;
	}

	/**
	 * Set Dependency object name.
	 * 
	 * @param dependencyObjectName the Dependency object name
	 */
	public void setDependencyObjectName(final String dependencyObjectName) {
		this.dependencyObjectName = dependencyObjectName;
	}

	/**
	 * Get dependency object type.
	 * 
	 * @return the dependency object type
	 */
	public String getDependencyObjectType() {
		return dependencyObjectType;
	}

	/**
	 * set dependency object type.
	 * 
	 * @param dependencyObjectType the dependency object type
	 */
	public void setDependencyObjectType(final String dependencyObjectType) {
		this.dependencyObjectType = dependencyObjectType;
	}

	/**
	 * get dependency object identifier.
	 * 
	 * @return the dependency object identifier
	 */
	public String getDependencyObjectIdentifier() {
		return dependencyObjectIdentifier;
	}

	/**
	 * Set dependency object identifier.
	 * 
	 * @param dependencyObjectIdentifier the dependency object identifier
	 */
	public void setDependencyObjectIdentifier(final String dependencyObjectIdentifier) {
		this.dependencyObjectIdentifier = dependencyObjectIdentifier;
	}

	/**
	 * Get dependency change set name.
	 * 
	 * @return the dependency change set name
	 */
	public String getDependencyChangeSetName() {
		return dependencyChangeSetName;
	}

	/**
	 * Set dependency change set name.
	 * 
	 * @param dependencyChangeSetName the dependency change set name
	 */
	public void setDependencyChangeSetName(final String dependencyChangeSetName) {
		this.dependencyChangeSetName = dependencyChangeSetName;
	}

	/**
	 * Get dependency change set guid.
	 * 
	 * @return the dependency change set guid
	 */
	public String getDependencyChangeSetGuid() {
		return dependencyChangeSetGuid;
	}

	/**
	 * Set dependency change set guid.
	 * 
	 * @param dependencyChangeSetGuid the dependency change set guid
	 */
	public void setDependencyChangeSetGuid(final String dependencyChangeSetGuid) {
		this.dependencyChangeSetGuid = dependencyChangeSetGuid;
	}

	/**
	 * get dependency object action.
	 * 
	 * @return the dependency object action
	 */
	public String getDependencyObjectAction() {
		return dependencyObjectAction;
	}

	/**
	 * Set dependency object action.
	 * 
	 * @param dependencyObjectAction the dependency object action
	 */
	public void setDependencyObjectAction(final String dependencyObjectAction) {
		this.dependencyObjectAction = dependencyObjectAction;
	}
	
	/**
	 * is dependency object deleted.
	 * 
	 * @return true if the dependency object is deleted
	 */
	public boolean isDependencyObjectDeleted() {
		boolean isDeleted = false;
		if (StringUtils.isNotBlank(dependencyObjectAction)) { 
			isDeleted = ChangeSetMemberAction.DELETE == ChangeSetMemberAction.getChangeSetMemberAction(dependencyObjectAction);
		}
		return isDeleted;
	}

}
