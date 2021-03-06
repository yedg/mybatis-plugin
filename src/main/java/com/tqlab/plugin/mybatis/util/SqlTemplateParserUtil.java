/**
 *
 */
package com.tqlab.plugin.mybatis.util;

import com.tqlab.plugin.mybatis.generator.*;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.dom.DefaultJavaFormatter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.types.JdbcTypeNameTranslator;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;
import java.util.Map;

/**
 * @author John Lee
 */
public final class SqlTemplateParserUtil {

    private static final Logger LOGGER = Logger.getLogger(SqlTemplateParserUtil.class);

    private static final String TABLE = "table";
    private static final String NAME = "name";
    private static final String VALUE = "value";
    private static final String OPERATION = "operation";
    private static final String ID = "id";
    private static final String MANY = "many";
    private static final String RESULT_TYPE = "resultType";
    private static final String PARAMETER_TYPE = "parameterType";
    private static final String RESULT = "result";
    private static final String OPTIONS = "options";
    private static final String OPTION = "option";
    private static final String PARAMS = "params";
    private static final String PARAM = "param";
    private static final String SQL = "sql";
    private static final String SQL_FRAGEMENT = "sqlFragment";
    private static final String COMMNET = "comment";
    private static final String OBJECT_NAME = "objectName";
    private static final String SERIAL_VERSION_UID = "serialVersionUID";
    private static final String PROPERTY = "property";
    private static final String JAVA_TYPE = "javaType";
    private static final String JAVA_PROPERTY = "javaProperty";
    private static final String JDBC_TYPE = "jdbcType";
    private static final String COLUMN = "column";
    private static final String SQL_SESSION_FACTORY = "sqlSessionFactory";
    private static final String SELECT_KEY = "selectKey";
    private static final String STATEMENT = "statement";
    private static final String KEY_PROPERTY = "keyProperty";
    private static final String KEY_COLUMN = "keyColumn";
    private static final String BEFORE = "before";
    private static final String STATEMENT_TYPE = "statementType";

    private static final String MYBATIS_XSD_LOCAL = "/com/tqlab/plugin/mybatis/tqlab-mybatis-plugin.xsd";

    private SqlTemplateParserUtil() {

    }

    private static void validate(File file) throws SAXException, ParserConfigurationException {
        // 1. Lookup a factory for the W3C XML Schema language
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        // 2. Compile the schema.
        // Here the schema is loaded from a java.io.File, but you could use
        // a java.net.URL or a javax.xml.transform.Source instead.
        Schema schema = factory.newSchema(new StreamSource(getSchemaInputStream()));

        // 3. Get a validator from the schema.
        Validator validator = schema.newValidator();

        // 4. Parse the document you want to check.
        Source source = new StreamSource(file);

        // 5. Check the document
        try {
            validator.validate(source);
        } catch (Exception ex) {
            LOGGER.error("XML validate error, file:" + file, ex);
        }
    }

    private static InputStream getSchemaInputStream() {
        return SqlTemplateParserUtil.class.getResourceAsStream(MYBATIS_XSD_LOCAL);
    }

    public static Element parseXml(String xml) {
        Document document = null;
        try {
            SAXReader reader = new SAXReader(false);
            //
            document = reader.read(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            LOGGER.error("Parse xml error. XML :" + xml, e);
        }

        if (null == document) {
            return null;
        }
        return document.getRootElement();
    }

    public static DbTable parseDbTable(File file) {

        Document document = null;
        try {
            validate(file);
            SAXReader reader = new SAXReader(false);
            //
            document = reader.read(new FileInputStream(file));
        } catch (Exception e) {
            LOGGER.error("Parse sql template file error. File :" + file, e);
        }

        if (null == document) {
            return null;
        }

        Element rootElement = document.getRootElement();
        if (!TABLE.equalsIgnoreCase(rootElement.getName())) {
            return null;
        }
        //
        DbTable table = new DbTable();
        String name = rootElement.attributeValue(NAME);

        LOGGER.info("parse table :	" + name);

        table.setName(name.toLowerCase());
        table.setSqlSessionFactory(rootElement.attributeValue(SQL_SESSION_FACTORY));
        table.setRootElement(rootElement);
        return table;
    }

    @SuppressWarnings("unchecked")
    public static DbTable parseDbTable(Context context, File file, Map<String, GeneratedJavaFile> maps) {

        LOGGER.info("start parse file :	" + file);

        DbTable table = parseDbTable(file);
        Element rootElement = table.getRootElement();
        String name = rootElement.attributeValue(NAME);

        LOGGER.info("parse table :	" + name);

        table.setName(name.toLowerCase());
        table.setSqlSessionFactory(rootElement.attributeValue(SQL_SESSION_FACTORY));
        List<Element> list = rootElement.elements();
        for (Element e : list) {
            if (COLUMN.equalsIgnoreCase(e.getName())) {
                String columnName = e.attributeValue(NAME);
                String javaProperty = e.attributeValue(JAVA_PROPERTY);
                String columnJavaType = e.attributeValue(JAVA_TYPE);

                DbColumn column = new DbColumn();
                column.setJavaType(columnJavaType);
                column.setName(columnName);
                column.setJavaProperty(javaProperty);
                table.getColumns().add(column);
            } else if (RESULT.equalsIgnoreCase(e.getName())) {
                DbSelectResult dbSelectResult = parseDbSelectResult(e, null, context, maps);
                table.getSelectResults().add(dbSelectResult);
            } else if (OPERATION.equalsIgnoreCase(e.getName())) {
                DbTableOperation operation = parseDbTableOperation(e, context, table.getSelectResults(), maps);
                if (null == operation) {
                    continue;
                }
                table.getOperations().add(operation);
            } else if (SQL_FRAGEMENT.equalsIgnoreCase(e.getName())) {
                String id = e.attributeValue(ID);
                DbSql sql = new DbSql();
                sql.setId(id);
                sql.setSql(e.getText());
                table.getSqls().put(id, sql);
            }
        }
        return table;
    }

    /**
     * Parse db table operation.
     *
     * @param e
     * @param context
     * @param results
     * @param maps
     * @return
     */
    @SuppressWarnings("unchecked")
    private static DbTableOperation parseDbTableOperation(Element e, Context context, List<DbSelectResult> results,
                                                          Map<String, GeneratedJavaFile> maps) {
        String id = e.attributeValue(ID);
        String many = e.attributeValue(MANY);
        String resultType = e.attributeValue(RESULT_TYPE);
        String parameterType = e.attributeValue(PARAMETER_TYPE);
        String sql = e.elementText(SQL);
        Element result = e.element(RESULT);
        Element comment = e.element(COMMNET);
        Element options = e.element(OPTIONS);
        Element params = e.element(PARAMS);
        Element selectKey = e.element(SELECT_KEY);

        if (null == id || null == sql) {
            return null;
        }

        DbTableOperation operation = new DbTableOperation();
        operation.setId(id);
        if (null != many) {
            operation.setMany(Boolean.parseBoolean(many));
        }
        operation.setSql(sql.replace("  ", " ").trim());
        operation.setResultType(resultType);

        if (null != comment) {
            operation.setComment(comment.getTextTrim());
        }

        if (null != result) {
            DbSelectResult dbSelectResult = parseDbSelectResult(result, resultType, context, maps);
            operation.setResult(dbSelectResult);
        } else if (null != resultType) {
            FullyQualifiedJavaType fullyQualifiedJavaType = getFullyQualifiedJavaType(context, resultType);
            for (DbSelectResult dbSelectResult : results) {
                if (dbSelectResult.getType().equals(fullyQualifiedJavaType)) {
                    operation.setResult(dbSelectResult);
                    break;
                }
            }
            //
            if (operation.getResult() == null) {
                DbSelectResult dbSelectResult = new DbSelectResult();
                dbSelectResult.setType(fullyQualifiedJavaType);
                operation.setResult(dbSelectResult);
            }
            //
            if (operation.getResult() == null) {
                DbSelectResult dbSelectResult = new DbSelectResult();
                dbSelectResult.setType(fullyQualifiedJavaType);
                operation.setResult(dbSelectResult);
            }
        }

        if (null != options && null != options.elements(OPTION)) {
            for (Element el : (List<Element>)options.elements(OPTION)) {
                String name = el.attributeValue(NAME);
                String value = el.attributeValue(VALUE);
                DbOption dbOption = new DbOption();
                dbOption.setName(name);
                dbOption.setValue(value);
                operation.addOption(dbOption);
            }
        }

        if (null != params && null != params.elements(PARAM)) {
            for (Element el : (List<Element>)params.elements(PARAM)) {
                String s = el.getTextTrim();
                int index = s.indexOf('#');
                if (index >= 0) {
                    s = s.substring(index + 2);
                } else {
                    continue;
                }

                index = s.indexOf('}');
                String parameter = s.substring(0, index);
                String[] ss = parameter.split(",");
                String objectName = null;
                FullyQualifiedJavaType type = null;
                if (ss.length == 1) {
                    objectName = s;
                    type = getFullyQualifiedJavaType(null);
                } else {
                    objectName = ss[0];
                    type = getFullyQualifiedJavaType(parseJdbcTypeName(ss[1]));
                }
                DbParam dbParam = new DbParam();
                dbParam.setObjectName(objectName);
                dbParam.setType(type);
                operation.addParams(dbParam);
            }
        } else if (null != parameterType) {
            operation.setParameterType(parameterType);
        }

        if (null != selectKey) {
            String statement = selectKey.attributeValue(STATEMENT);
            String keyProperty = selectKey.attributeValue(KEY_PROPERTY);
            String resultTypeForSelectKey = selectKey.attributeValue(RESULT_TYPE);
            String keyColumn = selectKey.attributeValue(KEY_COLUMN);
            String before = selectKey.attributeValue(BEFORE);
            String statementType = selectKey.attributeValue(STATEMENT_TYPE);
            DbSelectKey dbSelectKey = new DbSelectKey();
            dbSelectKey.setBefore(before);
            dbSelectKey.setStatement(statement);
            dbSelectKey.setKeyColumn(keyColumn);
            dbSelectKey.setResultType(resultTypeForSelectKey);
            dbSelectKey.setStatementType(statementType);
            dbSelectKey.setKeyProperty(keyProperty);
            operation.setDbSelectKey(dbSelectKey);
        }
        return operation;
    }

    /**
     * Parse db select result
     *
     * @param result
     * @param resultType
     * @param context
     * @param maps
     * @return
     */
    @SuppressWarnings("unchecked")
    private static DbSelectResult parseDbSelectResult(Element result, String resultType, Context context,
                                                      Map<String, GeneratedJavaFile> maps) {
        String objectName = result.attributeValue(OBJECT_NAME);
        DbSelectResult dbSelectResult = new DbSelectResult();
        dbSelectResult.setObjectName(objectName);
        String basicPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
        if (null != objectName) {
            String fullJavaType;
            if (objectName.indexOf('.') > 0) {
                fullJavaType = objectName;
            } else {
                fullJavaType = basicPackage + "." + objectName;
            }
            FullyQualifiedJavaType objectType = new FullyQualifiedJavaType(fullJavaType);

            dbSelectResult.setType(objectType);

            TopLevelClass topLevelClass = new TopLevelClass(objectType);
            topLevelClass.addJavaDocLine("/**");
            topLevelClass.addJavaDocLine(" * @author mybatis-generator");
            topLevelClass.addJavaDocLine(" */");

            FullyQualifiedJavaType serializable = new FullyQualifiedJavaType(Serializable.class.getName());
            topLevelClass.addImportedType(serializable);
            topLevelClass.addSuperInterface(serializable);

            Field field = new Field();
            field.setFinal(true);
            field.setInitializationString("1L");
            field.setName(SERIAL_VERSION_UID);
            field.setStatic(true);
            field.setType(new FullyQualifiedJavaType("long"));
            field.setVisibility(JavaVisibility.PRIVATE);
            topLevelClass.addField(field);

            DefaultJavaFormatter formatter = new DefaultJavaFormatter();
            formatter.setContext(context);
            GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(topLevelClass,
                context.getJavaModelGeneratorConfiguration().getTargetProject(), formatter);
            topLevelClass.setVisibility(JavaVisibility.PUBLIC);

            List<Element> properties = result.elements(PROPERTY);
            for (Element el : properties) {

                String javaType = el.attributeValue(JAVA_TYPE);
                String javaProperty = el.attributeValue(JAVA_PROPERTY);
                String column = el.attributeValue(COLUMN);
                String jdbcType = el.attributeValue(JDBC_TYPE);
                processProperty(topLevelClass, column, javaProperty, javaType);
                dbSelectResult.addDbColumn(column, javaProperty, javaType, jdbcType);
            }
            maps.put(objectType.getFullyQualifiedName(), generatedJavaFile);
        }
        // //////
        else if (null != resultType && !isBasicType(resultType)) {
            FullyQualifiedJavaType fullyQualifiedJavaType = getFullyQualifiedJavaType(context, resultType);

            List<Element> properties = result.elements(PROPERTY);
            for (Element el : properties) {
                String javaType = el.attributeValue(JAVA_TYPE);
                String javaProperty = el.attributeValue(JAVA_PROPERTY);
                String column = el.attributeValue(COLUMN);
                String jdbcType = el.attributeValue(JDBC_TYPE);
                dbSelectResult.addDbColumn(column, javaProperty, javaType, jdbcType);
            }
            dbSelectResult.setType(fullyQualifiedJavaType);
        }
        return dbSelectResult;
    }

    private static FullyQualifiedJavaType getFullyQualifiedJavaType(final Context context, final String resultType) {
        if (!isBasicType(resultType)) {
            String basicPackage = context.getJavaModelGeneratorConfiguration().getTargetPackage();
            String type = resultType;
            if (null != basicPackage && !"".equals(basicPackage) && !resultType.startsWith(basicPackage)
                && resultType.indexOf('.') == -1) {
                type = basicPackage + "." + resultType;
            }
            return new FullyQualifiedJavaType(type);
        } else {
            return new FullyQualifiedJavaType(resultType);
        }
    }

    public static boolean isBasicType(String resultType) {
        return "java.lang.Integer".equals(resultType) || "Integer".equalsIgnoreCase(resultType)
            || "int".equalsIgnoreCase(resultType) || "java.lang.Short".equals(resultType)
            || "Short".equalsIgnoreCase(resultType) || "short".equalsIgnoreCase(resultType)
            || "java.lang.Char".equals(resultType) || "Char".equalsIgnoreCase(resultType)
            || "char".equalsIgnoreCase(resultType) || "java.lang.Byte".equals(resultType)
            || "Byte".equalsIgnoreCase(resultType) || "byte".equalsIgnoreCase(resultType)
            || "java.lang.Long".equals(resultType) || "Long".equalsIgnoreCase(resultType)
            || "long".equalsIgnoreCase(resultType) || "java.lang.Float".equals(resultType)
            || "Float".equalsIgnoreCase(resultType) || "float".equalsIgnoreCase(resultType)
            || "java.lang.Double".equals(resultType) || "Double".equalsIgnoreCase(resultType)
            || "double".equalsIgnoreCase(resultType) || "java.lang.Boolean".equals(resultType)
            || "Boolean".equalsIgnoreCase(resultType) || "boolean".equalsIgnoreCase(resultType)
            || "java.lang.String".equals(resultType) || "String".equalsIgnoreCase(resultType);
    }

    private static void processProperty(final TopLevelClass topLevelClass, final String cloumn,
                                        final String javaProperty, final String javaType) {

        final FullyQualifiedJavaType type = new FullyQualifiedJavaType(javaType);

        final Field field = new Field();
        field.setName(javaProperty);
        field.setType(type);
        field.setVisibility(JavaVisibility.PRIVATE);
        addFieldComment(field, cloumn);

        topLevelClass.addField(field);
        topLevelClass.addImportedType(javaType);

        Method method = new Method();
        String methodName = javaProperty.length() > 1
            ? javaProperty.substring(0, 1).toUpperCase() + javaProperty.substring(1) : javaProperty.toUpperCase();
        method.setName("set" + methodName);
        method.addParameter(new Parameter(type, javaProperty));
        method.addBodyLine("this." + javaProperty + "=" + javaProperty + ";");
        method.setReturnType(new FullyQualifiedJavaType("void"));
        method.setVisibility(JavaVisibility.PUBLIC);
        addGeneralMethodComment(method, cloumn);
        topLevelClass.addMethod(method);

        method = new Method();
        method.setName("get" + methodName);
        method.setReturnType(type);
        method.addBodyLine("return " + javaProperty + ";");
        method.setVisibility(JavaVisibility.PUBLIC);
        addGeneralMethodComment(method, cloumn);
        topLevelClass.addMethod(method);
    }

    private static void addFieldComment(Field field, String cloumn) {

        StringBuilder sb = new StringBuilder();

        field.addJavaDocLine("/**");
        field.addJavaDocLine(
            " * This field was generated by MyBatis Generator plugin (SqlTemplatePlugin).");

        sb.append(" * This field corresponds to the database table ");
        sb.append(cloumn);
        field.addJavaDocLine(sb.toString());

        addJavadocTag(field, false);

        field.addJavaDocLine(" */");
    }

    private static void addGeneralMethodComment(Method method, String cloumn) {

        StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**");
        method.addJavaDocLine(
            " * This method was generated by MyBatis Generator plugin (SqlTemplatePlugin).");
        sb.append(" * This method corresponds to the database table ");
        sb.append(cloumn);
        method.addJavaDocLine(sb.toString());

        addJavadocTag(method, false);

        for (Parameter p : method.getParameters()) {
            method.addJavaDocLine(" * @param " + p.getName());
        }

        if (null != method.getReturnType()) {
            method.addJavaDocLine(" * @return " + method.getReturnType());
        }

        method.addJavaDocLine(" */");
    }

    public static void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {

    }

    /**
     * @param jdbcTypeName
     * @return
     */
    public static FullyQualifiedJavaType getFullyQualifiedJavaType(final String jdbcTypeName) {
        String jdbcType = jdbcTypeName;
        if (null == jdbcType || "".equals(jdbcType.trim())) {
            jdbcType = "JAVA_OBJECT";
        }
        final int type = JdbcTypeNameTranslator.getJdbcType(jdbcType);
        switch (type) {
            case Types.TINYINT: {
                return new FullyQualifiedJavaType(Byte.class.getName());
            }
            case Types.SMALLINT: {
                return new FullyQualifiedJavaType(Short.class.getName());
            }
            case Types.INTEGER: {
                return new FullyQualifiedJavaType(Integer.class.getName());
            }
            case Types.BIGINT: {
                return new FullyQualifiedJavaType(Long.class.getName());
            }
            case Types.DECIMAL: {
                return new FullyQualifiedJavaType(BigDecimal.class.getName());
            }
            case Types.NUMERIC:
            case Types.DOUBLE: {
                return new FullyQualifiedJavaType(Double.class.getName());
            }
            case Types.FLOAT: {
                return new FullyQualifiedJavaType(Float.class.getName());
            }
            case Types.CHAR:
            case Types.NCHAR:
            case Types.VARCHAR:
            case Types.NVARCHAR:
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR: {
                return FullyQualifiedJavaType.getStringInstance();
            }
            case Types.BIT:
            case Types.BOOLEAN: {
                return FullyQualifiedJavaType.getBooleanPrimitiveInstance();
            }
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.DATE: {
                return FullyQualifiedJavaType.getDateInstance();
            }
            case Types.ARRAY: {
                return new FullyQualifiedJavaType("java.util.List<?>");
            }
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.BLOB: {
                return new FullyQualifiedJavaType("byte[]");
            }
            case Types.JAVA_OBJECT:
            default: {
                return new FullyQualifiedJavaType(Object.class.getName());
            }
        }
    }

    public static String parseJdbcTypeName(final String str) {
        final String jdbcTypeName = str.replace("jdbcType", "").replace("=", "").trim();
        return jdbcTypeName;
    }
}
