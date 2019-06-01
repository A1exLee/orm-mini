package com.alexlee.orm.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author alexlee
 * @version 1.0
 * @date 2019/6/1 16:11
 */
public class MapperHandler implements InvocationHandler {

    private Class mappperClass;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return doExcute(method, args);
    }

    private Object doExcute(Method method, Object[] args) {
        return DBUtils.process(mappperClass,method,args);
    }

    /**
     * @return 代理类实例
     */
    public <T> T getInstance(Class<T> clz) {
        try {
            Object proxy = Proxy.newProxyInstance(clz.getClassLoader(), new Class[]{clz}, new MapperHandler());
            T obj = (T) proxy;
            this.mappperClass=clz;
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
