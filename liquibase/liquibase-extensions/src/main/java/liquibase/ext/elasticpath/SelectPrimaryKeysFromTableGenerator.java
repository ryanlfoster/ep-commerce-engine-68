package liquibase.ext.elasticpath;

import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.AbstractSqlGenerator;
import liquibase.util.StringUtils;

public class SelectPrimaryKeysFromTableGenerator extends AbstractSqlGenerator<SelectPrimaryKeysFromTableStatement> {

	public ValidationErrors validate(SelectPrimaryKeysFromTableStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		ValidationErrors errors = new ValidationErrors();
		errors.checkRequiredField("tableName", statement.getTableName());
		errors.checkRequiredField("primaryKeys", statement.getPrimaryKeys());
		return errors;
	}

	public Sql[] generateSql(SelectPrimaryKeysFromTableStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
		String[] columns = statement.getPrimaryKeys();
		int numberOfColumns = columns.length;
		String[] escapedColumns = new String[numberOfColumns];
		for (int i=0; i<numberOfColumns; i++) {
			escapedColumns[i] = database.escapeColumnName(statement.getSchemaName(), statement.getTableName(), columns[i]);
		}
		
		String sql = "SELECT " + StringUtils.join(escapedColumns, ",") + " FROM " +
		database.escapeTableName(statement.getSchemaName(), statement.getTableName());
		
		return new Sql[] { new UnparsedSql(sql) };
	}

}
