<script setup lang="ts">
import {ref} from 'vue';
import {api} from "boot/axios";
import { useQuasar } from 'quasar'
import {useRouter} from "vue-router";
import {updateUser} from "stores/userStore";

const $q = useQuasar();
let loading = ref(false)
const router = useRouter();

let userName = ref(null);
let password = ref(null);
let email = ref(null);



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
        await router.push('/#');
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
</script>

<template>
  <div class="form q-pa-lg q-ma-lg">
    <div class="logo">
      <q-img
        width="100%"
        src="../assets/logo-web.webp"
        :ratio="1"
      />
    </div>
    <span class="text-grey-2 text-h5">Connexion</span>
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
        type="password"
        color="secondary">
        <template v-slot:prepend>
          <q-icon name="key" />
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
        @click="login"
        :loading="loading"
      />
      <div class="external-services">
        <q-item-label class="text-secondary">Connexion avec:</q-item-label>

        <div class="btns">
          <q-btn
            @click="console.log('Google')"
            class="btn-log shadow-6 bg-white">
            <img src="/../src/assets/icons/googleIcon.svg" alt="Google"/>
          </q-btn>
          <q-btn
            @click="console.log('Apple')"
            class="btn-log shadow-6 bg-white">
            <img src="/../src/assets/icons/appleIcon.svg" alt="Apple"/>
          </q-btn>
          <q-btn
            @click="console.log('Microsoft')"
            class="btn-log shadow-6 bg-white">
            <img src="/../src/assets/icons/microsoftIcon.svg" alt="Microsoft"/>
          </q-btn>
          <q-btn
            @click="console.log('Facebook')"
            class="btn-log shadow-6 bg-white">
            <img src="/../src/assets/icons/facebookIcon.svg" alt="Facebook"/>
          </q-btn>
        </div>
      </div>
    </div>
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
  color: #4FA4BB;
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
