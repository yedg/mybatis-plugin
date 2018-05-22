/**
 *
 */
package com.tqlab.plugin.mybatis.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author John Lee
 */
public class HsqldbDatabase extends AbstractDatabase {

    private static final String DRIVER = "org.hsqldb.jdbc.JDBCDriver";
    private static final String LEFT_SQUARE_BRACKETS = "[";
    private static final String RIGHT_SQUARE_BRACKETS = "]";

    /**
     * @param database
     * @param url
     * @param properties
     */
    public HsqldbDatabase(final String database, final String url,
                          final Properties properties) {
        super(DRIVER, database, url, properties);
    }

    @Override
    protected String getTablesQuerySql() {
        return "SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_TYPE='TABLE';";
    }

    @Override
    protected String getTableName(ResultSet resultSet) throws SQLException {
        String name = (String)resultSet.getObject("TABLE_NAME");
        if (null != name) {
            name = name.toLowerCase();
        }
        return name;
    }

    @Override
    public DatabaseEnum getDatabaseEnum() {
        return DatabaseEnum.HSQLDB;
    }

    @Override
    protected String getColumnsQuerySql(String tableName) {
        if (null == tableName) {
            return null;
        }
        return "SELECT * FROM " + tableName + " LIMIT 1";
    }

    @Override
    protected String getColumnName(final String column) {

        if (null == column) {
            return null;
        }

        String columnName = column;
        if (!columnName.startsWith(LEFT_SQUARE_BRACKETS)) {
            columnName = LEFT_SQUARE_BRACKETS + columnName;
        }
        if (!columnName.endsWith(RIGHT_SQUARE_BRACKETS)) {
            columnName = columnName + RIGHT_SQUARE_BRACKETS;
        }

        return columnName;
    }

    @Override
    public ColumnResult getColumns(final String tableName) {

        final ColumnResult result = new ColumnResult();

        final List<String> columns = new ArrayList<String>();
        final List<String> primaryKeys = new ArrayList<String>();
        final List<String> autoIncrementPK = new ArrayList<String>();

        result.setTableName(tableName);
        result.setColumns(columns);
        result.setAutoIncrementPrimaryKeys(autoIncrementPK);
        result.setPrimaryKeys(primaryKeys);

        Statement stmt = null;
        ResultSet res = null;
        try {
            final Connection conn = this.getConnection();
            stmt = conn.createStatement();
            final String sql = getColumnsQuerySql(tableName);
            res = stmt.executeQuery(sql);
            ResultSetMetaData rsmd = res.getMetaData();
            // 取得全部列数
            int colcount = rsmd.getColumnCount();

            List<String> autoIncrementColumn = new ArrayList<String>();
            for (int i = 1; i <= colcount; i++) {
                final String column = getColumnName(rsmd.getColumnName(i));
                columns.add(column);
                // Indicates whether the designated column is automatically
                // numbered.
                if (rsmd.isAutoIncrement(i)) {
                    autoIncrementColumn.add(column);
                }
            }

            res.close();

            DatabaseMetaData dbmd = conn.getMetaData();
            res = dbmd.getPrimaryKeys(null, null, tableName);
            while (res.next()) {
                String primaryKey = res.getString("COLUMN_NAME");
                if (autoIncrementColumn.contains(primaryKey)) {
                    autoIncrementPK.add(primaryKey);
                }
                primaryKeys.add(primaryKey);
            }
        } catch (Exception e) {
            LOGGER.error("Error", e);
        } finally {
            releaseDbQuery(stmt, res);
        }

        return result;
    }

}