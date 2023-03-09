<template>
<ContentField>My Bot</ContentField>
</template>

<script>
import ContentField from "@/components/ContentField.vue";
import $ from "jquery";
import { useStore } from "vuex";
export default {
    components: {
        ContentField
    },
    setup(){
        const store = useStore();
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/add/",
        //     type: "POST",
        //     data: {
        //         title: "Bot title 3",
        //         description: "HHHHHH 3",
        //         content: "Code for bot 3"
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp)
        //     {
        //         console.log(resp);
        //     }
        // })
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/remove/",
        //     type: "POST",
        //     data: {
        //         "bot_id": 1,
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp)
        //     {
        //         console.log(resp);
        //     },
        //     error(resp)
        //     {
        //         console.log(resp);
        //     }
        // })
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/update/",
        //     type: "POST",
        //     data:
        //     {
        //         "bot_id": 3,
        //         title: "Bot title New",
        //         description: "HHHHHH New",
        //         content: "Code for bot New"
        //     },
        //     headers: {
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(res)
        //     {
        //         console.log(res);
        //     },
        //     error(res)
        //     {
        //         console.log(res);
        //     }
        // })
        $.ajax({
            url: "http://127.0.0.1:3000/user/bot/getlist/",
            type: "GET",
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(res)
            {
                console.log(res);
            },
            error(res)
            {
                console.log(res);
            }
        })
    }
}
</script>

<style scoped></style>