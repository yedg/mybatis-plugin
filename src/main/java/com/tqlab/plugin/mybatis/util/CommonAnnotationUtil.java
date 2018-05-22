/**
 *
 */
package com.tqlab.plugin.mybatis.util;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;

/**
 * <p>
 * Update, Insert, Delete sql annotation util.
 * </p>
 *
 * @author John Lee
 */
public final class CommonAnnotationUtil {

    private CommonAnnotationUtil() {

    }

    /**
     * @param interfaze
     * @param method
     * @param hasScript
     * @param sql
     * @param clazz
     */
    public static void addAnnotation(final Interface interfaze,
                                     final Method method, final boolean hasScript, final String sql,
                                     final Class<?> clazz) {
        interfaze.addImportedType(new FullyQualifiedJavaType(clazz.getName()));
        final StringBuilder buf = new StringBuilder();
        method.addAnnotation("@" + clazz.getSimpleName() + "({");
        ScriptUtil.addScriptStart(hasScript, method);
        javaIndent(buf, 1);
        buf.append((char)'"');
        buf.append(SqlUtil.pdataFilter(sql.replace("\n", " "), hasScript));
        buf.append((char)'"');
        javaIndent(buf, 1);
        method.addAnnotation(buf.toString());
        ScriptUtil.addScriptEnd(hasScript, method);
        method.addAnnotation("})");
    }
}
