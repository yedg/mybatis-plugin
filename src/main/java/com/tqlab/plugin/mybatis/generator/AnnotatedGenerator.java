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
package com.tqlab.plugin.mybatis.generator;

import com.tqlab.plugin.mybatis.util.CommonAnnotationUtil;
import com.tqlab.plugin.mybatis.util.ResultAnnotationUtil;
import com.tqlab.plugin.mybatis.util.SelectAnnotationUtil;
import com.tqlab.plugin.mybatis.util.SqlTemplateParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.AbstractJavaMapperMethodGenerator;

import java.util.Iterator;
import java.util.List;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;

/**
 * @author John Lee
 */
public class AnnotatedGenerator extends AbstractJavaMapperMethodGenerator {

    private static final String COMMA = ",";

    public AnnotatedGenerator() {
        super();
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {

    }

    public void addMapperAnnotations(final Interface interfaze, final Method method, final DbSelectResult result,
                                     final Statement statement, final boolean hasScript, final String sql) {

        if (statement instanceof Delete) {
            CommonAnnotationUtil.addAnnotation(interfaze, method, hasScript, sql,
                org.apache.ibatis.annotations.Delete.class);
        } else if (statement instanceof Update) {
            CommonAnnotationUtil.addAnnotation(interfaze, method, hasScript, sql,
                org.apache.ibatis.annotations.Update.class);
        } else if (statement instanceof Insert) {
            CommonAnnotationUtil.addAnnotation(interfaze, method, hasScript, sql,
                org.apache.ibatis.annotations.Insert.class);
        } else if (statement instanceof Select) {
            final GeneratorCallback generator = new GeneratorCallback() {

                @Override
                public void addAnnotatedResults(final Interface interfaze, final Method method) {
                    AnnotatedGenerator.this.addAnnotatedResults(interfaze, method, result);
                }

            };
            SelectAnnotationUtil.addSelectAnnotation(introspectedTable, interfaze, method, hasScript, sql, generator);
        }

    }

    private void addAnnotatedResults(final Interface interfaze, final Method method, final DbSelectResult result) {
        boolean checkResult = null != result
            && ((null != result.getColumns() && result.getColumns().size() > 0) || (isBasicType(result)));
        if (checkResult) {
            this.doAddAnnotatedResults(interfaze, method, result);
        } else {
            this.doAddAnnotatedResults(interfaze, method);
        }
    }

    private boolean isBasicType(final DbSelectResult result) {
        FullyQualifiedJavaType type = result.getType();
        return SqlTemplateParserUtil.isBasicType(type.getFullyQualifiedName());
    }

    /**
     * @param interfaze
     * @param method
     */
    private void doAddAnnotatedResults(final Interface interfaze, final Method method) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.type.JdbcType"));

        if (introspectedTable.isConstructorBased()) {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Arg"));
            interfaze.addImportedType(
                new FullyQualifiedJavaType("org.apache.ibatis.annotations.ConstructorArgs"));
            method.addAnnotation("@ConstructorArgs({");
        } else {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Result"));
            interfaze.addImportedType(
                new FullyQualifiedJavaType("org.apache.ibatis.annotations.Results"));
            method.addAnnotation("@Results({");
        }

        StringBuilder sb = new StringBuilder();

        Iterator<IntrospectedColumn> iterPk = introspectedTable.getPrimaryKeyColumns().iterator();
        Iterator<IntrospectedColumn> iterNonPk = introspectedTable.getNonPrimaryKeyColumns().iterator();
        while (iterPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterPk.next();
            sb.setLength(0);

            javaIndent(sb, 1);
            sb.append(getResultAnnotation(interfaze, introspectedColumn, true, introspectedTable.isConstructorBased()));

            if (iterPk.hasNext() || iterNonPk.hasNext()) {
                sb.append(',');
            }

            method.addAnnotation(sb.toString());
        }

        while (iterNonPk.hasNext()) {

            IntrospectedColumn introspectedColumn = iterNonPk.next();
            sb.setLength(0);

            javaIndent(sb, 1);
            sb.append(
                getResultAnnotation(interfaze, introspectedColumn, false, introspectedTable.isConstructorBased()));

            if (iterNonPk.hasNext()) {
                sb.append(COMMA);
            }

            method.addAnnotation(sb.toString());
        }

        List<String> annotations = method.getAnnotations();
        // remove last item and Check whether the comma at the end
        String s = annotations.remove(annotations.size() - 1);
        if (s.endsWith(COMMA)) {
            s = s.substring(0, s.length() - 1);
        }
        method.getAnnotations().add(s);
        method.addAnnotation("})");
    }

    private void doAddAnnotatedResults(final Interface interfaze, final Method method, final DbSelectResult result) {
        if (null == result || null == result.getColumns() || result.getColumns().size() == 0) {
            return;
        }
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Result"));
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Results"));
        method.addAnnotation("@Results({");
        List<DbColumn> list = result.getColumns();
        StringBuilder sb = new StringBuilder();
        for (Iterator<DbColumn> i = list.iterator(); i.hasNext(); ) {
            sb.setLength(0);
            javaIndent(sb, 1);
            DbColumn column = i.next();
            sb.append(ResultAnnotationUtil.getResultAnnotation(interfaze, column.getName(), column.getJavaProperty(),
                column.getJdbcType()));
            if (i.hasNext()) {
                sb.append(COMMA);
            }
            method.addAnnotation(sb.toString());
        }
        method.addAnnotation("})");
    }
}
