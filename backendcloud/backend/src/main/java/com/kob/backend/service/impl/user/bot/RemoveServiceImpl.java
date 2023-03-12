package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoveServiceImpl implements RemoveService {
    @Autowired
    private BotMapper botMapper;
    @Override
    public Map<String, String> remove(Map<String, String> data) {
        int bot_id = Integer.parseInt(data.get("bot_id"));
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        Bot bot = botMapper.selectById(bot_id);

        Map<String, String> map = new HashMap<>();
        if(bot == null)
        {
            map.put("error_message", "No such bot!");
            return map;
        }
        if(!bot.getUserId().equals(user.getId()))
        {
            map.put("error_message", "Cannot delete bot that is not yours!");
            return map;
        }
        botMapper.deleteById(bot_id);
        map.put("error_message", "success");
        return map;
    }
}
