package com.example.sharding.service;

import com.example.sharding.entity.User;
import com.example.sharding.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DemoService {

    @Resource
    private UserRepository userRepository;

    /**
     * 需求修改--实际新增 3.0.0.M4
     *
     * 通过id+eseId（EnterpriseShardingAlgorithm）查询后再修改，调用save方法后，数据库新增一条数据
     * SimpleJpaRepository-> entityInformation.isNew(entity) 判断也是修改操作
     */
    public void update() {
        User user = userRepository.findUserByIdAndEseId(1L, -1L);
        user.setGender(19);

//        List<User> users = new ArrayList<>();
//        users.add(user);

        userRepository.save(user);
    }
}
