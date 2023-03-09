import { createRouter, createWebHistory } from 'vue-router'
import BattleIndexView from "../views/battle/BattleIndexView";
import RanklistIndexView from "../views/ranklist/RanklistIndexView";
import RecordIndexView  from "../views/record/RecordIndexView";
import UserBotIndexView from "../views/user/bots/UserBotIndexView";
import NotFount from "../views/error/NotFount";
import UserAccountLoginView from "../views/user/account/UserAccountLoginView";
import UserAccountRegisterView from "../views/user/account/UserAccountRegisterView";
import store from "../store/index";
const routes = [
    {
        path: "/",
        name: "home",
        redirect: "/battle/",
        meta:{
            requestAuth: true
        },
    },
    {
        path: "/battle/",
        name: "battle_index",
        component: BattleIndexView,
        meta:{
            requestAuth: true
        },
    },
    {
        path: "/record/",
        name: "record_index",
        component: RecordIndexView,
        meta:{
            requestAuth: true
        },
    },
    {
        path: "/ranklist/",
        name: "ranklist_index",
        component: RanklistIndexView,
        meta:{
            requestAuth: true
        },
    },
    {
        path: "/user/bot/",
        name: "user_bot_index",
        component: UserBotIndexView,
        meta:{
            requestAuth: true
        },
    },
    {
        path: "/user/account/login/",
        name: "user_account_login",
        component: UserAccountLoginView,
        meta:{
            requestAuth: false
        },
    },
    {
        path: "/user/account/register",
        name: "user_account_register",
        component: UserAccountRegisterView,
        meta:{
            requestAuth: false
        },
    },
    {
        path: "/404/",
        name: "404",
        component: NotFount,
    },
    {
        path: "/:catchAll(.*)",
        redirect: "/404/",
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) =>{
    let flag = 1;
    const jwt_token = localStorage.getItem("jwt_token");

    if (jwt_token) {
        store.commit("updateToken", jwt_token);
        store.dispatch("getinfo", {
        success() {
        },
        error() {
            router.push({ name: 'user_account_login' });
        }
        })
    } else {
        flag = 2;
    }
    if (to.meta.requestAuth && !store.state.user.is_login) {
        if (flag === 1) {
            next();
        } else {
            next({name: "user_account_login"});
        }
        } else {
        next();
    }
})
export default router
