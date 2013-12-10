package liquibase.ext.elasticpath;

import liquibase.database.structure.type.DataType;
import liquibase.database.typeconversion.core.MSSQLTypeConverter;

import org.apache.commons.lang.StringUtils;

/**
 * Extension of {@link MSSQLTypeConverter} to add VARCHAR type conversion. 
 */
public class MSSQLVarcharTypeConverter extends MSSQLTypeConverter {
	private static final String NVARCHAR = "NVARCHAR";
	private static final String VARCHAR = "VARCHAR";


	/**
	 * Priority of any extension must be higher than its superclass or it is not guaranteed to be consumed by liquibase. 
	 *
	 * @return the priority
	 */
	@Override
    public int getPriority() {
        return super.getPriority() + 1;
    }
	
	/**
	 * Extension of {@link MSSQLTypeConverter} to support conversion of the VARCHAR type to NVARCHAR for MSSQL.
	 *
	 */
	@Override
    public DataType getDataType(String columnTypeString, Boolean autoIncrement) {
        final String upperCaseColumnTypeString = columnTypeString.toUpperCase();
        
		if (upperCaseColumnTypeString.contains(VARCHAR + "(") || upperCaseColumnTypeString.equals(VARCHAR)) {
			final String convertedColumnTypeString = NVARCHAR + StringUtils.substringAfter(upperCaseColumnTypeString, VARCHAR);
	        return super.getDataType(convertedColumnTypeString, autoIncrement);
        }
        
        return super.getDataType(columnTypeString, autoIncrement);
	}
}
