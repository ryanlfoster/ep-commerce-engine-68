package com.elasticpath.tags.domain.impl;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.ReadOnly;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumns;
import org.apache.openjpa.persistence.jdbc.Unique;

import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.tags.domain.TagAllowedValue;
import com.elasticpath.tags.domain.TagOperator;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.validation.domain.ValidationConstraint;
import com.elasticpath.validation.domain.impl.TagValueTypeDeclarativeValidationConstraintImpl;

/**
 * Implementation of {@link TagValueType}. 
 * Class represents value type of tag definition - like age, free text, positive number, country, etc.
 *     
 */
@Entity
@Table(name = TagValueTypeImpl.TABLE_NAME)
public class TagValueTypeImpl extends AbstractEntityImpl implements TagValueType {

	/**
     * Database Table.
     */
    public static final String TABLE_NAME = "TTAGVALUETYPE";
    
    private static final String GUID = "GUID";
        
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 5000000016L;

    private long uidPk;
    private String javaType;
    private String uiPickerKey;
    
    /**
     * a list of tag value type operators.
     */
    private Set<TagOperator> tagValueTypeOperators = new HashSet<TagOperator>();
    private Set<ValidationConstraint> validationConstraints = new HashSet<ValidationConstraint>();
    
    /**
     * a list of allowed values.
     */
    private Set<TagAllowedValue> tagAllowedValues = new HashSet<TagAllowedValue>();

	private String guid;
    
    
    @Override
    @Id
    @Column(name = "UIDPK")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
    @TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
    public long getUidPk() {
        return this.uidPk;
    }

    @Override
    public void setUidPk(final long uidPk) {
        this.uidPk = uidPk;
    }
    
    /**
     * @return the guid.
     */
    @Override
    @Basic
    @Column(name = GUID)
    @Unique(name = "TAGVALUETYPE_UNIQUE")
    public String getGuid() {
    	return guid;
    }

    /**
     * Set the guid.
     *
     * @param guid the guid to set.
     */
    @Override
    public void setGuid(final String guid) {
		this.guid = guid;
    }
    
        
    /**
     * @return the string representation of java type.
     * 					Basically a Java class name.
     */
    @Basic
    @Column(name = "JAVA_TYPE")
    @ReadOnly
	public String getJavaType() {
		return this.javaType;
	}

    /**
     * Sets the string representation of java type.
     *
     * @param javaType the string representation of java type
     */
    public void setJavaType(final String javaType) {
    	 this.javaType = javaType;
    }
    
    /**
     * @return the string represents symbolic name of UI picker.
     * 					Basically a Java class name.
     */
    @Basic
    @Column(name = "UI_PICKER_KEY")
    @ReadOnly
	public String getUIPickerKey() {
		return this.uiPickerKey;
	}
    
    /**
     * Set the string that represents symbolic name of UI picker.
     *
     * @param uiPickerKey the string represents symbolic name of UI picker.
     */
    public void setUIPickerKey(final String uiPickerKey) {
    	 this.uiPickerKey = uiPickerKey;
    }
    

    /**
     * @return a set of operators that go along with this tag value type.
     */
    @ManyToMany(targetEntity = TagOperatorImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinTable(
      name = "TTAGVALUETYPEOPERATOR",
      joinColumns = { @JoinColumn(name = "TAGVALUETYPE_GUID", referencedColumnName = GUID) },
      inverseJoinColumns = { @JoinColumn(name = "TAGOPERATOR_GUID", referencedColumnName = GUID) }
    )
    @ReadOnly
	public Set<TagOperator> getOperators() {
		return tagValueTypeOperators;
	}

    /**
     * Sets a set of {@link TagOperator}.
     *
     * @param tagValueTypeOperators a set of {@link TagOperator}
     */
    public void setOperators(final  Set<TagOperator> tagValueTypeOperators) {
        this.tagValueTypeOperators = tagValueTypeOperators;
    }
    
    
    
    /**
     * @return a set of allowed values
     */
    @OneToMany(targetEntity = TagAllowedValueImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @ElementJoinColumn(name = "TAGVALUETYPE_GUID", referencedColumnName = GUID, nullable = false)
    @ElementForeignKey(name = "FK_TAGALLOWEDVAL_TAGTYPE")
	@ElementDependent
    public Set<TagAllowedValue> getAllowedValues() {
        return this.tagAllowedValues;
    }
    
    /**
     * Sets the allowable values.
     *
     * @param tagAllowedValues a set of allowed values to be set
     */
    protected void setAllowedValues(final Set<TagAllowedValue> tagAllowedValues) {
        this.tagAllowedValues = tagAllowedValues;
    }
    
    /**
     * Add {@link TagAllowedValue} to allowed values set.
     * @param tagAllowedValue value to add
     */
    public void addAllowedValue(final TagAllowedValue tagAllowedValue) {
    	this.tagAllowedValues.add(tagAllowedValue);
    }
    


    /**
     * Generate the hash code.
     *
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        if (getGuid() == null) {
            return 0;
        }
        return getGuid().hashCode();
    }

    
    /**
     * Determines whether the given object is equal to this TagValueType.
     *
     * @param obj to be compared for equality
     * @return true if the given object's GUID is equal to this one's GUID
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof TagValueType && this.getGuid().equals(((TagValueType) obj).getGuid());

    }

    /**
     * @return the string representation of the {@link TagValueType}
     */
    @Override
    public String toString() {
        return "[TagValueType: "
                + " GUID=" + this.getGuid()
                + " JavaType=" + this.getJavaType()
                + " UI PikerKey=" + this.getUIPickerKey()
                + "]";
    }

    @Override
	@OneToMany(targetEntity = TagValueTypeDeclarativeValidationConstraintImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@ElementJoinColumns({
		@ElementJoinColumn(name = "OBJECT_UID", referencedColumnName = "UIDPK", nullable = false),
		@ElementJoinColumn(name = "TVALIDATIONCONSTRAINTS.TYPE", referencedColumnName = "'TagValueType'")
	})	
	@ElementDependent
	public Set<ValidationConstraint> getValidationConstraints() {
		return validationConstraints;
	}

	@Override
	public void setValidationConstraints(final Set<ValidationConstraint> validationConstraints) {
		this.validationConstraints = validationConstraints;
	}

}
