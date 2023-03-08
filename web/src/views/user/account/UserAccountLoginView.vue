<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">username</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="Please enter your username...">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">password</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="Please enter your password...">
                    </div>
                    <div class="error-message mb-3"> {{error_message}}</div>
                    <div class="mb-3">
                        <button type="submit" class="btn btn-primary">Login</button>
                    </div>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import { ref } from 'vue'
import {useStore} from 'vuex'
import router from '@/router';
// import router from '../../../router/index'
// import $ from 'jquery'

export default {
    components: {
        ContentField
    },
    setup()
    {
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_message = ref('');
        const login = () => {
            store.dispatch("login", {
                username: username.value,
                password: password.value,
                success()
                {
                    store.dispatch("getinfo", {
                        success(){
                            router.push({name: "home"});
                            console.log(store.state.user);
                        }
                    })
                },
                error()
                {
                    error_message.value = "Username or password incorrect!";
                }
            });
        }
        return {
            username,
            password,
            error_message,
            login
        };
    }
    
}
</script>

<style scoped>
button {
    width: 100%;
}

div.error-message {
    color: red;
}
</style>
