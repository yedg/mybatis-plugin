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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author John Lee
 *
 */
public enum DatabaseEnum {

    /**
     * Cloudscape
     */
    CLOUDSCAPE("Cloudscape"),

    /**
     * DB2
     */
    DB2("DB2"),

    /**
     * DB2_MF
     */
    DB2_MF("DB2_MF"),

    /**
     * Derby
     */
    DERBY("Derby"),

    /**
     * HSQLDB
     */
    HSQLDB("HSQLDB"),

    /**
     *
     */
    MYSQL("MySql"),

    /**
     * SqlServer
     */
    SQLSERVER("SqlServer"),

    /**
     * SYBASE
     */
    SYBASE("SYBASE"),

    /**
     * JDBC
     */
    DEFAULT("JDBC");

    /**
     *
     */
    private static final Map<String, DatabaseEnum> CACHE = new ConcurrentHashMap<String, DatabaseEnum>();

    static {
        for (DatabaseEnum databaseEnum : DatabaseEnum.values()) {
            CACHE.put(databaseEnum.sqlStatement.toLowerCase(), databaseEnum);
        }
    }

    private String sqlStatement;

    private DatabaseEnum(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    /**
     * @return the sqlStatement
     */
    public final String getSqlStatement() {
        return sqlStatement;
    }

    /**
     * @param sqlStatement
     *            the sqlStatement to set
     */
    public final void setSqlStatement(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    /**
     *
     * @param sqlStatement
     * @return
     */
    public final static DatabaseEnum getDatabaseEnum(String sqlStatement) {
        if (null == sqlStatement) {
            return null;
        }
        return CACHE.get(sqlStatement.toLowerCase());
    }
}
