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
 */
public enum DatabaseEnum {

    /**
     * HSQLDB
     */
    HSQLDB("HSQLDB", "CALL IDENTITY()"),

    /**
     *
     */
    MYSQL("MySql", "SELECT LAST_INSERT_ID()"),

    /**
     * OceanBase
     */
    OB("OB", "");

    /**
     *
     */
    private static final Map<String, DatabaseEnum> CACHE = new ConcurrentHashMap<String, DatabaseEnum>();

    static {
        for (DatabaseEnum databaseEnum : DatabaseEnum.values()) {
            CACHE.put(databaseEnum.name.toLowerCase(), databaseEnum);
        }
    }

    private String name;
    private String sqlStatement;

    DatabaseEnum(String name, String sqlStatement) {
        this.name = name;
        this.sqlStatement = sqlStatement;
    }

    /**
     * @return the sqlStatement
     */
    public final String getSqlStatement() {
        return sqlStatement;
    }

    /**
     * @param name
     * @return
     */
    public final static DatabaseEnum getDatabaseEnum(String name) {
        if (null == name) {
            return null;
        }
        return CACHE.get(name.toLowerCase());
    }
}
