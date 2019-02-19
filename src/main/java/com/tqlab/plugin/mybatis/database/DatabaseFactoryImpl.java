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
import org.apache.commons.lang.StringUtils;

import java.util.Properties;
import java.util.ServiceLoader;

/**
 * @author John Lee
 */
public class DatabaseFactoryImpl implements DatabaseFactory {

    @Override
    public Database getDatabase(final DatabaseEnum databaseEnum,
                                final String database, final String url,
                                final Properties properties, final String driver) {

        AbstractDatabase result = null;
        switch (databaseEnum) {
            case MYSQL: {
                result = new MysqlDatabase(database, url, properties);
                break;
            }
            case HSQLDB: {
                result = new HsqldbDatabase(database, url, properties);
                break;
            }
            default: {
                //
                result = load();
                break;
            }
        }
        if (null != result) {
            if (StringUtils.isNotBlank(driver)) {
                result.setDriverClass(driver);
            }

            result.setUrl(url);
            result.setProperties(properties);
            result.setDatabase(database);

            return result;
        }
        throw new MybatisPluginException("database " + databaseEnum.name()
            + " not supported.");
    }

    /**
     * Creates a new service with service loader.
     *
     * @return
     */
    private AbstractDatabase load() {
        ServiceLoader<Database> serviceLoader = ServiceLoader.load(Database.class, Database.class.getClassLoader());
        for (Database service : serviceLoader) {
            return (AbstractDatabase)service;
        }
        return null;
    }
}
