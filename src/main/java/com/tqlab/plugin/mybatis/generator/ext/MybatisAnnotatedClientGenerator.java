/**
 * 
 */
package com.tqlab.plugin.mybatis.generator.ext;

import java.util.List;

import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.codegen.mybatis3.javamapper.AnnotatedClientGenerator;
import org.mybatis.generator.codegen.mybatis3.javamapper.SqlProviderGenerator;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * @author lijun
 *
 */
public class MybatisAnnotatedClientGenerator extends AnnotatedClientGenerator {

	public MybatisAnnotatedClientGenerator(){
		super();
	}
	
	
	public List<CompilationUnit> getExtraCompilationUnits() {
    	boolean useLegacyBuilder = false;
    	
    	String prop = context.getJavaClientGeneratorConfiguration().getProperty(PropertyRegistry.CLIENT_USE_LEGACY_BUILDER);
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
