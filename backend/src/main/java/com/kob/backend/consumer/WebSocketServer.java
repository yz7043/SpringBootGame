package com.kob.backend.consumer;
import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.config.filter.JwtAuthenticationTokenFilter;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // Don't end with '/'
public class WebSocketServer {
    final public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>();
    final private static CopyOnWriteArraySet<User> matchPool = new CopyOnWriteArraySet<>();
    private Session session = null;
    private User user;
    //
    private static UserMapper userMapper;
    private Game game= null;
    public static RecordMapper recordMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper)
    {
        // Because this autowired variable is across threads we need to use singeleton to protect it
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper)
    {
        WebSocketServer.recordMapper = recordMapper;
    }

    private void startMatching(){
        System.out.println("Start Matching!");
        matchPool.add(this.user);
        while(matchPool.size() >= 2)
        {
            Iterator<User> it = matchPool.iterator();
            User a = it.next(), b = it.next();
            matchPool.remove(a); matchPool.remove(b);

            Game game = new Game(13, 14, 20, a.getId(), b.getId());
            game.createMap();
            game.start();
            users.get(a.getId()).game = game;
            users.get(b.getId()).game = game;


            JSONObject resGame = new JSONObject();
            resGame.put("a_id", game.getPlayerA().getId());
            resGame.put("a_sx", game.getPlayerA().getSx());
            resGame.put("a_sy", game.getPlayerA().getSy());
            resGame.put("b_id", game.getPlayerB().getId());
            resGame.put("b_sx", game.getPlayerB().getSx());
            resGame.put("b_sy", game.getPlayerB().getSy());
            resGame.put("map", game.getG());

            JSONObject respA = new JSONObject();
            respA.put("event", "start-matching");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respA.put("game", resGame);
            users.get(a.getId()).sendMessage(respA.toJSONString());

            JSONObject respB = new JSONObject();
            respB.put("event", "start-matching");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", b.getPhoto());
            respB.put("game", resGame);
            users.get(b.getId()).sendMessage(respB.toJSONString());
        }
    }
    private void stopMatching(){
        System.out.println("Stop Matching!");
        matchPool.remove(this.user);
    }



    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session;
        System.out.println("connected!");
        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if(this.user != null){
            users.put(userId, this);
        } else {
            this.session.close();
        }
        System.out.println(users);
    }

    @OnClose
    public void onClose() {
        System.out.println("disconnected!");
        if(this.user != null)
        {
            users.remove(this.user.getId());
            matchPool.remove(this.user);
        }
    }

    private void move(int direction){
        if(game.getPlayerA().getId().equals(user.getId())){
            game.setNextStepA(direction);
        }else if(game.getPlayerB().getId().equals(user.getId())){
            game.setNextStepB(direction);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("received message!");
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if("start-matching".equals(event))
        {
            startMatching();
        }else if("stap-matching".equals(event))
        {
            stopMatching();
        }else if("move".equals(event)){
            move(data.getInteger("direction"));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message)
    {
        synchronized (this.session)
        {
            try{
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}

