<template>
    <nav class="navbar navbar-expand-lg bg-dark">
    <div class="container">
        <router-link class="navbar-brand" :to="{name: 'home'}">Bot Crash</router-link>
        <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
            <router-link :class="route_name == 'battle_index' ? 'nav-link active' : 'nav-link'" aria-current="page" :to="{name: 'battle_index'}">Battle</router-link>
            </li>
            <li class="nav-item">
            <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'record_index'}">Battle List</router-link>
            </li>
            <li class="nav-item">
            <router-link :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'ranklist_index'}">Ranking</router-link>
            </li>
        </ul>
        <ul class="navbar-nav" v-if="$store.state.user.is_login">
            <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                {{$store.state.user.username}}
            </a>
            <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                <li><router-link class="dropdown-item" :to="{name: 'user_bot_index'}">My Bot</router-link></li>
                <li><hr class="dropdown-divider"></li>
                <li><a class="dropdown-item" href="#" @click="logout">Logout</a></li>
            </ul>
            </li>
        </ul>

        <ul class="navbar-nav" v-else>
            <li class="nav-item dropdown">
                <router-link class="nav-link" :to="{name: 'user_account_login'}" role="button">
                    Login
                </router-link>
            </li>
            <li class="nav-item dropdown">
                <router-link class="nav-link" :to="{name: 'user_account_register'}" role="button">
                    Register
                </router-link>
            </li>
        </ul>
        </div>
    </div>
    </nav>
</template>

<script>
import { useRoute } from "vue-router";
import { computed } from "vue";
import { useStore } from "vuex";
export default {
    setup(){
        const store = useStore();
        const route = useRoute();
        let route_name = computed(() => route.name);
        const logout = () => {
            store.dispatch("logout");
        }
        return {
            route_name,
            logout
        }
    }
}
</script>

<style scoped>
.nav-item > .nav-link {
    color : gray;
}

.nav-link.active   {
    color : white !important;
    transform: scale(1.2);
}

.nav-item:hover > .nav-link{
    color: white;
    transform: scale(1.1);
    transition-duration: 0.2s;
}

.navbar-text{
    color: gray;
}

.navbar-brand{
    color: gray;
}

.navbar-brand:hover {
    color: white;
}
</style>

