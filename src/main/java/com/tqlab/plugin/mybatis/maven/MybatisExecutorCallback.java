package com.tqlab.plugin.mybatis.maven;

import java.io.IOException;

/**
 * @author John Lee
 */
public interface MybatisExecutorCallback {

    /**
     * jdbc config postfix
     *
     * @return
     */
    String getJdbcConfigPostfix();

    /**
     * spring config postfix
     *
     * @return
     */
    String getSpringConfigPostfix();

    /**
     * osgi config postfix
     *
     * @return
     */
    String getOsgiConfigPostfix();

    /**
     * write jdbc config callback
     *
     * @param str
     */
    void onWriteJdbcConfig(String str);

    /**
     * write spring config callback
     *
     * @param str
     */
    void onWriteSpringConfig(String str);

    /**
     * write osgi config callback
     *
     * @param str
     */
    void onWriteOsgiConfig(String str);

    /**
     * finish callback
     *
     * @param overwrite
     * @throws IOException
     */
    void onFinish(boolean overwrite) throws IOException;
}
