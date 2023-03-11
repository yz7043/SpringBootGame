package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Record;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    private final Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing"; // playing -> finished
    private String loser = ""; // all, A, B

    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB){
        this.rows = rows; this.cols = cols; this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        playerA = new Player(idA, this.rows - 2, 1, new ArrayList<>());
        playerB = new Player(idB, 1, this.cols - 2,  new ArrayList<>());
    }

    public int[][] getG(){
        return g;
    }

    public Player getPlayerA(){
        return playerA;
    }

    public Player getPlayerB(){
        return playerB;
    }

    public void setNextStepA(Integer nextStepA){
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB){
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty){
        if(sx == tx && sy == ty) return true;
        g[sx][sy] = 1;
        for(int i = 0; i < 4; i++){
            int x = sx + dx[i]; int y = sy + dy[i];
            if(x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0){
                if(check_connectivity(x, y, tx, ty)){
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }
        g[sx][sy] = 0;
        return false;
    }

    private boolean draw(){
        // generate the map
        for(int i = 0; i < rows; i++)
            Arrays.fill(g[i], 0);
        for(int i = 0; i < rows; i++)
            g[i][0] = g[i][this.cols-1] = 1;

        for(int i = 0; i < cols; i++)
            g[0][i] = g[this.rows-1][i] = 1;

        Random random = new Random();
        for(int i = 0; i < this.inner_walls_count / 2; i++){
            for(int j = 0; j < 1000; j++){
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);
                if(g[r][c] == 1 || g[this.rows-1-r][this.cols-1-c] == 1) continue;
                // avoid occupying rebirth point
                if(r == this.rows-2 && c == 1 || c == this.cols-2 && r == 1) continue;
                g[r][c] = g[this.rows-1-r][this.cols-1-c] = 1;
                break;
            }
        }
        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap(){
        for(int i = 0; i < 1000; i++){
            if(draw()) break;
        }
    }

    private boolean nextStep(){
        // wait two players finish their round
        try {
            // We need time to render movement in the frontend;
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < 50; i++)
        {
            try {
                Thread.sleep(100);
                lock.lock();
                try{
                    if(nextStepA != null && nextStepB != null){
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                        lock.unlock();
                }
            } catch (InterruptedException e){
                throw  new RuntimeException(e);
            }
        }
        return false;
    }


    private boolean checkValid(List<Cell> cellsA, List<Cell> cellsB){
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        if(g[cell.x][cell.y] == 1) return false;
        // check if snake eats itself
        for(int i = 0; i < n- 1; i++){
            if(cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                return false;
        }
        for(int i = 0; i < n- 1; i++){
            if(cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                return false;
        }
        return true;
    }

    private void judge(){
        // check if game is running
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();
        boolean validA = checkValid(cellsA, cellsB);
        boolean validB = checkValid(cellsB, cellsA);
        if(!validA || !validB){
            status = "finished";
            if(!validA && !validB){
                loser = "all";
            } else if (!validA){
                loser = "A";
            } else {
                loser = "B";
            }
        }
    }

    private void sendMove(){
        // send movement to clients
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            sendAllMessage(resp.toJSONString());
            nextStepA = nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    private void sendResult(){
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    private void sendAllMessage(String message){
        WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private String getMapString(){
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++)
                res.append(g[i][j]);
        }
        return res.toString();
    }

    private void saveToDatabase(){

        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }

    @Override
    public void run() {
        for(int i = 0; i < 1000; i++){
            if(nextStep()){
                judge();
                if(status.equals("playing")){
                    sendMove();
                } else {
                    sendResult();
                    break;
                }

            } else {
                status = "finished";
                lock.lock();
                try {
                    if (nextStepA == null && nextStepB == null) {
                        loser = "all";
                    } else if (nextStepA == null) {
                        loser = "A";
                    } else {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
