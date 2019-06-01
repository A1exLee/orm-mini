package com.alexlee.orm.service;

import com.alexlee.orm.dao.UserDao;
import com.alexlee.orm.entity.User;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author alexlee
 * @version 1.0
 * @date 2019/6/1 22:47
 */
@Service
public class UserService {
    @Autowired
    private UserDao userDao;

    public List<User> queryUser() {
        return userDao.selectAll();
    }

    public List<User> queryUserByCondition(User user) {
        return userDao.selectByCondition(user);
    }

    public void insert(User user) {
        userDao.insert(user);
    }

    public void update(User user) {
        userDao.update(user);
    }

    public void delete(User user) {
        userDao.delete(user);
    }

}
