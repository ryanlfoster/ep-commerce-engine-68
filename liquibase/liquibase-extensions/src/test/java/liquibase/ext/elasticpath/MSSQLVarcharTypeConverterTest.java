package liquibase.ext.elasticpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import liquibase.database.structure.type.DataType;
import liquibase.database.typeconversion.core.MSSQLTypeConverter;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link MSSQLVarcharTypeConverter}.
 */
public class MSSQLVarcharTypeConverterTest {

	private MSSQLVarcharTypeConverter mssqlVarcharTypeConverter;

	@Before
	public void setUp() {
		mssqlVarcharTypeConverter = new MSSQLVarcharTypeConverter();
	}
	
	/**
	 * Test get data type with lower case.
	 */
	@Test
	public void testGetDataTypeWithLowerCase() {
		String columnTypeString = "varchar(64)";
		DataType updatedColumnType = mssqlVarcharTypeConverter.getDataType(columnTypeString, false);
		String expectedColumnTypeString = "NVARCHAR(64)";
		
		assertEquals("Converted column type is incorrect.", expectedColumnTypeString, updatedColumnType.toString());
	}

	/**
	 * Test get data type with mixed case.
	 */
	@Test
	public void testGetDataTypeWithMixedCase() {
		String columnTypeString = "vArChaR(32)";
		DataType updatedColumnType = mssqlVarcharTypeConverter.getDataType(columnTypeString, false);
		String expectedColumnTypeString = "NVARCHAR(32)";
		
		assertEquals("Converted column type is incorrect.", expectedColumnTypeString, updatedColumnType.toString());
	}

	/**
	 * Test get data type with upper case.
	 */
	@Test
	public void testGetDataTypeWithUpperCase() {
		String columnTypeString = "VARCHAR(16)";
		DataType updatedColumnType = mssqlVarcharTypeConverter.getDataType(columnTypeString, false);
		String expectedColumnTypeString = "NVARCHAR(16)";
		
		assertEquals("Converted column type is incorrect.", expectedColumnTypeString, updatedColumnType.toString());
	}

	/**
	 * Test get data type with package.
	 */
	@Test
	public void testGetDataTypeWithPackage() {
		String columnTypeString = "java.sql.Types.VARCHAR(16)";
		DataType updatedColumnType = mssqlVarcharTypeConverter.getDataType(columnTypeString, false);
		String expectedColumnTypeString = "NVARCHAR(16)";
		
		assertEquals("Converted column type is incorrect.", expectedColumnTypeString, updatedColumnType.toString());
	}
	
	/**
	 * Ensure priority of extension is higher, or extension is not guaranteed to run.
	 */
	@Test
	public void ensurePriorityOfExtensionIsHigher() {
		final MSSQLTypeConverter mssqlTypeConverter = new MSSQLTypeConverter();

		assertTrue("Priority of type converter extension must be higher than superclass.", 
				mssqlTypeConverter.getPriority() < mssqlVarcharTypeConverter.getPriority());
		
	}
	
}
