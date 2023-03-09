package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confirmedPassword) {
        Map<String, String> map = new HashMap<>();
        if(username == null)
        {
            map.put("error_message", "Username cannot be empty!");
            return map;
        }
        if(password == null || confirmedPassword == null)
        {
            map.put("error_message", "Password cannot be empty!");
            return map;
        }
        username = username.trim();
        if(username.length() == 0)
        {
            map.put("error_message", "Username cannot be empty!");
            return map;
        }
        if(username.length() > 100)
        {
            map.put("error_message", "Username cannot be longer than 100!");
            return map;
        }
        if(password.length() == 0 || confirmedPassword.length() == 0)
        {
            map.put("error_message", "Password cannot be empty!");
            return map;
        }
        if(password.length() > 100 || confirmedPassword.length() > 100)
        {
            map.put("error_message", "Password cannot be longer than 100!");
            return map;
        }
        if(!password.equals(confirmedPassword))
        {
            map.put("error_message", "Two passwords don't match!");
            return map;
        }
        QueryWrapper<User> queryWapper = new QueryWrapper<>();
        queryWapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWapper);
        if(!users.isEmpty())
        {
            map.put("error_message", "Username already exists!");
            return map;
        }
        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg";
        User user = new User(null, username, encodedPassword, photo);
        userMapper.insert(user);
        map.put("error_message", "success");
        return map;
    }
}