import com.alexlee.orm.entity.User;
import com.alexlee.orm.service.UserService;
import com.alibaba.fastjson.JSONArray;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * @author alexlee
 * @version 1.0
 * @date 2019/5/31 17:40
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class JdbcTest {

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserService userService;

    @Test
    public void test01() {
        List<User> users = jdbcTemplate.query("select id,name,age from user", new BeanPropertyRowMapper<>(User.class));
        System.out.println(JSONArray.toJSON(users));
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setId("alexlee");
        user.setName("alexlee");
        user.setAge(18);
        userService.insert(user);
    }


}
