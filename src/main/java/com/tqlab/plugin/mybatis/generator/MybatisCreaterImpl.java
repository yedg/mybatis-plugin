/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tqlab.plugin.mybatis.generator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.DefaultShellCallback;

import com.tqlab.plugin.mybatis.database.ColumnResult;
import com.tqlab.plugin.mybatis.database.Database;
import com.tqlab.plugin.mybatis.database.DatabaseEnum;
import com.tqlab.plugin.mybatis.generator.config.Config;
import com.tqlab.plugin.mybatis.util.Constants;
import com.tqlab.plugin.mybatis.util.TableUtil;

/**
 * @author John Lee
 * 
 */
public class MybatisCreaterImpl implements MybatisCreater {

	private static final Logger LOGGER = Logger.getLogger(MybatisCreaterImpl.class);
	private Config config;
	private Properties properties;

	public MybatisCreaterImpl(Properties properties) {
		this.config = new Config(properties);
		this.properties = properties;
	}

	public List<MybatisBean> create(final Database database, final String jdbcUrl, final String doSuffix,
			final String doRootClass, final String databaseName, final String userName, final String password,
			final String dalPackage, final String outputDir, final boolean overwrite,
			final Map<String, DbTable> dbTables, final String... tableNames) {

		List<String> tableList = new ArrayList<String>();
		for (String s : tableNames) {
			if (s.indexOf(":") > 0) {
				String alias = s.substring(s.indexOf(":") + 1);
				String name = s.substring(0, s.indexOf(":"));
				tableList.add(name);
				TableHolder.addTable(name, alias);
			} else {
				TableHolder.addTable(s, s);
				tableList.add(s);
			}
		}

		String tables[] = tableList.toArray(new String[0]);

		String url = jdbcUrl;
		if (StringUtils.isNotBlank(url) && url.contains("&") && !url.contains("&amp;")) {
			url = url.replace("&", "&amp;");
		}

		String dir = outputDir.replace(File.separator, "/");
		final String java = dir + "/src/main/java/";
		final String res = dir + "/src/main/resources/";

		File f = new File(java);
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(res);
		if (!f.exists()) {
			f.mkdirs();
		}

		StringBuffer buf = new StringBuffer();

		for (final String name : tables) {
			buf.append(getTableString(database.getDatabaseEnum(), database, name));
		}
		database.close();

		final StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("<!DOCTYPE generatorConfiguration");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(" PUBLIC \"-//mybatis.org//DTD MyBatis Generator Configuration 1.0//EN\"");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(" \"http://mybatis.org/dtd/mybatis-generator-config_1_0.dtd\">");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("<generatorConfiguration>");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("  <context id=\"" + databaseName
				+ "\" targetRuntime=\"com.tqlab.plugin.mybatis.generator.ext.MyBatisIntrospectedTableImpl\" defaultModelType=\"hierarchical\">");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(Constants.LINE_SEPARATOR);

		sb.append("    <property name=\"javaFileEncoding\" value=\"UTF-8\" />");
		sb.append(Constants.LINE_SEPARATOR);
		if (StringUtils.isNotBlank(doSuffix)) {
			sb.append("    <property name=\"doSuffix\" value=\"" + doSuffix + "\" />");
			sb.append(Constants.LINE_SEPARATOR);
		}

		sb.append("    <plugin type=\"org.mybatis.generator.plugins.SerializablePlugin\" />");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("    <plugin type=\"com.tqlab.plugin.mybatis.generator.SqlTempleatePluginAdapter\" >");
		sb.append(Constants.LINE_SEPARATOR);
		if (null != properties) {
			Set<Entry<Object, Object>> set = properties.entrySet();
			for (Iterator<Entry<Object, Object>> i = set.iterator(); i.hasNext();) {
				Entry<Object, Object> e = i.next();
				sb.append("      <property name=\"" + e.getKey() + "\" value=\"" + e.getValue() + "\" />");
				sb.append(Constants.LINE_SEPARATOR);
			}
		}
		sb.append("    </plugin>");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("    <commentGenerator type=\"com.tqlab.plugin.mybatis.generator.ext.MybatisCommentGenerator\">");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("      <property name=\"suppressDate\" value=\"true\" />");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("      <property name=\"addRemarkComments\" value=\"true\" />");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("    </commentGenerator>");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("    <jdbcConnection driverClass=\"" + database.getDriverClass() + "\"");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("      connectionURL=\"" + url + "\"");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("      userId=\"" + userName + "\"");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("      password=\"" + password + "\">");
		sb.append(Constants.LINE_SEPARATOR);
		if (null != properties) {
			Set<Entry<Object, Object>> set = properties.entrySet();
			for (Iterator<Entry<Object, Object>> i = set.iterator(); i.hasNext();) {
				Entry<Object, Object> e = i.next();
				sb.append("      <property name=\"" + e.getKey() + "\" value=\"" + e.getValue() + "\" />");
				sb.append(Constants.LINE_SEPARATOR);
			}
		}
		sb.append("      <property name=\"nullCatalogMeansCurrent\" value=\"true\" />");
		sb.append(Constants.LINE_SEPARATOR);

		sb.append("    </jdbcConnection>");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("    <javaTypeResolver >");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("      <property name=\"forceBigDecimals\" value=\"false\" />");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("    </javaTypeResolver>");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("    <javaModelGenerator targetPackage=\"" + dalPackage + ".dataobject" + "\" targetProject=\"" + java
				+ "\">");

		if (StringUtils.isNotBlank(doRootClass)) {
			sb.append(Constants.LINE_SEPARATOR);
			sb.append("      <property name=\"rootClass\" value=\"" + doRootClass + "\" />");
			sb.append(Constants.LINE_SEPARATOR);
		}
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("      <property name=\"enableSubPackages\" value=\"true\" />");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("      <property name=\"trimStrings\" value=\"true\" />");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("    </javaModelGenerator>");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("    <sqlMapGenerator targetPackage=\"sqlmaps\"  targetProject=\"" + res + "\">");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("      <property name=\"enableSubPackages\" value=\"true\" />");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("    </sqlMapGenerator>");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(
				"    <javaClientGenerator  type=\"com.tqlab.plugin.mybatis.generator.ext.MybatisAnnotatedClientGenerator\" targetPackage=\""
						+ dalPackage + ".dao\"" + " targetProject=\"" + java + "\">");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("      <property name=\"enableSubPackages\" value=\"true\" />");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("    </javaClientGenerator >");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("    <!-- tables -->");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(buf.toString());
		sb.append("    <!-- tables end -->");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("  </context>");
		sb.append(Constants.LINE_SEPARATOR);
		sb.append("</generatorConfiguration>");
		sb.append(Constants.LINE_SEPARATOR);

		LOGGER.info("###################################################################");
		LOGGER.info(Constants.LINE_SEPARATOR + Constants.LINE_SEPARATOR + sb.toString() + Constants.LINE_SEPARATOR
				+ Constants.LINE_SEPARATOR);
		LOGGER.info("###################################################################");

		InputStream is = null;

		try {
			is = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			LOGGER.error("data get btyes error.", e1);
			return null;
		}

		List<String> warnings = new ArrayList<String>();
		try {
			ConfigurationParser cp = new ConfigurationParser(warnings);
			Configuration config = cp.parseConfiguration(is);

			DefaultShellCallback shellCallback = new DefaultShellCallback(overwrite);
			MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, shellCallback, warnings);
			myBatisGenerator.generate(null);

			List<MybatisBean> myList = new ArrayList<MybatisBean>();
			for (String s0 : tables) {
				String alias = TableHolder.getTableAlias(s0);
				String s = TableUtil.getTableName(alias);
				String temp = TableUtil.getObjectName(s, this.config.getTablePrefix());
				String beanId = temp.substring(0, 1).toLowerCase() + temp.substring(1) + "Mapper";
				MybatisBean mybatisBean = new MybatisBean();
				mybatisBean.setBeanId(beanId);
				mybatisBean.setBeanName(beanId);
				DbTable dbTable = dbTables.get(alias);
				if (null == dbTable) {
					dbTable = dbTables.get(alias);
				}
				mybatisBean.setSqlSessionFactory(null == dbTable ? null : dbTable.getSqlSessionFactory());
				mybatisBean.setClassPath(dalPackage + ".dao." + temp + "Mapper");
				myList.add(mybatisBean);
			}

			LOGGER.info("##############################################################");
			LOGGER.info("Create completely");
			LOGGER.info("##############################################################");
			return myList;
		} catch (XMLParserException e) {
			List<String> errors = e.getErrors();
			for (String s : errors) {
				LOGGER.error(s);
			}
			LOGGER.error(e);
		} catch (SQLException e) {
			LOGGER.error(e);
		} catch (IOException e) {
			LOGGER.error(e);
		} catch (InterruptedException e) {
			LOGGER.error(e);
		} catch (InvalidConfigurationException e) {
			LOGGER.error(e);
		} finally {
			TableHolder.clear();
		}

		return null;
	}

	private String getTableString(final DatabaseEnum dbEnum, final Database database, final String tableName) {

		LOGGER.info("getTableString >>> " + TableHolder.getTableAlias(tableName));

		final ColumnResult result = database.getColumns(tableName);
		final StringBuffer buf = new StringBuffer(300);
		buf.append("    <table ");
		buf.append("tableName=\"");
		buf.append(tableName);
		buf.append("\" ");
		buf.append("domainObjectName=\"");
		String objectName = TableUtil.getObjectName(TableHolder.getTableAlias(tableName), this.config.getTablePrefix());

		buf.append(objectName);
		buf.append("\" ");
		buf.append(" ");
		buf.append("mapperName=\"");
		buf.append(objectName);
		buf.append("Mapper");
		buf.append("\" ");

		buf.append(" ");
		buf.append("sqlProviderName=\"");
		buf.append(objectName);
		buf.append("SqlProvider");
		buf.append("\" ");

		buf.append("enableSelectByPrimaryKey=\"true\" ");
		buf.append("enableUpdateByPrimaryKey=\"true\" ");
		buf.append("enableDeleteByPrimaryKey=\"true\" ");
		buf.append("escapeWildcards=\"true\" ");
		buf.append("enableSelectByExample=\"false\" ");
		buf.append("enableDeleteByExample=\"false\" ");
		buf.append("enableCountByExample=\"false\" ");
		buf.append("enableUpdateByExample=\"false\">");
		buf.append(Constants.LINE_SEPARATOR);

		final List<String> list = result.getAutoIncrementPrimaryKeys();
		for (String key : list) {
			buf.append("      <generatedKey column=\"");
			buf.append(key);
			buf.append("\" ");
			buf.append("sqlStatement=\"");
			buf.append(dbEnum.getSqlStatement());
			buf.append("\" ");
			buf.append("identity=\"true\" />");
			buf.append(Constants.LINE_SEPARATOR);
		}
		buf.append("    </table>");
		buf.append(Constants.LINE_SEPARATOR);
		buf.append(Constants.LINE_SEPARATOR);
		return buf.toString();
	}

}
