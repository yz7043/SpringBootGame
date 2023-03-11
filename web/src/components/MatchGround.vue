<template>
    <div class="matchground">
        <div class="row">
            <div class="col-6">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="user-username">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-6">
                <div class="user-photo">
                    <img :src="$store.state.battle.opponent_photo" alt="">
                </div>
                <div class="user-username">
                    {{ $store.state.battle.opponent_username }}
                </div>
            </div>
            <div class="col-12" style="text-align: center; padding-top: 10vh;">
                <button type="button" class="btn btn-warning btn-lg" @click="click_match_button">{{ match_btn_info }}</button>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue';
import { useStore } from 'vuex';
export default {
    components: {

    },
    setup(){
        const store = useStore();
        let match_btn_info = ref("Start Matching");
        const click_match_button = () => {
            if(match_btn_info.value === "Start Matching")
            {
                match_btn_info.value = "Cancel";
                store.state.battle.socket.send(JSON.stringify({
                    event: "start-matching",
                }));
            }
            else{
                match_btn_info.value = "Start Matching";
                store.state.battle.socket.send(JSON.stringify({
                    event: "stop-matching",
                }))
            }
        }
        return {
            match_btn_info,
            click_match_button,
        }
    }
}
</script>

<style scoped>
div.matchground{
    width: 60vw;
    height: 70vh;
    margin: 40px auto;
    background-color: rgba(50, 50, 50, 0.5);
}
div.user-photo{
    text-align: center;
    padding-top: 10vh;
}
div.user-photo > img{
    border-radius: 50%;
    max-width: 50%;  
}
div.user-username {
    text-align: center;
    font-size: 24px;
    font-weight: 600;
    color: white;
    padding-top: 2vh;
}
</style>