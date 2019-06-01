package com.alexlee.orm.dao;

import com.alexlee.orm.entity.User;
import com.alexlee.orm.framework.MapperHandler;
import com.alexlee.orm.mapper.UserMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author alexlee
 * @version 1.0
 * @date 2019/6/1 16:32
 */
@Component
public class UserDao {

    private UserMapper userMapper;

    public UserMapper getUserMapper() {
        if (userMapper == null) {
            userMapper = new MapperHandler().getInstance(UserMapper.class);
        }
        return userMapper;
    }

    public void insert(User user) {
        getUserMapper().insert(user);
    }

    public List<User> selectAll() {
        return getUserMapper().selectAll();
    }

    public List<User> selectByCondition(User user) {
        return getUserMapper().selectByUser(user);
    }

    public void update(User user) {
        getUserMapper().update(user);
    }

    public void delete(User user) {
        getUserMapper().delete(user);
    }

}
