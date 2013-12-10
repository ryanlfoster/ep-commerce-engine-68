package liquibase.ext.elasticpath;

import liquibase.statement.AbstractSqlStatement;

public class SelectPrimaryKeysFromTableStatement extends AbstractSqlStatement {

	private String[] primaryKeys;

	private String schemaName;

	private String tableName;

	public SelectPrimaryKeysFromTableStatement(String schemaName, String tableName, String... columnsToSelect) {
		this.schemaName = schemaName;
		this.tableName = tableName;
		this.primaryKeys = columnsToSelect;
	}

	public String[] getPrimaryKeys() {
		return primaryKeys;
	}

	public String getTableName() {
		return tableName;
	}

	public String getSchemaName() {
		return schemaName;
	}
}
