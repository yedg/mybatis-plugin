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

import java.util.Set;

/**
 * @author John Lee
 */
public interface Database {

    /**
     * Table names
     *
     * @return
     */
    Set<String> getTablesName();

    /**
     * Columns
     *
     * @param tableName
     * @return
     */
    ColumnResult getColumns(String tableName);

    /**
     * Release connection
     */
    void close();

    /**
     * Driver class
     *
     * @return
     */
    String getDriverClass();

    /**
     * Database name
     *
     * @return
     */
    String getDatabase();

    /**
     * DatabaseEnum
     *
     * @return
     */
    DatabaseEnum getDatabaseEnum();

}
