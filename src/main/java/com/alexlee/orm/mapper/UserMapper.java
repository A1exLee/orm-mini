package com.alexlee.orm.mapper;

import com.alexlee.orm.entity.User;

import java.util.List;

/**
 * @author alexlee
 * @version 1.0
 * @date 2019/6/1 15:46
 */
public interface UserMapper {
    void insert(User user);

    List<User> selectAll();

    List<User> selectByUser(User user);

    void update(User user);

    void delete(User user);
}
