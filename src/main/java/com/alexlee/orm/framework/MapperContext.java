package com.alexlee.orm.framework;

import com.alexlee.orm.entity.MapperDefinition;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.management.AttributeNotFoundException;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * class Mapper context.
 *
 * @author alexlee
 * @version 1.0
 * @ 2019 /6/1 17:00
 */
@Component
public class MapperContext {
    private static Map<Class, MapperDefinition> mapperContainer = Maps.newHashMap();

    /**
     * The constant XML_MAPPER_PROPERTY_NAMESPACE.
     */
    private static final String XML_MAPPER_PROPERTY_NAMESPACE = "namespace";
    /**
     * The constant XML_MAPPER_PROPERTY_ENTITY_CLASS.
     */
    private static final String XML_MAPPER_PROPERTY_ENTITY_CLASS = "entityClass";
    /**
     * The constant XML_MAPPER_PROPERTY_ID.
     */
    private static final String XML_MAPPER_PROPERTY_ID = "id";
    /**
     * The constant XML_MAPPER_ELEMENT_INSERT.
     */
    private static final String XML_MAPPER_ELEMENT_INSERT = "insert";
    /**
     * The constant XML_MAPPER_ELEMENT_SELECT.
     */
    private static final String XML_MAPPER_ELEMENT_SELECT = "select";
    /**
     * The constant XML_MAPPER_ELEMENT_UPDATE.
     */
    private static final String XML_MAPPER_ELEMENT_UPDATE = "update";
    /**
     * The constant XML_MAPPER_ELEMENT_DELETE.
     */
    private static final String XML_MAPPER_ELEMENT_DELETE = "delete";

    private static Pattern pattern = Pattern.compile("\\#\\{(.+?)}");

    /**
     * 初始化加载mapper定义到内存
     */
    @PostConstruct
    public void init() {
        readAllMapperDefinitions();
    }

    /**
     * 读取所有的mapper定义
     */
    private static void readAllMapperDefinitions() {
        try {
            ClassPathResource mappers = new ClassPathResource("/mappers");
            File file = new File(mappers.getURI().getPath());
            if (!file.exists()) {
                return;
            }
            if (!file.isDirectory()) {
                return;
            }
            File[] mapperFiles = file.listFiles();
            if (mapperFiles == null || mapperFiles.length == 0) {
                return;
            }
            for (File mapperFile : mapperFiles) {
                MapperDefinition definition = readMapperDefinition(mapperFile);
                if (definition != null) {
                    mapperContainer.put(definition.getMapperClass(), definition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取单个mapper定义
     *
     * @param mapperFile xml文件
     * @return mapper定义
     */
    private static MapperDefinition readMapperDefinition(File mapperFile) {
        try {
            if (mapperFile == null || !mapperFile.exists() || !mapperFile.isFile()) {
                return null;
            }
            MapperDefinition mapperDefinition = new MapperDefinition();
//            解析XML
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(mapperFile);
            Element root = document.getRootElement();
            String namespace = root.attributeValue(XML_MAPPER_PROPERTY_NAMESPACE);
            if (StringUtils.isEmpty(namespace)) {
                throw new AttributeNotFoundException("namespace not exist");
            }
            Class<?> namespaceClass = Class.forName(namespace);
            mapperDefinition.setMapperClass(namespaceClass);
            String entity = root.attributeValue(XML_MAPPER_PROPERTY_ENTITY_CLASS);
            if (StringUtils.isEmpty(entity)) {
                throw new AttributeNotFoundException("entityClass not exist");
            }
            Class<?> entityClass = Class.forName(entity);
            mapperDefinition.setEntityClass(entityClass);
            List<MapperDefinition.QueryRule> queryRules = Lists.newArrayList();
            queryRules.addAll(generateQueryRule(mapperDefinition, root, XML_MAPPER_ELEMENT_SELECT, MapperDefinition.DMLType.SELECT));
            queryRules.addAll(generateQueryRule(mapperDefinition, root, XML_MAPPER_ELEMENT_INSERT, MapperDefinition.DMLType.INSERT));
            queryRules.addAll(generateQueryRule(mapperDefinition, root, XML_MAPPER_ELEMENT_UPDATE, MapperDefinition.DMLType.UPDATE));
            queryRules.addAll(generateQueryRule(mapperDefinition, root, XML_MAPPER_ELEMENT_DELETE, MapperDefinition.DMLType.DELETE));
            mapperDefinition.setRules(queryRules);
            return mapperDefinition;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private static List<MapperDefinition.QueryRule> generateQueryRule(MapperDefinition mapperDefinition, Element root, String elementType, MapperDefinition.DMLType dmlType) {
        List<Element> selects = root.elements(elementType);
        return selects.stream().map(element -> {
            MapperDefinition.QueryRule queryRule = mapperDefinition.new QueryRule();
            String id = element.attributeValue(XML_MAPPER_PROPERTY_ID);
            String sql = element.getTextTrim();
            queryRule.setId(id);
            queryRule.setType(dmlType);
            queryRule.setArgs(extractArgs(sql));
            queryRule.setSql(replaceStub(sql));
            return queryRule;
        }).collect(Collectors.toList());
    }

    private static String replaceStub(String sql) {
        Matcher matcher = pattern.matcher(sql);
        int i = 0;
        while (matcher.find()) {
            sql = sql.replace("#{" + matcher.group(1) + "}", "{" + i + "}");
            i++;
        }
        return sql;
    }

    /**
     * 从sql中占位符获取参数列表
     *
     * @param sql
     * @return 参数列表
     */
    private static List<?> extractArgs(String sql) {
        List param = Lists.newArrayList();
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            param.add(matcher.group(1));
        }
        return param;
    }

    public static MapperDefinition getMapperDefinition(Class clazz) {
        return mapperContainer.get(clazz);
    }

    public static void main(String[] args) {
        readAllMapperDefinitions();
    }


}
