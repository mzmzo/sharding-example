package com.example.sharding;

import com.example.sharding.entity.Order;
import com.example.sharding.entity.User;
import com.example.sharding.repository.OrderRepository;
import com.example.sharding.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShardingApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(ShardingApplicationTests.class);

    @Resource
    private OrderRepository orderRepository;

    @Resource
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testShardingById() {
        orderRepository.deleteAll();

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Order order = new Order();
            order.setUserId(51);
            order.setStatus("INSERT_TEST");
            orders.add(order);
        }
        orderRepository.save(orders);
        logger.info("****** all={},t_order_0={}", orderRepository.count(), orderRepository.findByIdMod().size());
    }

    @Test
    public void testShardingByEseId() {
        User user = new User();
        user.setEseId(1L);
        user.setName("TEST_ESE_1");
        user.setGender(1);

        User user2 = new User();
        user2.setEseId(2L);
        user2.setName("TEST_ESE_2");
        user2.setGender(1);

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);

        userRepository.save(users);
    }

    @Test
    public void testShardingByEseIdFindSave() {
        User user = userRepository.findUserByIdAndEseId(1L, -1L);
        user.setGender(19);

//        List<User> users = new ArrayList<>();
//        users.add(user);

        userRepository.save(user);
    }


    @Test
    public void testFindByGender() {
        List<User> users = userRepository.findByGender(1);
        logger.info("males={}", users.toString());
    }

    @Test
    public void testFindByPage() {
        Page<User> users = userRepository.findAll(new PageRequest(2, 2));
        logger.info("all user ={}", users.getContent().toString());
    }

    @Test
    public void testFindBySpecWithPage() {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicate = new ArrayList<>();

            predicate.add(cb.equal(root.get("gender").as(Integer.class), 1));

            Predicate[] parr = new Predicate[predicate.size()];
            return cb.and(predicate.toArray(parr));
        };
        Page<User> users = userRepository.findAll(spec, new PageRequest(0, 10));

        logger.info("规格模式分页查询 users={}", users.getContent().toString());

        userRepository.findAll(spec, new PageRequest(8, 10));
    }

    @Test
    public void findByJdbcTemplate() {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sql.append("SELECT * FROM t_user WHERE ese_id = 1 ");

        String countSql = "select count(*) u from (" + sql + " ) u ";

        Integer count = jdbcTemplate.queryForObject(countSql, params.toArray(), Integer.class);
        logger.info("findByJdbcTemplate count={}", count);

        sql.append("ORDER BY id OFFSET 0 ROW FETCH NEXT 10 ROWS ONLY");

        List<User> list = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(User.class));
        logger.info("findByJdbcTemplate list={}", list.toString());
    }


}
