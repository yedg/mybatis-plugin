/**
 *
 */
package com.tqlab.plugin.mybatis.generator;

import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;

/**
 * @author John Lee
 */
public interface GeneratorCallback {

    /**
     * Generator callback
     *
     * @param interfaze
     * @param method
     */
    void addAnnotatedResults(final Interface interfaze,
                             final Method method);
}
