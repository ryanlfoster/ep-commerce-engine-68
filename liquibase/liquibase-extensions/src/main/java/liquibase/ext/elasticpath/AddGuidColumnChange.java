package liquibase.ext.elasticpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import liquibase.change.AbstractChange;
import liquibase.change.Change;
import liquibase.change.ChangeMetaData;
import liquibase.change.ColumnConfig;
import liquibase.change.ConstraintsConfig;
import liquibase.change.core.AddColumnChange;
import liquibase.change.core.AddNotNullConstraintChange;
import liquibase.change.core.AddUniqueConstraintChange;
import liquibase.change.core.DropColumnChange;
import liquibase.database.Database;
import liquibase.database.structure.type.DataType;
import liquibase.database.typeconversion.TypeConverter;
import liquibase.database.typeconversion.TypeConverterFactory;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.exception.UnsupportedChangeException;
import liquibase.executor.ExecutorService;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.UpdateStatement;

import com.elasticpath.domain.misc.impl.RandomGuidImpl;

/**
 *
 */
public class AddGuidColumnChange extends AbstractChange {

	/**
	 * The schema name.
	 */
	private String schemaName;

	/**
	 * The table name.
	 */
	private String tableName;

	/**
	 * The name of the GUID column.
	 */
	private String columnName = "GUID";

	/**
	 * The type of the column.
	 */
	protected String columnType = null;

	/**
	 * The primary key for the table (if composite, use common to separate column names). Used to select unique rows and introduce GUID.
	 */
	private String primaryKey;

	public AddGuidColumnChange() {
		super("addGuidColumn", "adds a guid column to a table, selects existing rows and fills in values for all existing rows.",
				ChangeMetaData.PRIORITY_DEFAULT);
	}

	/**
	 * @see liquibase.change.Change#getConfirmationMessage()
	 */
	public String getConfirmationMessage() {
		return null;
	}

	private String generateRandomGuid() {
		return new RandomGuidImpl().toString();
	}

	private String generateColumnType(Database database) {
		TypeConverter typeConverter = TypeConverterFactory.getInstance().findTypeConverter(database);
		DataType dataType = typeConverter.getDataType(generateRandomGuid());
		dataType.setFirstParameter("64");
		return dataType.toString();

	}

	private AddColumnChange createGuidColumn() {
		AddColumnChange addNewColumnChange = new AddColumnChange();
		addNewColumnChange.setSchemaName(getSchemaName());
		addNewColumnChange.setTableName(getTableName());
		ColumnConfig columnConfig = new ColumnConfig();
		columnConfig.setName(getColumnName());
		columnConfig.setType(getColumnType());
		columnConfig.setAutoIncrement(Boolean.FALSE);

		ConstraintsConfig constraints = new ConstraintsConfig();
		constraints.setNullable(Boolean.TRUE);
		constraints.setPrimaryKey(Boolean.FALSE);
		constraints.setUnique(Boolean.FALSE);
		columnConfig.setConstraints(constraints);

		addNewColumnChange.addColumn(columnConfig);

		return addNewColumnChange;
	}

	/** Modified from LoadUpdateDataChange */
	private String getWhereClause(String primaryKeys, Map<?, ?> keyValues, Database database) {
		StringBuffer where = new StringBuffer();

		String[] pkColumns = primaryKeys.split(",");

		for (String thisPkColumn : pkColumns) {
			where.append(database.escapeColumnName(getSchemaName(), getTableName(), thisPkColumn) + " = ");
			Object newValue = keyValues.get(thisPkColumn);
			if (newValue == null || newValue.toString().equals("NULL")) {
				where.append("NULL");
			} else if (newValue instanceof String && database.shouldQuoteValue(((String) newValue))) {
				where.append("'").append(database.escapeStringForDatabase((String) newValue)).append("'");
			} else if (newValue instanceof Date) {
				where.append(database.getDateLiteral(((Date) newValue)));
			} else if (newValue instanceof Boolean) {
				if (((Boolean) newValue)) {
					where.append(TypeConverterFactory.getInstance().findTypeConverter(database).getBooleanType().getTrueBooleanValue());
				} else {
					where.append(TypeConverterFactory.getInstance().findTypeConverter(database).getBooleanType().getFalseBooleanValue());
				}
			} else {
				where.append(newValue);
			}

			where.append(" AND ");
		}

		where.delete(where.lastIndexOf(" AND "), where.lastIndexOf(" AND ") + " AND ".length());
		return where.toString();
	}

	private void addGuidValue(Database database, List<SqlStatement> statements) {

		try {
			SqlStatement stmt = new SelectPrimaryKeysFromTableStatement(getSchemaName(), getTableName(), getPrimaryKey().split(","));
			@SuppressWarnings("rawtypes")
			List<Map> rows = ExecutorService.getInstance().getExecutor(database).queryForList(stmt);
			if (rows != null && rows.size() > 0) {
				for (Map<?, ?> row : rows) {

					UpdateStatement update = new UpdateStatement(getSchemaName(), getTableName());

					update.addNewColumnValue(getColumnName(), generateRandomGuid());
					update.setWhereClause(getWhereClause(primaryKey, row, database));

					statements.add(update);
				}
			}
		} catch (Exception e) {
			throw new UnexpectedLiquibaseException(e);
		}
	}

	private Change addNotNullConstraint() {
		AddNotNullConstraintChange notNull = new AddNotNullConstraintChange();

		notNull.setSchemaName(getSchemaName());
		notNull.setTableName(getTableName());
		notNull.setColumnName(getColumnName());
		notNull.setColumnDataType(getColumnType());
		return notNull;

	}

	private Change addUniqueConstraint() {
		AddUniqueConstraintChange uniqueConstraint = new AddUniqueConstraintChange();
		uniqueConstraint.setSchemaName(getSchemaName());
		uniqueConstraint.setTableName(getTableName());
		uniqueConstraint.setColumnNames(getColumnName());
		return uniqueConstraint;
	}

	private void initializeOptionalProperties(Database database) {

		if (getColumnType() == null) {
			setColumnType(generateColumnType(database));
		}

		if (getSchemaName() == null) {
			setSchemaName(database.getDefaultSchemaName());
		}
	}

	/**
	 * @see liquibase.change.Change#generateStatements(liquibase.database.Database)
	 */
	public SqlStatement[] generateStatements(Database database) {

		initializeOptionalProperties(database);

		List<SqlStatement> statements = new ArrayList<SqlStatement>();

		statements.addAll(Arrays.asList(createGuidColumn().generateStatements(database)));

		addGuidValue(database, statements);

		statements.addAll(Arrays.asList(addNotNullConstraint().generateStatements(database)));

		statements.addAll(Arrays.asList(addUniqueConstraint().generateStatements(database)));

		return statements.toArray(new SqlStatement[statements.size()]);
	}

	@Override
	public SqlStatement[] generateRollbackStatements(Database database) throws UnsupportedChangeException, RollbackImpossibleException {

		initializeOptionalProperties(database);

		DropColumnChange dropColumn = new DropColumnChange();

		dropColumn.setSchemaName(getSchemaName());
		dropColumn.setTableName(getTableName());
		dropColumn.setColumnName(getColumnName());

		return dropColumn.generateStatements(database);

	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

}
