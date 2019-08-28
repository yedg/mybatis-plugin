package com.tqlab.plugin.mybatis.generator.ext;

import com.tqlab.plugin.mybatis.generator.TableHolder;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.codegen.AbstractJavaGenerator;
import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.codegen.mybatis3.model.ExampleGenerator;
import org.mybatis.generator.codegen.mybatis3.model.PrimaryKeyGenerator;
import org.mybatis.generator.codegen.mybatis3.model.RecordWithBLOBsGenerator;

import java.util.List;

/**
 * @author lijun
 */
public class MyBatisIntrospectedTableImpl extends IntrospectedTableMyBatis3Impl {

    @Override
    protected void calculateXmlAttributes() {
        super.calculateXmlAttributes();
        setUpdateByPrimaryKeyWithBLOBsStatementId("updateByPrimaryKeyWithBlobs");
        setUpdateByExampleWithBLOBsStatementId("updateByExampleWithBlobs");
        setSelectByExampleWithBLOBsStatementId("selectByExampleWithBlobs");
    }

    @Override
    protected void calculateModelAttributes() {
        String pakkage = calculateJavaModelPackage();

        StringBuilder sb = new StringBuilder();
        sb.append(pakkage);
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append(filterDoSuffix());
        sb.append("Key");
        setPrimaryKeyType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append(getDoSuffix());
        setBaseRecordType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append(filterDoSuffix());
        sb.append("WithBlobs");
        setRecordWithBLOBsType(sb.toString());

        sb.setLength(0);
        sb.append(pakkage);
        sb.append('.');
        sb.append(fullyQualifiedTable.getDomainObjectName());
        sb.append(filterDoSuffix());
        sb.append("Example");
        setExampleType(sb.toString());
    }

    /**
     * Calculate java model generators.
     *
     * @param warnings         the warnings
     * @param progressCallback the progress callback
     */
    @Override
    protected void calculateJavaModelGenerators(List<String> warnings, ProgressCallback progressCallback) {
        if (getRules().generateExampleClass()) {
            AbstractJavaGenerator javaGenerator = new ExampleGenerator();
            initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            javaModelGenerators.add(javaGenerator);
        }

        if (getRules().generatePrimaryKeyClass()) {
            AbstractJavaGenerator javaGenerator = new PrimaryKeyGenerator();
            initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            javaModelGenerators.add(javaGenerator);
        }

        if (getRules().generateBaseRecordClass()) {
            AbstractJavaGenerator javaGenerator = new MybatisBaseRecordGenerator();
            initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            javaModelGenerators.add(javaGenerator);
        }

        if (getRules().generateRecordWithBLOBsClass()) {
            AbstractJavaGenerator javaGenerator = new RecordWithBLOBsGenerator();
            initializeAbstractGenerator(javaGenerator, warnings, progressCallback);
            javaModelGenerators.add(javaGenerator);
        }
    }

    @Override
    public String getFullyQualifiedTableNameAtRuntime() {
        String tableNameAtRuntime = super.getFullyQualifiedTableNameAtRuntime();
        if (null != TableHolder.getTableAlias(tableNameAtRuntime)) {
            tableNameAtRuntime = TableHolder.getTableAlias(tableNameAtRuntime);
        }
        return tableNameAtRuntime;
    }

    @Override
    public String getAliasedFullyQualifiedTableNameAtRuntime() {
        return getFullyQualifiedTableNameAtRuntime();
    }

    private String filterDoSuffix() {
        String doSuffix = getDoSuffix();
        if (doSuffix.length() > 1) {
            StringBuffer buf = new StringBuffer();
            buf.append(doSuffix.substring(0, 1));
            buf.append(doSuffix.substring(1).toLowerCase());
            doSuffix = buf.toString();
        }
        return doSuffix;
    }

    /**
     * @return the doSuffix
     */
    public final String getDoSuffix() {
        String doSuffix = this.getContext().getProperty("doSuffix");
        if (StringUtils.isBlank(doSuffix)) {
            return "";
        }

        return doSuffix;
    }
}
