package liquibase.ext.elasticpath;

import static org.junit.Assert.assertEquals;
import liquibase.database.structure.type.DataType;
import liquibase.database.typeconversion.core.OracleTypeConverter;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link OracleTypeConverter}.
 * Verifies that the currently existing type converter handles converting VARCHAR to VARCHAR2.
 */
public class OracleTypeConverterTest {

	private OracleTypeConverter oracleTypeConverter;
	
	@Before
	public void setUp() {
		oracleTypeConverter = new OracleTypeConverter();
	}
	
	/**
	 * Test get data type with lower case.
	 */
	@Test
	public void testGetDataTypeWithLowerCase() {
		String columnTypeString = "varchar(64)";
		DataType updatedColumnType = oracleTypeConverter.getDataType(columnTypeString, false);
		String expectedColumnTypeString = "VARCHAR2(64)";
		
		assertEquals("Converted column type is incorrect.", expectedColumnTypeString, updatedColumnType.toString());
	}

	/**
	 * Test get data type with mixed case.
	 */
	@Test
	public void testGetDataTypeWithMixedCase() {
		String columnTypeString = "vArChaR(32)";
		DataType updatedColumnType = oracleTypeConverter.getDataType(columnTypeString, false);
		String expectedColumnTypeString = "VARCHAR2(32)";
		
		assertEquals("Converted column type is incorrect.", expectedColumnTypeString, updatedColumnType.toString());
	}

	/**
	 * Test get data type with upper case.
	 */
	@Test
	public void testGetDataTypeWithUpperCase() {
		String columnTypeString = "VARCHAR(16)";
		DataType updatedColumnType = oracleTypeConverter.getDataType(columnTypeString, false);
		String expectedColumnTypeString = "VARCHAR2(16)";
		
		assertEquals("Converted column type is incorrect.", expectedColumnTypeString, updatedColumnType.toString());
	}
	
	/**
	 * Test get data type with package.
	 */
	@Test
	public void testGetDataTypeWithPackage() {
		String columnTypeString = "java.sql.Types.VARCHAR(16)";
		DataType updatedColumnType = oracleTypeConverter.getDataType(columnTypeString, false);
		String expectedColumnTypeString = "VARCHAR2(16)";
		
		assertEquals("Converted column type is incorrect.", expectedColumnTypeString, updatedColumnType.toString());
	}
	
}
