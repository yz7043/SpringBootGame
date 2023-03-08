import { createRouter, createWebHistory } from 'vue-router'
import BattleIndexView from "../views/battle/BattleIndexView";
import RanklistIndexView from "../views/ranklist/RanklistIndexView";
import RecordIndexView  from "../views/record/RecordIndexView";
import UserBotIndexView from "../views/user/bots/UserBotIndexView";
import NotFount from "../views/error/NotFount";
import UserAccountLoginView from "../views/user/account/UserAccountLoginView";
import UserAccountRegisterView from "../views/user/account/UserAccountRegisterView";
const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/battle/",
  },
  {
    path: "/battle/",
    name: "battle_index",
    component: BattleIndexView,
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
  },
  {
    path: "/user/account/register",
    name: "user_account_register",
    component: UserAccountRegisterView,
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

export default router
