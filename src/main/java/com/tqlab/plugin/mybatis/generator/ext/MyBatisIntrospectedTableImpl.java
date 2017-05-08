package com.tqlab.plugin.mybatis.generator.ext;

import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;

import com.tqlab.plugin.mybatis.generator.TableHolder;

/**
 * 
 * @author lijun
 *
 */
public class MyBatisIntrospectedTableImpl extends IntrospectedTableMyBatis3Impl {

	protected void calculateModelAttributes() {
		String pakkage = calculateJavaModelPackage();

		StringBuilder sb = new StringBuilder();
		sb.append(pakkage);
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("Key"); //$NON-NLS-1$
		setPrimaryKeyType(sb.toString());

		sb.setLength(0);
		sb.append(pakkage);
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		setBaseRecordType(sb.toString());

		sb.setLength(0);
		sb.append(pakkage);
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("WithBlobs"); //$NON-NLS-1$
		setRecordWithBLOBsType(sb.toString());

		sb.setLength(0);
		sb.append(pakkage);
		sb.append('.');
		sb.append(fullyQualifiedTable.getDomainObjectName());
		sb.append("Example"); //$NON-NLS-1$
		setExampleType(sb.toString());
	}

	public String getFullyQualifiedTableNameAtRuntime() {
		String tableNameAtRuntime = super.getFullyQualifiedTableNameAtRuntime();
		if (null != TableHolder.getTableAlias(tableNameAtRuntime)) {
			tableNameAtRuntime = TableHolder.getTableAlias(tableNameAtRuntime);
		}
		return tableNameAtRuntime;
	}

	public String getAliasedFullyQualifiedTableNameAtRuntime() {
		return getFullyQualifiedTableNameAtRuntime();
	}
}
