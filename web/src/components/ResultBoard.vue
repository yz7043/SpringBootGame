<template>
    <div class="result-board">
        <div class="result-board-text" v-if="$store.state.battle.loser === 'all'">
            Draw
        </div>
        <div class="result-board-text" v-else-if="$store.state.battle.loser==='A' && $store.state.battle.a_id === parseInt($store.state.user.id)">
            Lose
        </div>
        <div class="result-board-text" v-else-if="$store.state.battle.loser==='B' && $store.state.battle.b_id === parseInt($store.state.user.id)">
            Lose
        </div>
        <div class="result-board-text" v-else>
            Win
        </div>
        <div class="result-board-btn">
            <button type="button" class="btn btn-warning btn-lg" @click="restart">
                Rematch
            </button>
        </div>
    </div>
</template>

<script>
import { useStore } from 'vuex';
export default{
    setup(){
        const store = useStore();
        const restart = () => {
            store.commit("updateStatus", "matching");
            store.commit("updateLoser", "none");
            store.commit("updateOpponent", {username: "opponent", photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"});
        }
        return {
            restart,
        }
    }
}
</script>

<style scoped>
div.result-board{
    height: 30vh;
    width: 30vw;
    background-color: rgba(50, 50, 50, 0.5);
    position: absolute;
    top: 30vh;
    left: 35vw;
}
div.result-board-text{
    text-align: center;
    color: white;
    font-size: 50px;
    font-weight: 600;
    font-style: italic;
    padding-top: 5vh;
}
div.result-board-btn{
    text-align: center;
    padding-top: 7vh;
}
</style>
