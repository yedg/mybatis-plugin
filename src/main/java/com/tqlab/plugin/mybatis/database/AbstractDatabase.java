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

import com.tqlab.plugin.mybatis.MybatisPluginException;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * @author John Lee
 */
public abstract class AbstractDatabase implements Database {

    protected static final Logger LOGGER = Logger.getLogger(AbstractDatabase.class);

    private String driverClass;
    private String database;
    private String url;
    private Properties properties;
    /**
     * Database connection
     */
    private transient Connection conn;

    public AbstractDatabase() {

    }

    /**
     * @param driverClass
     * @param database
     * @param url
     * @param properties
     */
    public AbstractDatabase(final String driverClass, final String database, final String url,
                            final Properties properties) {
        this.driverClass = driverClass;
        this.database = database;
        this.url = url;
        this.properties = properties;
    }

    /**
     * @return the driverClass
     */
    @Override
    public final String getDriverClass() {
        return driverClass;
    }

    /**
     * @param driverClass the driverClass to set
     */
    public final void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public final void setDatabase(String database) {
        this.database = database;
    }

    public final String getUrl() {
        return url;
    }

    public final void setUrl(String url) {
        this.url = url;
    }

    public final Properties getProperties() {
        return properties;
    }

    public final void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * @return
     * @throws SQLException
     */
    protected Connection getConnection() throws SQLException {
        try {
            if (null == conn || conn.isClosed()) {
                Class.forName(driverClass);
                Properties p = new Properties();
                buildProperties(p);
                conn = DriverManager.getConnection(url, p);
            }

        } catch (ClassNotFoundException e) {
            throw new MybatisPluginException(e);
        }
        return conn;
    }

    @Override
    public Set<String> getTablesName() {

        final Set<String> set = new HashSet<String>();

        Statement stmt = null;
        ResultSet res = null;
        try {
            final Connection conn = this.getConnection();
            stmt = conn.createStatement();
            res = stmt.executeQuery(getTablesQuerySql());
            while (res.next()) {
                //
                set.add(getTableName(res));
            }
        } catch (SQLException e) {
            LOGGER.error(e);
        } finally {
            releaseDbQuery(stmt, res);
        }
        return set;
    }

    protected void releaseDbQuery(Statement stmt, ResultSet res) {
        try {
            if (res != null) { res.close(); }
        } catch (Exception ex) {
            LOGGER.error(ex);
        }

        try {
            if (stmt != null) { stmt.close(); }
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * Filter the database keyword
     *
     * @param columnName
     * @return
     */
    protected abstract String getColumnName(String columnName);

    /**
     * Get columns query sql
     *
     * @param tableName
     * @return
     */
    protected abstract String getColumnsQuerySql(String tableName);

    /**
     * Get tables query sql
     *
     * @return
     */
    protected abstract String getTablesQuerySql();

    /**
     * Get table name from resultSet and filter the database keyword
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    protected abstract String getTableName(ResultSet resultSet) throws SQLException;

    @Override
    public String getDatabase() {
        return this.database;
    }

    /**
     * close
     */
    @Override
    public void close() {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * build jdbc Properties
     *
     * @param p
     */
    protected void buildProperties(Properties p) {
        for (Entry<Object, Object> e : properties.entrySet()) {
            if (e.getValue() instanceof String) {
                p.put(e.getKey(), e.getValue());
            }
        }
    }
}
