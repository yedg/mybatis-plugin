/**
 *
 */
package com.tqlab.plugin.mybatis.generator.ext;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.codegen.mybatis3.javamapper.AnnotatedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.SqlProviderGenerator;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author lijun
 */
public class MybatisAnnotatedClientGenerator extends AnnotatedClientGenerator {

    public MybatisAnnotatedClientGenerator() {
        super();
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.17", //$NON-NLS-1$
            introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        Interface interfaze = new Interface(type);

        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * @author mybatis-generator");
        interfaze.addJavaDocLine(" */");

        interfaze.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(interfaze);

        String rootInterface = introspectedTable.getTableConfigurationProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        if (!stringHasValue(rootInterface)) {
            rootInterface = context.getJavaClientGeneratorConfiguration()
                .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
        }

        if (stringHasValue(rootInterface)) {
            FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(rootInterface);
            interfaze.addSuperInterface(fqjt);
            interfaze.addImportedType(fqjt);
        }

        // addCountByExampleMethod(interfaze);
        // addDeleteByExampleMethod(interfaze);
        addDeleteByPrimaryKeyMethod(interfaze);
        addInsertMethod(interfaze);
        addInsertSelectiveMethod(interfaze);
        // addSelectByExampleWithBLOBsMethod(interfaze);
        // addSelectByExampleWithoutBLOBsMethod(interfaze);
        addSelectByPrimaryKeyMethod(interfaze);
        // addUpdateByExampleSelectiveMethod(interfaze);
        // addUpdateByExampleWithBLOBsMethod(interfaze);
        // addUpdateByExampleWithoutBLOBsMethod(interfaze);
        addUpdateByPrimaryKeySelectiveMethod(interfaze);
        addUpdateByPrimaryKeyWithBLOBsMethod(interfaze);
        addUpdateByPrimaryKeyWithoutBLOBsMethod(interfaze);

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
        if (context.getPlugins().clientGenerated(interfaze, null, introspectedTable)) {
            answer.add(interfaze);
        }

        List<CompilationUnit> extraCompilationUnits = getExtraCompilationUnits();
        if (extraCompilationUnits != null) {
            answer.addAll(extraCompilationUnits);
        }

        return answer;
    }

    public List<CompilationUnit> getExtraCompilationUnits() {
        boolean useLegacyBuilder = false;

        String prop = context.getJavaClientGeneratorConfiguration()
            .getProperty(PropertyRegistry.CLIENT_USE_LEGACY_BUILDER);
        if (StringUtility.stringHasValue(prop)) {
            useLegacyBuilder = Boolean.valueOf(prop);
        }
        SqlProviderGenerator sqlProviderGenerator = new MybatisSqlProviderGenerator(useLegacyBuilder);
        sqlProviderGenerator.setContext(context);
        sqlProviderGenerator.setIntrospectedTable(introspectedTable);
        sqlProviderGenerator.setProgressCallback(progressCallback);
        sqlProviderGenerator.setWarnings(warnings);
        return sqlProviderGenerator.getCompilationUnits();
    }
}
