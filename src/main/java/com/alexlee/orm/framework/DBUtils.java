package com.alexlee.orm.framework;

import com.alexlee.orm.entity.MapperDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author alexlee
 * @version 1.0
 * @date 2019/6/1 22:00
 */
@Component
public class DBUtils {
    @Autowired
    private static JdbcTemplate jdbcTemplate;

    //    暂时只支持一个参数和没有参数，参数必须是配置的实体类类型
    public static Object process(Class clazz, Method method, Object[] args) throws Exception {

        MapperDefinition definition = MapperContext.getMapperDefinition(clazz);
        if (args.length > 1) {
            throw new IllegalArgumentException("argument too much");
        }
        if (args.length == 1) {
            if (!args.getClass().getName().equals(definition.getEntityClass())) {
                throw new IllegalArgumentException("argument type not match");
            }
        }
        List<MapperDefinition.QueryRule> rules = definition.getRules();
        Object result = null;
        for (MapperDefinition.QueryRule rule : rules) {
            if (method.getName().equals(rule.getId())) {
                result = doExecute(rule, args, definition.getEntityClass());
                break;
            }
        }
        return result;
    }

    private static Object doExecute(MapperDefinition.QueryRule rule, Object[] args, Class entityClass) throws Exception {
        Object result = null;
        List<String> argNames = rule.getArgs();
        Object[] object = new Object[argNames.size()];
        Field[] declaredFields = entityClass.getDeclaredFields();
        if (argNames.size() > 0) {
            for (int i = 0; i < argNames.size(); i++) {
                for (Field declaredField : declaredFields) {
                    if (declaredField.getName().equals(argNames.get(i))) {
                        declaredField.setAccessible(true);
                        object[i] = declaredField.get(args[0]);
                        break;
                    }
                }
            }
        }
        switch (rule.getType()) {
            case SELECT:
                result = doSelect(rule, object, entityClass);
                break;
            case INSERT:
            case UPDATE:
            case DELETE:
                result = doUpdate(rule, object);
                break;
        }
        return result;

    }


    private static Object doUpdate(MapperDefinition.QueryRule rule, Object[] args) {
        return jdbcTemplate.update(MessageFormat.format(rule.getSql(), args));
    }

    private static Object doSelect(MapperDefinition.QueryRule rule, Object[] args, Class returnType) {
        return jdbcTemplate.query(MessageFormat.format(rule.getSql(), args), new BeanPropertyRowMapper<>(returnType));
    }
}
