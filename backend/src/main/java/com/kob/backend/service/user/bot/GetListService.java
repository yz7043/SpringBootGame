package com.kob.backend.service.user.bot;

import com.kob.backend.pojo.Bot;

import java.util.List;

public interface GetListService {
    /* get current user's bot, user information is in the login info*/
    List<Bot> getList();
}
