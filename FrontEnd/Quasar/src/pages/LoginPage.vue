<script setup lang="ts">
import {onUnmounted, ref} from 'vue';
import {api} from "boot/axios";
import {LocalStorage, useQuasar} from 'quasar'
import {useRouter} from "vue-router";
import {getUser, updateUser} from "stores/userStore";

const $q = useQuasar();
let loading = ref(false)
const router = useRouter();

let userName = ref(null);
let password = ref(null);
let email = ref(null);
let isPwd = ref(true);


function getCookie(name: string): string | undefined {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(';').shift();
}

(async () => {
  const token = getCookie('jwt');

  if (token) {
    console.log('JWT Token:', token);
  }

})();

async function login() {
  loading.value = true;
  if((userName.value !=null && userName.value!='') && (password.value != null && password.value!='')){
    try {
      let sendUserLogin = null;
      if (userName.value.includes('@')) {
        email.value = userName.value;
      } else {
        sendUserLogin = userName.value;
      }
      const response = await api.post("auth/login", {
        username: sendUserLogin,
        password: password.value,
        email: email.value,
      });

      if (response.data) {
        updateUser(response.data);
        await router.push('../');
      }
    }
    catch (error) {
      if (error.response.status === 400) {
        $q.notify({
          type: 'negative',
          message: 'Nom d\'utilisateur ou mot de passe incorrect'
        })
      }
      else {
        $q.notify({
          type: 'negative',
          message: 'Une erreur s\'est produite lors de la connexion'
        })
      }
    }
  }
  else {
    $q.notify({
      type: 'negative',
      message: 'Tout les champs ne sont pas remplis'
    })
  }
  loading.value = false;
}

async function loginGoogle() {

  const authWindow = window.open('https://3proj-back.tristan-tourbier.com/api/oauth2/google', '_blank', 'height=600,width=600');

  window.addEventListener('message', (event) => {

    if (event.data.token){
      console.log('Message reçu de la fenêtre enfant :', event.data.token);
      sessionStorage.setItem('userToken', event.data.token);
      window.location.href = '/';
    }

  });
}


</script>

<template>
  <div class="form q-pa-lg q-ma-lg">
    <div class="logo">
      <q-img
        width="100%"
        src="assets/logo-web.webp"
        :ratio="1"
      />
    </div>
    <span class="text-grey-2 text-h5">Connexion</span>
    <q-form  @submit="login">
    <div class="inputs">
      <q-input
        class="input"
        outlined
        v-model="userName"
        icon="user"
        label="Nom d'utilisateur"
        dark
        color="secondary">
        <template v-slot:prepend>
          <q-icon name="person"/>
        </template>
      </q-input>
      <q-input
        dark
        class="input"
        outlined
        v-model="password"
        label="Mot de passe"
        :type="isPwd ? 'password' : 'text'"
        color="secondary">
        <template v-slot:append>
          <q-icon
            :name="isPwd ? 'visibility_off' : 'visibility'"
            class="cursor-pointer"
            @click="isPwd = !isPwd"
          />
        </template>
      </q-input>
      <div class="links">
        <a href="#/register">S'inscrire</a>
        <a href="#/reset-pwd"><b>Mot de passe oublié</b></a>
      </div>
      <q-btn
        class="btn "
        color="secondary"
        text-color="white"
        unelevated
        label="Se connecter"
        type="submit"
        :loading="loading"
      />
      <div class="external-services">
        <q-item-label class="text-secondary">Connexion avec:</q-item-label>

        <div class="btns">
          <q-btn
            @click="loginGoogle"
            class="btn-log shadow-6 bg-white">
            <img src="assets/icons/googleIcon.svg" alt="Google"/>
          </q-btn>
          <q-btn
            @click="console.log('Apple')"
            class="btn-log shadow-6 bg-white">
            <img src="assets/icons/appleIcon.svg" alt="Apple"/>
          </q-btn>
          <q-btn
            @click="console.log('Microsoft')"
            class="btn-log shadow-6 bg-white">
            <img src="assets/icons/microsoftIcon.svg" alt="Microsoft"/>
          </q-btn>
          <q-btn
            @click="console.log('Facebook')"
            class="btn-log shadow-6 bg-white">
            <img src="assets/icons/facebookIcon.svg" alt="Facebook"/>
          </q-btn>
        </div>
      </div>
    </div>
    </q-form>
  </div>
</template>

<style scoped>

.form {
  width: 90%;
  max-width: 400px;
  margin: auto;
  display: flex;
  flex-direction: column;
}

.form .input, .form .logo, .form .external-services {
  margin-top: 20px;
}

.links {
  margin-top: 5px;
  margin-bottom: 25px;
  display: flex;
  justify-content: space-between;
  flex-direction: row;
}

.btn {
  width: 70%;
  height: 32px;
  border-radius: 10px;
  margin: 10px 15%;
}

.text-primary {
  font-family: 'lato', sans-serif;
  font-weight: bold;
}

.links * {
  text-decoration: none;
  color: #ffa31a;
}

.btn-log {
  width: 48px;
  height: 48px;
  margin: 10px 10px;
}

.btn-log img {
  width: 32px;
  height: 32px;
}

.external-services .btns {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-evenly;
}

.logo {
  margin-bottom: 20px;
}

/* RESPONSIVE */
@media screen and (min-width: 600px) {
  .form {
    width: 60%;
  }
}

@media screen and (min-width: 1200px) {
  .form {
    width: 40%;
  }
}
</style>
