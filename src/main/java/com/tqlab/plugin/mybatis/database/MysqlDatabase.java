/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.tqlab.plugin.mybatis.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author John Lee
 */
public class MysqlDatabase extends AbstractDatabase {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String GRAVE = "`";

    /**
     * @param database
     * @param url
     * @param properties
     */
    public MysqlDatabase(final String database, final String url, final Properties properties) {
        super(DRIVER, database, url, properties);
    }

    @Override
    protected String getTablesQuerySql() {
        return "show tables;";
    }

    @Override
    protected String getTableName(final ResultSet resultSet) throws SQLException {
        String name = (String)resultSet.getObject(1);
        return name;
    }

    @Override
    public DatabaseEnum getDatabaseEnum() {
        return DatabaseEnum.MYSQL;
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

            List<String> autoIncrementColumn = new ArrayList<String>();
            while (res.next()) {
                String column = getColumnName(res.getString("Field"));
                column = column.replaceAll("`", "");
                columns.add(column);
                if ("auto_increment".equalsIgnoreCase(res.getString("Extra"))) {
                    autoIncrementColumn.add(column);
                }

                if ("PRI".equalsIgnoreCase(res.getString("Key"))) {
                    if (autoIncrementColumn.contains(column)) {
                        autoIncrementPK.add(column);
                    }
                    primaryKeys.add(column);
                }
            }

            res.close();
        } catch (Exception e) {
            LOGGER.error("Error", e);
        } finally {
            releaseDbQuery(stmt, res);
        }

        return result;
    }

    @Override
    protected String getColumnsQuerySql(final String table) {
        if (null == table) {
            return null;
        }
        String tableName = table;
        if (!tableName.startsWith(GRAVE)) {
            tableName = GRAVE + tableName;
        }
        if (!tableName.endsWith(GRAVE)) {
            tableName = tableName + GRAVE;
        }
        return "SHOW COLUMNS FROM " + tableName + "";
    }

    @Override
    protected String getColumnName(final String column) {
        if (null == column) {
            return null;
        }
        String columnName = column;
        if (!columnName.startsWith(GRAVE)) {
            columnName = GRAVE + columnName;
        }
        if (!columnName.endsWith(GRAVE)) {
            columnName = columnName + GRAVE;
        }
        return columnName;
    }

    @Override
    protected void buildProperties(Properties p) {
        super.buildProperties(p);
        p.put("nullCatalogMeansCurrent", true);
    }
}
