package com.alexlee.orm.entity;

import lombok.Data;

import java.util.List;

/**
 * @author alexlee
 * @version 1.0
 * @date 2019/6/1 16:18
 */
@Data
public class MapperDefinition<T> {

    private Class mapperClass;
    private List<QueryRule> rules;
    //    参数先限制，只能传对应实体
    private Class<T> entityClass;
    @Data
    public class QueryRule {

        private String id;
        private DMLType type;
        private List<?> args;
        private String sql;

    }

    public enum DMLType {
        SELECT("select"), INSERT("insert"), UPDATE("update"), DELETE("delete");
        private String type;

        DMLType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }
    }

}
