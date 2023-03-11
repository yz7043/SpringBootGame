import { GameObject } from "./GameObject";
import { Wall } from "@/assets/scripts/Wall";
import { Snake, SNAKE_STATUS } from "./Snake";

export class GameMap extends GameObject{
    constructor(ctx, parent, store){
        super();
        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;
        this.rows = 13;
        this.cols = 14;
        this.store = store;

        this.walls = []
        this.inner_walls_count = 20;
        this.innerWalls = []
        this.snakes = [
            new Snake({id: 0, color: "#4876EC", r: this.rows - 2, c: 1}, this), 
            new Snake({id: 1, color: "#F94848", r: 1, c: this.cols - 2}, this)
        ];
    }
    
    start(){
        this.create_walls();
        this.add_listening_events();
    }

    update(){
        this.update_map();
        if(this.check_ready()){
            this.next_step();
        }
        this.render();
    }

    render(){
        const color_even = "#AAD751", color_odd = "#A2D149";
        for(let r = 0; r < this.rows; r++){
            for(let c = 0; c < this.cols; c++){
                if((r + c) % 2 ==0){
                    this.ctx.fillStyle = color_even;
                }else{
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }

    update_map(){
        this.L = parseInt(Math.min(this.parent.clientWidth / this.rows, this.parent.clientHeight / this.cols));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    dfs(r, c, st){
        let dr = [1, 0, -1, 0];
        let dc = [0, 1, 0, -1];
        st[r][c] = true;
        for(let i = 0; i < 4; i++){
            let newR = dr[i] + r;
            let newC = dc[i] + c;
            if(newR >= 0 && newR < this.rows && newC >= 0 && newC < this.cols){
                if(!st[newR][newC]){
                    this.dfs(newR, newC, st);
                }
            }
        }
    }

    create_walls(){
        const g = this.store.state.battle.game_map;
        for(let r = 0; r < this.rows; r++){
            for(let c = 0; c < this.cols; c++){
                if(g[r][c]){
                    this.walls.push(new Wall(r, c, this));
                }
            }
        } 
        return true;
    }

    check_ready(){
        for(const snake of this.snakes){
            // hasn't finished moving
            if(snake.status !== SNAKE_STATUS.IDLE) return false;
            // no command is issued
            if(snake.direction === -1) return false;
        }
        return true;
    }

    next_step(){
        for(const snake of this.snakes){
            snake.next_step();
        }    
    }

    add_listening_events(){
        // let outer = this;
        this.ctx.canvas.focus();
        this.ctx.canvas.addEventListener("keydown", e => {
            let d = -1;
            if(e.key === "w"){
                d = 0;
            }else if(e.key === "d"){
                d = 1; 
            }else if(e.key === "s"){
                d = 2;
            }else if(e.key === "a"){
                d = 3;
            }
            if(d >= 0){
                this.store.state.battle.socket.send(JSON.stringify({
                    event: "move",
                    direction: d,
                }))
            }
        });
    }
    
    check_valid(cell){
        for(const wall of this.walls){
            if(wall.r == cell.r && wall.c == cell.c) return false;
        }
        for(const snake of this.snakes){
            let k = snake.cells.length;
            if(!snake.check_tail_increasing()){
                // if tail doesn't grow, head won't touch tail after moving
                k--;
            }
            for(let i = 0; i < k; i++){
                if(snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                    return false;
            }
        }
        return true;
    }
}