package com.elasticpath.domain.dataimport.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.openjpa.persistence.DataCache;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Represents a default implementation of <code>ImportFault</code>.
 */
@Entity
@Table(name = ImportFaultImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ImportFaultImpl extends AbstractPersistableImpl implements ImportFault {
	
	private static final String COMMA = ",";
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/** Database Table. */
	protected static final String TABLE_NAME = "TIMPORTFAULT";

	private int level = WARNING;

	private String code;

	private String source;
	
	private long uidPk;

	private String argString;

	/**
	 * Returns the level.
	 *
	 * @return the level
	 */
	@Basic
	@Column(name = "LEVEL_NUMBER")
	public int getLevel() {
		return this.level;
	}

	/**
	 * Sets the level.
	 *
	 * @param level the level to set
	 */
	public void setLevel(final int level) {
		this.level = level;
	}

	/**
	 * Returns <code>true</code> if the fault is a warning.
	 *
	 * @return <code>true</code> if the fault is a warning
	 */
	@Transient
	public boolean isWarning() {
		return (this.getLevel() == WARNING);
	}

	/**
	 * Returns <code>true</code> if the fault is an error.
	 *
	 * @return <code>true</code> if the fault is an error
	 */
	@Transient
	public boolean isError() {
		return (this.getLevel() == ERROR);
	}

	/**
	 * Returns the fault code.
	 *
	 * @return the fault code.
	 */
	@Basic
	@Column(name = "CODE")
	public String getCode() {
		return this.code;
	}

	/**
	 * Sets the fault code.
	 *
	 * @param code the fault code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Returns the exception message.
	 *
	 * @return the exception message
	 */
	@Lob
	@Column(name = "SOURCE_MESSAGE")
	public String getSource() {
		return this.source;
	}

	/**
	 * Sets the exception message.
	 *
	 * @param source the exception message
	 */
	public void setSource(final String source) {
		this.source = source;
	}

	/**
	 * Returns the arguments for fault code.
	 *
	 * @return the arguments for fault code
	 */
	@Transient
	public Object[] getArgs() {
		if (getArgString() == null) {
			return new Object[0];
		}
		return StringUtils.splitPreserveAllTokens(getArgString(), COMMA);
	}
	
	/**
	 * Sets the arguments for fault code.
	 *
	 * @param args the arguments
	 */
	public void setArgs(final Object[] args) {
		setArgString(StringUtils.join(args, COMMA));
	}
	
	/**
	 * Get the arg String.
	 * 
	 * @return the string of the args
	 */
	@Basic
	@Column(name = "ARGS")
	protected String getArgString() {
		return argString;
	}
	
	/**
	 * Set the arg String.
	 * 
	 * @param argString the string of args
	 */
	protected void setArgString(final String argString) {
		this.argString = argString;
	}

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk	= uidPk;		
	}
	
	@Override
	public String toString() {
		ToStringBuilder toStringBuilder = new ToStringBuilder("", ToStringStyle.SIMPLE_STYLE);
	    if (org.apache.commons.lang.StringUtils.isNotBlank(getCode())) {
	    	toStringBuilder.append("code", getCode());
	    }
	    if (org.apache.commons.lang.StringUtils.isNotBlank(getSource())) {
	    	toStringBuilder.append("source", getSource());
	    }
	    if (org.apache.commons.lang.StringUtils.isNotBlank(getArgString())) {
	    	toStringBuilder.append("args", getArgString());
	    }
	    return toStringBuilder.toString();
	}
}
