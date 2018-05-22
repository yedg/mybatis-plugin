/**
 *
 */
package com.tqlab.plugin.mybatis.generator.ext;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.codegen.mybatis3.javamapper.SqlProviderGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author lijun
 */
public class MybatisSqlProviderGenerator extends SqlProviderGenerator {

    public MybatisSqlProviderGenerator(boolean useLegacyBuilder) {
        super(useLegacyBuilder);
    }

    @Override
    public List<CompilationUnit> getCompilationUnits() {
        progressCallback.startTask(getString("Progress.18",
            introspectedTable.getFullyQualifiedTable().toString()));
        CommentGenerator commentGenerator = context.getCommentGenerator();

        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
            introspectedTable.getMyBatis3SqlProviderType());
        TopLevelClass topLevelClass = new TopLevelClass(type);
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        commentGenerator.addJavaFileComment(topLevelClass);

        boolean addApplyWhereMethod = false;
        addApplyWhereMethod |= addCountByExampleMethod(topLevelClass);
        addApplyWhereMethod |= addDeleteByExampleMethod(topLevelClass);
        addInsertSelectiveMethod(topLevelClass);
        addApplyWhereMethod |= addSelectByExampleWithBLOBsMethod(topLevelClass);
        addApplyWhereMethod |= addSelectByExampleWithoutBLOBsMethod(topLevelClass);
        addApplyWhereMethod |= addUpdateByExampleSelectiveMethod(topLevelClass);
        addApplyWhereMethod |= addUpdateByExampleWithBLOBsMethod(topLevelClass);
        addApplyWhereMethod |= addUpdateByExampleWithoutBLOBsMethod(topLevelClass);
        addUpdateByPrimaryKeySelectiveMethod(topLevelClass);

        if (addApplyWhereMethod) {
            addApplyWhereMethod(topLevelClass);
        }

        List<CompilationUnit> answer = new ArrayList<CompilationUnit>();

        if (topLevelClass.getMethods().size() > 0 &&
            context.getPlugins().providerGenerated(topLevelClass, introspectedTable)) {
            answer.add(topLevelClass);
        }

        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * @author mybatis-generator");
        topLevelClass.addJavaDocLine(" */");

        return answer;
    }
}
